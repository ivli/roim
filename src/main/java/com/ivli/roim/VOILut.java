
package com.ivli.roim;


import com.ivli.roim.core.Curve;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Window;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;

public class VOILut implements com.ivli.roim.core.Transformation {    
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
        
    private final byte[] iBuffer;        
    private LookupOp iLook; 
        
    public VOILut(PValueTransform aPVT, Window aWin) {//, Range aR) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
        iBuffer = new byte[BUFFER_SIZE];
        iLook = null;
        iWin = aWin;
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
        iLook = null;
    }
    
    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        if (null == iLook)
            makeLUT();
        return iLook.filter(aSrc, aDst);	
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
        
        iLook = new LookupOp(new ByteLookupTable(0, iBuffer), null);        
    }       
    
    public Curve getCurve() {        
        Curve ret = new Curve();        
         
        for (int i = 0; i < iBuffer.length; ++i)
            ret.put(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }      
} 
