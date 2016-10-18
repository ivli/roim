
package com.ivli.roim.events;

import com.ivli.roim.core.TimeSlice;
/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends java.util.EventObject {    
    private final int iFrame; //actual frame number  
   
    public FrameChangeEvent(Object aO, int aFrame){ 
        super(aO); 
        iFrame = aFrame;   
    }  
    
    public int getFrame() {
        return iFrame;
    }
}


