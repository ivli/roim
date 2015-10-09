
package com.ivli.roim.events;

import com.ivli.roim.Window;

/**
 *
 * @author likhachev
 */
public final class WindowChangeEvent extends java.util.EventObject {   
    private final boolean iRangeChanged;
    private final double  iMin;
    private final double  iMax;
    private final Window  iWindow; 
    
    public WindowChangeEvent(Object aO, Window aW, double aMin, double aMax, boolean aRC) {
        super(aO); 
        iWindow = aW;
        iMin = aMin;
        iMax = aMax;
        iRangeChanged = aRC;      
    }
    
    public WindowChangeEvent(Object aO, Window aW) {
        super(aO); 
        iWindow = aW;
        iMin = 0;
        iMax = 256;
        iRangeChanged = false;      
    }
    
    public final Window getWindow() {return iWindow;}   
    public final double getMin() {return iMin;}
    public final double getMax() {return iMax;}
    public final boolean isRangeChanged() {return iRangeChanged;}
    
}


