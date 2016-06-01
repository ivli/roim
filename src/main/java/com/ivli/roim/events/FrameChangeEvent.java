
package com.ivli.roim.events;

import com.ivli.roim.core.Range;
import com.ivli.roim.core.TimeSlice;
/**
 *
 * @author likhachev
 */
public final class FrameChangeEvent extends java.util.EventObject {    
    private final int iFrame;//a number of current frame  
    /*
    private final int iTotal;//a number of frames in image
    
   
    private final Range iRange; //a range of the new image    
    private final TimeSlice iTimeSlice; // valid only for DYNAMIC images
    private final Double iAngularStep;  // valid only for TOMO & TOMO_RECON images
    */
    
    public FrameChangeEvent(Object aO, int aFrame){ //, int aTotal){//, Range aRange) {//, TimeSlice aTimeSlice) {
        super(aO); 
        iFrame = aFrame;
    //    iTotal = aTotal;
    //    iRange = aRange;
    //    iTimeSlice = aTimeSlice;
    //    iAngularStep = Double.NaN;
    }  
    /*
    public FrameChangeEvent(Object aO, int aFrame, int aTotal, Range aRange, Double anAngularStep) {
        super(aO); 
        iFrame = aFrame;
        iTotal = aTotal;
        iRange = aRange;
    //    iTimeSlice = null;
    //    iAngularStep = anAngularStep;
    }  
    */
    
    public int getFrame() {
        return iFrame;
    }
    
    /*
    public int getTotal() {
        return iTotal;
    }
    
    public Range getRange() {
        return iRange;
    }
    
    
    public TimeSlice getTimeSlice() {
        return iTimeSlice;
    }
    
    public double getAngularStep() {
        return iAngularStep;
    } 
*/
}


