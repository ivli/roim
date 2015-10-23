
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
    private boolean   iInverted;            
    private boolean        iLog;
    private boolean iKeepWindow = true;     
    
    private final PValueTransform iPVt;
    private final Buffer       iBuffer;
    private LookupOp iLok; 
    private Window iWin;
    private Range iRange; 
    
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
    
       if(iKeepWindow && null != iWin && null != iRange) {        
            final double percentTop    = iWin.getTop() / iRange.getWidth();
            final double percentBottom = iWin.getBottom() / iRange.getWidth();
            iRange = aR;
            iWin   = new Window(new Range(percentBottom * iRange.getWidth(), percentTop * iRange.getWidth()));
            //iWin.setTop(percentTop * iRange.getWidth()); 
            //iWin.setBottom(percentBottom * iRange.getWidth()); 
        } else {
            iRange = aR;
            iWin = new Window(aR);
       }
        
        makeLUT();            
    }
      
    public void setWindow(Window aW) {       
        //final Window tmp = new Window(aW.getLevel(), aW.getWidth());
        
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
    private static final double LUT_RANGE = LUT_MAX - LUT_MIN;

    private final void makeLogarithmic() {   
       
        for (int i = 0; i < iBuffer.length; ++i) {
            double y = Ranger.range(LUT_RANGE / (1 + Math.exp(-4*(iPVt.transform(iBuffer.min + i) - iWin.getLevel())/iWin.getWidth())) + LUT_MIN + 0.5, LUT_MIN, LUT_MAX);
            iBuffer.bytes[i]=(byte)(isInverted() ? (LUT_MAX - y) : y);
        }

        iLok  = new LookupOp(new ByteLookupTable(0, iBuffer.bytes), null);	
    }
    
    private static final int GREYSCALES = 256;
    private static final int GREYSCALES_MAX = 255;
    private static final int GREYSCALES_MIN = 0;
    
    private void makeLinear() {  
        final double m = GREYSCALES / iWin.getWidth();
        final double b = m*iWin.getBottom();
        final byte max = (byte)(isInverted() ? GREYSCALES_MIN:GREYSCALES_MAX);
        final byte min = (byte)(isInverted() ? GREYSCALES_MAX:GREYSCALES_MIN);
        
        for (int x=0; x < iBuffer.length; ++x) {
            if (x <= iWin.getBottom()) 
                iBuffer.bytes[x] = min;
            else if (x > iWin.getTop()) 
                iBuffer.bytes[x] = max;
            else {
                final double y = m * iPVt.transform(x) - b;
                iBuffer.bytes[x] = (byte) (isInverted()? max - y : y);
            }
        }

        iLok = new LookupOp(new ByteLookupTable(0, iBuffer.bytes), null);
    }
    
    private void makeLinear2() {  	
        for (int i = 0; i < iBuffer.length; ++i) {
            double y = iPVt.transform(i-iBuffer.min);

            if (y <= iWin.getBottom()) y = LUT_MIN;
            else if (y > iWin.getTop()) y = LUT_MAX;
            else {
                y = (((y - iWin.getLevel())/iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
            }

            iBuffer.bytes[i] = (byte)(isInverted() ? LUT_MAX - y : y);
        }

        iLok = new LookupOp(new ByteLookupTable(0, iBuffer.bytes), null);	
    }

    private void makeLUT() {   
        
        logger.info((isLinear() ? "linear, ":"logarithmic, ") + (isInverted() ? "inverted, ":"direct, ") + "level=" + iWin.getLevel() + ", width=" + iWin.getWidth()); //NOI18N
        
        if (isLinear())
            makeLinear2();   
        else 
            makeLogarithmic();     
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
    final byte []bytes;
    final int  length;
    final int  min;
    final int  max;

    public Buffer(int T) {
        
        switch (T) {
            case DataBuffer.TYPE_BYTE:
                min = -128;
                max = 128;
                length = 256; 
                break;
            
            case DataBuffer.TYPE_USHORT: 
                min =  0;
                max = 65536;
                length = 65536;
                break;
                
            case DataBuffer.TYPE_SHORT: 
                min =  -32768;
                max =   32768; 
                length = 65536; break;
            default:
                throw new IllegalArgumentException();                  
        }     
        bytes = new byte[length]; 
    }
}