/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;


import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.DataBuffer;
import java.awt.image.LookupOp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.data.xy.XYSeries;


class VOILut implements LutTransform {
    private boolean   iInverted;            
    private boolean        iLog;
    private boolean iKeepWindow; 
    
    
    private PValueTransform iPVt;
    private LutBuffer    iBuffer;
    private LookupOp        iLok; 
    private Window          iWin;
    private Window          iMax;
    
    
    public VOILut(/*MEDImage aI*/) { 
        //iPVt = new PValueTransform();
        ///iBuffer = new LutBuffer(aI.getBufferedImage().getSampleModel().getDataType());
        ///reset(aI);
    }
        
    public void setImage(MEDImageBase aI) {
        reset(aI);
    }
    
    public Window getWindow() {return iWin;}
    public Window getRange() {return iMax;}
    
    
    private void reset(MEDImageBase aI) {
        
        iPVt = new PValueTransform();
        iBuffer = new LutBuffer(aI.getBufferedImage().getSampleModel().getDataType());
        
        final double min = aI.getImageStats().iMin;
        final double max = aI.getImageStats().iMax;
        
        
        if (null == iWin || !iKeepWindow) {
            iMax = new Window(min + (max - min) / 2., max - min);
            iWin = new Window(iMax); 
        } else {
            final double percentTop = iWin.getTop()/iMax.getWidth();
            final double percentBottom = iWin.getBottom()/iMax.getWidth();
            iMax = new Window(min + (max - min) / 2., max - min);
            iWin = new Window(iMax);//percentTop * iMax.getWidth()/2, (percentTop - percentBottom) * iMax.getWidth()); 
            iWin.setTop(percentTop*iMax.getWidth()); 
            iWin.setBottom(percentBottom*iMax.getWidth()); 
        }    
               
        
        makeLUT();            
    }
      
    public void setWindow(Window aW) {       
        final Window tmp = new Window(aW.getLevel(), aW.getWidth());
        
        if (tmp.getBottom()>=iMax.getBottom() && tmp.getTop()<=iMax.getTop()) {
            iWin.setWindow(aW.getLevel(), aW.getWidth());
            makeLUT();
        }
    }
    
    public boolean isInverted() {return iInverted;}

    public boolean setInverted(boolean aI) {
        if (aI != isInverted()) {
            iInverted = aI;    
            makeLUT();
            return true;
        }
        return false;
    }

    public boolean isLinear() {return true!=iLog;}
    
    public void setLinear(boolean aL) {
            iLog = !aL;
            makeLUT();    
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
    
    private static final int GREYSCALES = 255;
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


final class LutBuffer {
    final byte [] bytes;
    final int  length;
    final int  min;
    final int  max;

    public LutBuffer(int aType) {
        boolean Signed=false;
        switch (aType) {
            case DataBuffer.TYPE_SHORT:
                Signed = true;
            case DataBuffer.TYPE_USHORT:
                min = Signed ? -32768 : 0;
                max = Signed ?  32768 : 65536;
                bytes = new byte[length = 65536];
                break;
            case DataBuffer.TYPE_BYTE:
                min = Signed ? -128 : 0;
                max = Signed ?  128 : 256;
                bytes = new byte[length = 256]; 
                break;
            default:
                throw new IllegalArgumentException();                  
        }                
    }
}