
package com.ivli.roim.events;

import com.ivli.roim.core.Range;
/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends java.util.EventObject {
    private final int iFrame; 
    private final int iTotal;
    
    private final Range iRange;
    
    public FrameChangeEvent(Object aO, int aFrame, int aTotal, Range aRange) {
        super(aO); 
        iFrame = aFrame;
        iTotal = aTotal;
        iRange = aRange;
    }  
    
    public int getFrame() {
        return iFrame;
    }
    
    public int getTotal() {
        return iTotal;
    }
    
    public Range getRange() {
        return iRange;
    }
}


