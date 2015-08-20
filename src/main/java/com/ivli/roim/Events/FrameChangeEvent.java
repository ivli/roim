
package com.ivli.roim.Events;

/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends java.util.EventObject {
    final int iFrame; 
    
    public FrameChangeEvent(Object aO, int aFrame) {
        super(aO); 
        iFrame = aFrame;
    }  
    
    public int getFrame() {
        return iFrame;
    }
}


