
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
//import org.jfree.data.xy.XYSeries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class VOILut implements com.ivli.roim.core.Transformation {
    private boolean iInverted;            
    private boolean iLinear;   
    
    private final PValueTransform iPVt;
    private Window iWin;
    private Range iRange; 
    
    private final Buffer iBuffer;
    private LookupOp iLook; 
        
    public VOILut(ImageFrame aI) {
        iInverted = false;
        iLinear = true;
        iPVt = new PValueTransform();
        iBuffer = new Buffer(DataBuffer.TYPE_USHORT);  //!!!-> stub
        iLook = null;
        reset(new Range(aI.getMin(), aI.getMax()));
    }  
    
    public void setRange(Range aR) {
        reset(aR);
    }
    
    public Range getRange() {
        return iRange;
    }
            
    public void setWindow(Window aW) {           
        if (iRange.contains(aW)) {//tmp.getBottom()>=iMax.getBottom() && tmp.getTop()<=iMax.getTop()) {
            iWin.setWindow(aW);
            updateLUT();
        }
    }
    
    public Window getWindow() {
        return iWin;
    }
    
    public void setInverted(boolean aI) {
        if (isInverted() != aI) {
            iInverted = aI;    
            updateLUT();
        }
    }
    
    public boolean isInverted() {
        return iInverted;
    }

    public void setLinear(boolean aL) {
        if (isLinear() != aL) { 
            iLinear = aL;
            updateLUT();   
        }       
    }

    public boolean isLinear() {
        return iLinear;
    }
        
    private void updateLUT() {
        iLook = null;
    }
    
     @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        if (null == iLook)
            makeLUT();
        return iLook.filter(aSrc, aDst);//null == aDst ? iLok.createCompatibleDestImage(aSrc, iCMdl):aDst);	
    }
    
    public com.ivli.roim.core.Curve makeXYSeries() {
        final int minval = (int)getRange().getMin();//int)(getWindow().getBottom());
        final int maxval = (int)getRange().getMax();//(getWindow().getTop());
        
        com.ivli.roim.core.Curve ret = new com.ivli.roim.core.Curve();        
        
        int[] buf = new int[maxval - minval]; 
        for (int i = minval; i < maxval; ++i)
            ret.put(i, (Integer)(iBuffer.bytes[i] & 0xFF));
            //buf[i] = iBuffer.bytes[i] & 0xFF;
        
        return ret;
    }   
    
    /* EOI */
    
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = 255.;
    
    private static final byte GREYSCALES_MIN = (byte)0x0;
    private static final byte GREYSCALES_MAX = (byte)0xff;
    private static final int  GREYSCALES = 255;
    
    private void makeLogarithmic() {                  
        for (int i = 0; i < iBuffer.bytes.length; ++i) {            
            final double val = (1 + Math.exp(-4*(iPVt.transform(iBuffer.min + i) - iWin.getLevel()) / iWin.getWidth())) + LUT_MIN + 0.5;
            final double y = Ranger.range(LUT_RANGE / val, LUT_MIN, LUT_MAX);
            
            iBuffer.bytes[i]=(byte)(isInverted() ? (LUT_MAX - y) : y);           
        }
    }    
    
    interface functor {
        byte function(double i);
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

    private void doMakeLUT(functor f) {  
        
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
            doMakeLUT(new functor() { 
                public byte function(double PV) {
                    return (byte)(((PV - iWin.getLevel())/iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
                }});               
        else 
            doMakeLUT(new functor() { 
                public byte function(double PV) {
                    return (byte)(LUT_RANGE/(1 + Math.exp(-4*(PV - iWin.getLevel()) / iWin.getWidth()) + LUT_MIN));
                }});
              
        
        iLook = new LookupOp(new ByteLookupTable(0, iBuffer.bytes), null);
        /**/
        
        String fname = String.format("%s_%.0f-%.0f_%.0f-%.0f.csv", isLinear() ? "Lin" : "Log", 
                                                                    getWindow().getLevel(), getWindow().getWidth(),
                                                                        iRange.getMin(), iRange.getMax());
                                    
        /*
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
        */
    }
  
    private void reset(Range aR) {            
        if(Settings.get(Settings.KEY_PRESERVE_WINDOW, false) && null != iWin && null != iRange) {                    
            final double scale = aR.range() / iRange.range();           
            final double bottom = iWin.getBottom() * scale;
            final double range  = (iWin.getTop() - iWin.getBottom()) * scale; 
            iRange = aR;            
            iWin = new Window(bottom + range / 2.0, range);                        
        } else {
            iRange = aR;
            iWin = new Window(aR);
        }
        
        updateLUT();                  
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
                min = -32768;
                max = 32768; 
                bytes = new byte[65536]; 
                break;
            default:
                throw new IllegalArgumentException();                  
        }             
    }
}