package com.ivli.roim.view;

import com.ivli.roim.algorithm.Algorithm;
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
    private static final int GREYSCALES_MIN = 0;
    private static final int GREYSCALES_MAX = 255;
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
               dst.setPixel(x, y, iPlut.translate(iBuffer[aSrc.get(x, y)]));               
                
        return aDst;
    }
      
    private int linVal(double PV) {  
        if (PV <= iWin.getBottom()) 
            return GREYSCALES_MIN;
        else if (PV > iWin.getTop()) 
            return GREYSCALES_MAX;
        else  
            return (int)(LUT_RANGE *((PV - iWin.getLevel()) / iWin.getWidth() + .5) + LUT_MIN);
    }  
    
    private int logVal(double aV) {               
        return (int)(LUT_RANGE /(1 + Math.exp(-4 * (aV - iWin.getLevel()) / (iWin.getWidth()))) + LUT_MIN);
    }
 
    private void makeLUT() {             
        for (int i=0; i<iBuffer.length; ++i) {          
            final double PV = iPVt.transform(i);
            
            if (!isLinear()){    
                iBuffer[i] = logVal(PV);
            } else {                              
                if (!isInverted())                
                    iBuffer[i] = linVal(PV);  
                else
                    iBuffer[i] = (GREYSCALES_MAX - linVal(PV));                                                                                                   
            }   
        }
    }       
    /*
    private void makeLUT2() {          
        final byte maxval; 
        final byte minval;    
        
        if (isInverted()) {
            maxval = GREYSCALES_MIN;
            minval = GREYSCALES_MAX;
        } else {
            minval = GREYSCALES_MIN;
            maxval = GREYSCALES_MAX;
        }
               
        final double[] x = {iWin.getBottom() + .5, iWin.getTop()};
        final double[] y = {.5, 255.};
        final Function<Double, Double> F1 = Algorithm.leastsquares(x, y, 0, x.length, isLinear() ? Algorithm.TYPE.LINEAR:Algorithm.TYPE.EXPONENTIAL);        
        final Function<Double, Double> F = isInverted() ? (Double a)->GREYSCALES_MAX - F1.apply(a) : F1;
                        
        for (int i=0; i<iBuffer.length; ++i) {          
            final double PV = iPVt.transform(i);
              /*          
            if (PV <= iWin.getBottom()) 
                iBuffer[i] = minval;
            else if (PV > iWin.getTop()) 
                iBuffer[i] = maxval;
            else {                          
                iBuffer[i] = (int)Math.round(F.apply(PV));                
          //  }                        
        }        
    }       
    */
    public Curve getCurve() {        
        Curve ret = new Curve();        
         
        for (int i=0; i<iBuffer.length; ++i)
            ret.put(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }      
} 
