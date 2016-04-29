
package com.ivli.roim;


import com.ivli.roim.io.LutReader;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Window;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.IndexColorModel;
import java.awt.image.LookupOp;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class VOILut /*implements com.ivli.roim.core.Transformation*/ {    
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = 255.;    
    private static final byte GREYSCALES_MIN = (byte)0x0;
    private static final byte GREYSCALES_MAX = (byte)0xff;
    private final static int BUFFER_SIZE = 65536;
    
    private boolean iInverted;            
    private boolean iLinear;   
    
    private final PValueTransform iPVt;
    private Window iWin;    
        
    private final int[] iBuffer;        
    //private LookupOp iLook; // VOI LUT
    //protected IndexColorModel iModel; //presentation LUT

    private final int [][]lut = new int[256][3];
   
    
    public VOILut(PValueTransform aPVT, Window aWin, String aLUTcanBeNull) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
        iBuffer = new int[BUFFER_SIZE];
        iWin = aWin;     
        setLUT(aLUTcanBeNull);        
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
       
        byte reds[] = new byte[256];
        byte greens[] = new byte[256];
        byte blues[] = new byte[256];
        mdl.getReds(reds);
        mdl.getGreens(greens);
        mdl.getBlues(blues);    
        
        for (int i=0;i<256; ++i) {
            lut[i][0] = (int)(reds[i]); 
            lut[i][1] = (int)(greens[i]);
            lut[i][2] = (int)(blues[i]);
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
       
    
    public BufferedImage transform(ImageFrame aSrc, BufferedImage aDst) {//BufferedImage aSrc, BufferedImage aDst) {        
        final int width = aSrc.getWidth();
        final int height = aSrc.getHeight();
                
        if (null == aDst || aDst.getWidth() != width || aDst.getHeight() != height) 
            aDst = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        final WritableRaster dst = aDst.getRaster();        
        //final Raster src = (iLook.filter(aSrc, aDst)).getRaster();        
        
        for (int y=0; y < height; ++y) {
            for (int x=0; x < width; ++x) {
               final int ndx = 0x0ff & (iBuffer[aSrc.get(x, y)]);
               /*
               final int sample = iModel.getRGB(ndx); 
               final int[] rgb = {(sample&0x00ff0000)>>16, (sample&0x0000ff00)>>8, sample&0x000000ff};
               /* */
               
               ///final int[] rgb = ;//{(int)(reds[ndx]), (int)(greens[ndx]), (int)(blues[ndx])};
               /* */
               dst.setPixel(x, y, lut[ndx]);               
            }
        }
        
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
