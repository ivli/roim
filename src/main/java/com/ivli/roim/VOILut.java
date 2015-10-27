
package com.ivli.roim;


import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Ranger;
import com.ivli.roim.core.Range;
import com.ivli.roim.core.Window;
import com.ivli.roim.core.ImageFrame;

import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.DataBuffer;
import java.awt.image.LookupOp;
import org.jfree.data.xy.XYSeries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class VOILut implements com.ivli.roim.core.Transformation {
    private boolean iInverted;            
    private boolean iLog;   
    
    private final PValueTransform iPVt;
    private Window iWin;
    private Range iRange; 
    
    private final Buffer iBuffer;
    private LookupOp iLok; 
    
    
    public VOILut(ImageFrame aI) {
        iPVt = new PValueTransform();
        iBuffer = new Buffer(aI.getRaster().getSampleModel().getDataType());
                
        reset(new Range(aI.getMin(), aI.getMax()));
    }  
          
    public Window getWindow() {
        return iWin;
    }
    
    public Range getRange() {
        return iRange;
    }
        
    public void setRange(Range aR) {
        reset(aR);
    }
     
    private void reset(Range aR) {            
       if(Settings.KEEP_WINDOW_AMONG_FRAMES && null != iWin && null != iRange) {        
            final double percentTop    = iWin.getTop() / iRange.getWidth();
            final double percentBottom = iWin.getBottom() / iRange.getWidth();
            iRange = aR;
            
            final double newTop    = percentTop * iRange.getWidth();
            final double newBottom = percentBottom * iRange.getWidth();
            final double newRange  = newTop - newBottom; 
            
            iWin = new Window(newBottom + newRange / 2.0, newRange);
            
        } else {
            iRange = aR;
            iWin = new Window(aR);
        }
        
        makeLUT();            
    }
      
    public void setWindow(Window aW) {           
        if (iRange.contains(aW)) {//tmp.getBottom()>=iMax.getBottom() && tmp.getTop()<=iMax.getTop()) {
            iWin.setWindow(aW);
            makeLUT();
        }
    }
    
    public boolean isInverted() {
        return iInverted;
    }

    public boolean setInverted(boolean aI) {
        if (aI != isInverted()) {
            iInverted = aI;    
            makeLUT();
            return true;
        }
        return false;
    }

    public boolean isLinear() {
        return true!=iLog;
    }
    
    public boolean setLinear(boolean aL) {
        if (iLog == aL) { // LOG == 1 but we set LINEAR == 1
            iLog = !aL;
            makeLUT();   
            return true;
        }
        return false;
    }
    
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = 255.;
    
    private static final byte GREYSCALES_MIN = (byte)0x0;
    private static final byte GREYSCALES_MAX = (byte)0xff;
    private static final int  GREYSCALES = 255;
    
    private void makeLogarithmic2() {                  
        for (int i = 0; i < iBuffer.bytes.length; ++i) {            
            final double val = (1 + Math.exp(-4*(iPVt.transform(iBuffer.min + i) - iWin.getLevel()) / iWin.getWidth())) + LUT_MIN + 0.5;
            final double y = Ranger.range(LUT_RANGE / val, LUT_MIN, LUT_MAX);
            
            iBuffer.bytes[i]=(byte)(isInverted() ? (LUT_MAX - y) : y);           
        }
    }
    
    
    interface functor {
        byte function(double i);
    }
    
    private void makeLogarithmic() {          
        for (int i = 0; i < iBuffer.bytes.length; ++i) {          
            final double PV = iPVt.transform(i - iBuffer.min);
            byte y;
            
            if (PV <= iWin.getBottom()) 
                y = GREYSCALES_MIN;
            else 
                if (PV > iWin.getTop()) 
                    y = GREYSCALES_MAX;
            else {
                y = (byte)((1 + Math.exp(-4*(PV - iWin.getLevel()) / iWin.getWidth())) + LUT_MIN + 0.5);
            }
            
            iBuffer.bytes[i] = (byte)(isInverted() ? GREYSCALES_MAX - y : y);
        }        	
    }
    
    
    private void makeLinear2() {          
        for (int i = 0; i < iBuffer.bytes.length; ++i) {          
            final double PV = iPVt.transform(i-iBuffer.min);
            byte y;
            
            if (PV <= iWin.getBottom()) 
                y = GREYSCALES_MIN;
            else 
                if (PV > iWin.getTop()) 
                    y = GREYSCALES_MAX;
            else {
                y = (byte)(((PV - iWin.getLevel())/iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
            }
            
            iBuffer.bytes[i] = (byte)(isInverted() ? GREYSCALES_MAX - y : y);
        }        	
    }

    private void makeLUT(functor f) {  
        
        final byte maxval; 
        final byte minval;    
        
        if (isInverted()) {
            maxval = GREYSCALES_MIN;
            minval = GREYSCALES_MAX;
        } else {
            minval = GREYSCALES_MIN;
            maxval = GREYSCALES_MAX;
        }
                    
        
        for (int i = 0; i < iBuffer.bytes.length; ++i) {          
            final double PV = iPVt.transform(iBuffer.min + i);
                        
            if (PV <= iWin.getBottom()) 
                iBuffer.bytes[i] = minval;
            else 
                if (PV > iWin.getTop()) 
                    iBuffer.bytes[i] = maxval;
            else {
                if(isInverted())                
                    iBuffer.bytes[i] = (byte)(GREYSCALES_MAX - f.function(PV));
                else
                    iBuffer.bytes[i] = f.function(PV);
            }                        
        }        	
    }
    
    
    
    private void makeLUT() {   
        
        logger.info((isLinear() ? "linear, ":"logarithmic, ") + (isInverted() ? "inverted, ":"direct, ") + "level=" + iWin.getLevel() + ", width=" + iWin.getWidth()); //NOI18N
        
        if (isLinear())
            makeLUT(new functor() { 
                public byte function(double PV) {
                    return (byte)(((PV - iWin.getLevel())/iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
                }});               
        else 
            makeLUT(new functor() { 
                public byte function(double PV) {
                    return (byte)(LUT_RANGE/(1 + Math.exp(-4*(PV - iWin.getLevel()) / iWin.getWidth())) + LUT_MIN);
                }});
              
        
        iLok = new LookupOp(new ByteLookupTable(0, iBuffer.bytes), null);
        /**/
        
        String fname = String.format("%s_%.0f-%.0f_%.0f-%.0f.csv", isLinear() ? "Lin" : "Log", 
                                                                    getWindow().getLevel(), getWindow().getWidth(),
                                                                        iRange.getMin(), iRange.getMax());
                                    
        
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fname)) {
            try(java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fos)) {            
                                 
                class Formatter {
                    String format(byte[] a) {
                         //the code snatched from java.utils.Array  
                        int iMax = a.length - 1;
                        if (iMax == -1)
                            return "[]";

                        StringBuilder b = new StringBuilder();
                        b.append('[');
                        for (int i = 0; ; i++) {
                            b.append(String.valueOf(((int)a[i]) & 0xff));
                            if (i == iMax)
                                return b.append(']').toString();
                            b.append(", ");
                        }
                    }
                }
                
                String s = new Formatter().format(iBuffer.bytes);
                
                oos.writeObject(s); 

                oos.close();
                fos.close();
            }
        } catch (java.io.IOException ex) {
            
        } 
        
    }
  
    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        if (null == iLok)
            makeLUT();
        return iLok.filter(aSrc, aDst);//null == aDst ? iLok.createCompatibleDestImage(aSrc, iCMdl):aDst);	
    }
    
    public XYSeries makeXYSeries(XYSeries ret) {
        final int minval = (int)(getWindow().getBottom());
        final int maxval = (int)(getWindow().getTop());
                
        for (int i=minval; i<maxval; ++i)
            ret.add(i, (short)(iBuffer.bytes[i] & 0xFF));
        
        return ret;
    }   
    
    private static final Logger logger = LogManager.getLogger(VOILut.class);
} 

final class Buffer {
    final int min;
    final int max;
    
    final byte []bytes;    
    

    public Buffer(int T) {
        
        switch (T) {
            case DataBuffer.TYPE_BYTE:
                min = -128;
                max = 128;
                bytes = new byte[256]; 
                break;
            
            case DataBuffer.TYPE_USHORT: 
                min =  0;
                max = 65536;
                bytes = new byte[65536] ;
                break;
                
            case DataBuffer.TYPE_SHORT: 
                min =  -32768;
                max =   32768; 
                bytes = new byte[65536]; 
                break;
            default:
                throw new IllegalArgumentException();                  
        }             
    }
}