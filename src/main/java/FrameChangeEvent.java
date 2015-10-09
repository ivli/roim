
package com.ivli.roim.events;

/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends java.util.EventObject {
    final int iFrame; 
    final int iTotal;
    public FrameChangeEvent(Object aO, int aFrame, int aTotal) {
        super(aO); 
        iFrame = aFrame;
        iTotal = aTotal;
    }  
    
    public int getFrame() {
        return iFrame;
    }
    
    public int getTotal() {
        return iTotal;
    }
}


