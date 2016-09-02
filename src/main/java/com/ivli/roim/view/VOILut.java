package com.ivli.roim.view;

import com.ivli.roim.core.Curve;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.PresentationLUT;
import com.ivli.roim.core.Window;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.function.Function;

public class VOILut {        
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = LUT_MAX - LUT_MIN;    
    private static final byte GREYSCALES_MIN = (byte)0x0;
    private static final byte GREYSCALES_MAX = (byte)0xff;
    private static final int  IMAGESPACE_SIZE = 65536;
    private static final int  LUT_SIZE = 256;        
    
    private PValueTransform iPVt;
    private PresentationLUT iPlut;
    private Window iWin;    
    private boolean iInverted;            
    private boolean iLinear; 
    
     // lut table to convert image space values to greyscale 0-255
    private final int []iBuffer;   
     // lut table to convert greyscale 0-255 to RGB
    ///private final int [][]iLutBuffer;
   
    public VOILut(PValueTransform aPVT, Window aWin, PresentationLUT aLUT) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
        iBuffer = new int[IMAGESPACE_SIZE];
        iPlut = (null !=aLUT) ? aLUT : PresentationLUT.create(null);
        iWin = aWin;                  
    }  
    
    public VOILut(PresentationLUT aLUT) {
        iInverted = false;
        iLinear = true;
        iPVt = PValueTransform.DEFAULT_TRANSFORM;        
        iBuffer = new int[IMAGESPACE_SIZE];       
        iPlut = (null !=aLUT) ? aLUT : PresentationLUT.create(null);
        iWin = new Window(0, IMAGESPACE_SIZE);                        
    }  
    
    public void setTransform(PValueTransform aT) {
        if (null != aT)
            iPVt = aT;
        makeLUT();
    }
    
    public final void setLUT(PresentationLUT aLUT) { 
        iPlut = (null !=aLUT) ? aLUT : PresentationLUT.create(null);;
        makeLUT();
    }
        
    public void setWindow(Window aW) {              
        iWin = aW;    
        makeLUT();
    }
    
    public Window getWindow() {
        return iWin;
    }
    
    public void setInverted(boolean aI) {       
        iInverted = aI;    
        makeLUT();   
    }
    
    public boolean isInverted() {
        return iInverted;
    }
    
    public void setLinear(boolean aL) {
        iLinear = aL;                    
        makeLUT();            
    }

    public boolean isLinear() {
        return iLinear;
    }
    
    //TODO: rewrite to work with ImageFrame instead of BufferedImage
    public BufferedImage transform(ImageFrame aSrc, BufferedImage aDst) {
        final int width = aSrc.getWidth();
        final int height = aSrc.getHeight();
                        
        if (null == aDst || aDst.getWidth() != width || aDst.getHeight() != height || aDst.getType() != BufferedImage.TYPE_INT_RGB) 
            aDst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        final WritableRaster dst = aDst.getRaster();        
                      
        for (int y=0; y < height; ++y) 
            for (int x=0; x < width; ++x)                          
               dst.setPixel(x, y, iPlut.translate(0x0ff & (iBuffer[aSrc.get(x, y)])));               
                
        return aDst;
    }
      
    private void makeLUT() {          
        final byte maxval; 
        final byte minval;    
        
        if (isInverted()) {
            maxval = GREYSCALES_MIN;
            minval = GREYSCALES_MAX;
        } else {
            minval = GREYSCALES_MIN;
            maxval = GREYSCALES_MAX;
        }
       
        final Function<Double, Integer> F;
        if(isLinear()) { 
            F = (Double aV) -> (int)(((aV - iWin.getLevel()) / iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
        } else { 
            F = (Double aV) -> (int)(LUT_RANGE/(1 + Math.exp(-4*(aV - iWin.getLevel()) / iWin.getWidth()) + LUT_MIN)); 
        }
                        
        for (int i=0; i<iBuffer.length; ++i) {          
            final double PV = iPVt.transform(i);
                        
            if (PV <= iWin.getBottom()) 
                iBuffer[i] = minval;
            else if (PV > iWin.getTop()) 
                iBuffer[i] = maxval;
            else {          
                if (isInverted())                
                    iBuffer[i] = (byte)(GREYSCALES_MAX - F.apply(PV));
                else
                    iBuffer[i] = F.apply(PV);                
            }                        
        }        
    }       
    
    public Curve getCurve() {        
        Curve ret = new Curve();        
         
        for (int i=0; i<iBuffer.length; ++i)
            ret.put(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }      
} 
