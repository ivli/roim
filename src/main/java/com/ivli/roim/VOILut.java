
package com.ivli.roim;


import com.ivli.roim.io.LutReader;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Window;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class VOILut {    
    
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = LUT_MAX - LUT_MIN;    
    private static final byte GREYSCALES_MIN = (byte)0x0;
    private static final byte GREYSCALES_MAX = (byte)0xff;
    private static final int  IMAGESPACE_SIZE = 65536;
    private static final int  LUT_SIZE = 256;
    
    
    
    private PValueTransform iPVt;
    private Window iWin;    
    private boolean iInverted;            
    private boolean iLinear; 
    
     // lut table to convert image space values to greyscale 0-255
    private final int []iBuffer;   
     // lut table to convert greyscale 0-255 to RGB
    private final int [][]iLutBuffer;
    
    /*
     *
     */
    public VOILut(PValueTransform aPVT, Window aWin, String aLUTcanBeNull) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
        iBuffer = new int[IMAGESPACE_SIZE];
        iLutBuffer = new int[LUT_SIZE][3];
        iWin = aWin;     
        setLUT(aLUTcanBeNull);        
    }  
    
    public VOILut(String aLUTcanBeNull) {
        iInverted = false;
        iLinear = true;
        iPVt = PValueTransform.DEFAULT_TRANSFORM;        
        iBuffer = new int[IMAGESPACE_SIZE];
        iLutBuffer = new int[LUT_SIZE][3];
        iWin = new Window(0, IMAGESPACE_SIZE);     
        
        setLUT(aLUTcanBeNull);        
    }  
    
    public void setWindow(Window aW, PValueTransform aT) {
        if (null != aT)
            iPVt = aT;
        setWindow(aW);
    }
    
    public final void setLUT(String aName) { 
        IndexColorModel mdl;
    
        try {
            if (null != aName)
                mdl = LutReader.open(aName);   
            else
                mdl = LutReader.defaultLUT();            
        } catch (IOException ex) {
            mdl = LutReader.defaultLUT();
        }
       
        byte reds[] = new byte[LUT_SIZE];
        byte greens[] = new byte[LUT_SIZE];
        byte blues[] = new byte[LUT_SIZE];
        mdl.getReds(reds);
        mdl.getGreens(greens);
        mdl.getBlues(blues);    
        
        for (int i = 0; i < LUT_SIZE; ++i) {
            iLutBuffer[i][0] = (int)(reds[i]); 
            iLutBuffer[i][1] = (int)(greens[i]);
            iLutBuffer[i][2] = (int)(blues[i]);
        }
    }
        
    public void setWindow(Window aW) {              
        iWin = aW;    
        invalidateLUT();
    }
    
    public Window getWindow() {
        return iWin;
    }
    
    public void setInverted(boolean aI) {       
        iInverted = aI;    
        invalidateLUT();   
    }
    
    public boolean isInverted() {
        return iInverted;
    }

    private void invalidateLUT() {    
        makeLUT();
    }
    
    public BufferedImage transform(ImageFrame aSrc, BufferedImage aDst) {
        final int width = aSrc.getWidth();
        final int height = aSrc.getHeight();
                        
        if (null == aDst || aDst.getWidth() != width || aDst.getHeight() != height || aDst.getType() != BufferedImage.TYPE_INT_RGB) 
            aDst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        final WritableRaster dst = aDst.getRaster();        
                      
        for (int y=0; y < height; ++y) 
            for (int x=0; x < width; ++x)                          
               dst.setPixel(x, y, iLutBuffer[0x0ff & (iBuffer[aSrc.get(x, y)])]);               
                
        return aDst;
    }
   
    private byte makeLinear(double PV) {                
        return (byte)(((PV - iWin.getLevel()) / iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
    }  
    
    private byte makeLogarithmic(double PV) {               
        return (byte)(LUT_RANGE/(1 + Math.exp(-4*(PV - iWin.getLevel()) / iWin.getWidth()) + LUT_MIN));
    }
       
    public void setLinear(boolean aL) {
        iLinear = aL;                    
        invalidateLUT();            
    }

    public boolean isLinear() {
        return iLinear;
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
                            
        for (int i = 0; i < iBuffer.length; ++i) {          
            final double PV = iPVt.transform(i);
                        
            if (PV <= iWin.getBottom()) 
                iBuffer[i] = minval;
            else if (PV > iWin.getTop()) 
                    iBuffer[i] = maxval;
            else {   
                final byte BV = isLinear() ?  makeLinear(PV) : makeLogarithmic(PV);
               
                if(isInverted())                
                    iBuffer[i] = (byte)(GREYSCALES_MAX - BV);
                else
                    iBuffer[i] = BV;
            }                        
        }  
        
//        iLook = new LookupOp(new ByteLookupTable(0, iBuffer), null);        
    }       
    
    public Curve getCurve() {        
        Curve ret = new Curve();        
         
        for (int i = 0; i < iBuffer.length; ++i)
            ret.put(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }      
} 
