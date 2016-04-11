
package com.ivli.roim;


import com.ivli.roim.core.Curve;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Range;
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
    private Range  iRange; 
        
    private final byte[] iBuffer;        
    private LookupOp iLook; 
        
    public VOILut(PValueTransform aPVT, Range aR) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
        iBuffer = new byte[BUFFER_SIZE];
        iLook = null;
        reset(aR);
    }  
    
    public void setRange(Range aR) {
        reset(aR);
    }
    
    public Range getRange() {
        return iRange;
    }
            
    public void setWindow(Window aW) {           
        if (iRange.contains(aW)) {
            iWin.setWindow(aW);
            updateLUT();
        }
    }
    
    public Window getWindow() {
        return iWin;
    }
    
    public void setInverted(boolean aI) {       
        iInverted = aI;    
        updateLUT();   
    }
    
    public boolean isInverted() {
        return iInverted;
    }

    private void updateLUT() {
        iLook = null;
    }
    
    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        if (null == iLook)
            makeLUT();
        return iLook.filter(aSrc, aDst);	
    }
   
    private byte makeLinear(double PV) {                
        return (byte)(((PV - iWin.getLevel())/iWin.getWidth() + .5) * LUT_RANGE + LUT_MIN);
    }  
    
    private byte makeLogarithmic(double PV) {               
        return (byte)(LUT_RANGE/(1 + Math.exp(-4*(PV - iWin.getLevel()) / iWin.getWidth()) + LUT_MIN));
    }
       
    public void setLinear(boolean aL) {
        iLinear = aL;                    
        updateLUT();            
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
  
    private void reset(Range aR) {            
        if(null != iWin && null != iRange) {                    
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
        
    public Curve getCurve() {
        final int minval = (int)getRange().getMin();
        final int maxval = (int)getRange().getMax();
        
        Curve ret = new Curve();        
         
        for (int i = minval; i < maxval; ++i)
            ret.put(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }      
} 
