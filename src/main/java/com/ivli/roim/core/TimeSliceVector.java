package com.ivli.roim.core;

import java.util.ArrayList;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class TimeSliceVector implements java.io.Serializable, Comparable<TimeSliceVector> {
    private static final long serialVersionUID = 42L;
                    
       
    private ArrayList<PhaseInformation> iPhases; 
      //frame start time in millisecons from series begin
    private ArrayList<Long>             iSlices; 
    
    
    public TimeSliceVector(ArrayList<PhaseInformation> aP) {                
        iPhases =  new ArrayList();
        
        for (PhaseInformation p : aP)
            iPhases.add(new PhaseInformation(p));
        
        ///iPhases.
        iSlices = new ArrayList(); 
        
        fillSlicesArray();
    }
      
    public TimeSliceVector slice(TimeSlice aS) {
        
        ArrayList<PhaseInformation> phases =  new ArrayList();
        
        
        
        final int frameTo = (0 == aS.getTo().compareTo(Instant.INFINITE)) ? getNumFrames() - 1 : frameNumber(aS.getTo()) - 1;

        final int frameFrom = frameNumber(aS.getFrom());   
        
        final int phaseFrom = phaseFrame(frameFrom);
        final int phaseTo   = phaseFrame(frameTo);               
        
        for (int n = phaseFrom; n <= phaseTo; ++n) {        
            
            PhaseInformation pi = new PhaseInformation(iPhases.get(n));
            
            if (phaseFrom == n)  {
                pi.iNumberOfFrames = pi.iNumberOfFrames - (frameFrom - framesToPhase(n));
            }
            
            else if (phaseTo == n) {
                pi.iNumberOfFrames = frameTo - framesToPhase(n) + 1;
            }
            
            phases.add(pi);
        }
        
        return new TimeSliceVector(phases);        
    }
    
    public ArrayList<Long> getSlices() {
        return iSlices;
    }
    
    public TimeSlice getSlice(int aFrameNumber) {
        final long from = frameStarts(aFrameNumber);
        final long to = frameStarts(aFrameNumber + 1);
        return new TimeSlice (from, to);
    }
    
    private void fillSlicesArray() {
        long n = 0L;
       
        iSlices.clear();
       
        for (PhaseInformation p : iPhases) {            
            for (int j = 0; j < p.iNumberOfFrames; ++j) 
                iSlices.add(n += p.iFrameDuration);                 
        } 
        
        assert (getNumFrames() == iSlices.size());    
    }
    
    boolean isValidFrameNumber(int aFrame) {
        return aFrame >=0 && aFrame < iSlices.size();
    }
         
    public int getNumFrames() {
        int ret = 0;
        for (PhaseInformation p : iPhases) 
            ret += p.iNumberOfFrames;
        return ret;   
    }
    
    public int getNumPhases() {     
        return iPhases.size();   
    }
    
    public long phaseDuration(int aPhase) {
        return iPhases.get(aPhase).iFrameDuration * iPhases.get(aPhase).iNumberOfFrames;
    }
    
     //get phase number by frame
    public int phaseFrame(int aFrameNumber) {
        
        if (aFrameNumber < 0 || aFrameNumber > getNumFrames())
            throw new IllegalArgumentException("bad FrameNumber");
        
        long frm = 0L;
                
        for (int i = 0; i < getNumPhases(); ++i) {
            if (aFrameNumber >= framesToPhase(i) && aFrameNumber < framesToPhase(i+1))
                return i;            
        }
        
        throw new IllegalArgumentException("bad FrameNumber");        
    }
     //return number of frames before phase aPhaseNumber
    public int framesToPhase(int aPhaseNumber) {
        int ret = 0;
        
        for (int n = 0; n < aPhaseNumber; ++n)            
            ret += iPhases.get(n).iNumberOfFrames;
                
        return ret;  
    }
    
    public long phaseStarts(int aPhaseNumber) {
        long ret = 0L;
        
        for (int i = 0; i < iPhases.size() && i < aPhaseNumber; ++i)             
            ret += phaseDuration(i);    
        
        return ret;
    }
            
    public long frameLapse(int aStart, int aEnd) {    
        return frameStarts(aEnd) - frameStarts(aStart);                 
    } 
    
    public long frameStarts(int aFrameNumber) {
        if (0 == aFrameNumber)
            return 0L;
        else {             
            return iSlices.get(aFrameNumber-1);
        }
    }
    
    public long duration() { // does it make sense to cache this field
        long ret = 0L;
        for (PhaseInformation p : iPhases)
            ret += p.duration();
        return ret;
    }
       
    private void testTimeArgument(long uSec) {
        if (uSec < -1L || uSec > duration())
            throw new IllegalArgumentException("bad uSecFromStart");
    }
      //get phase number by time in uSec -returns 0, 1, 2 etc
    public int phaseNumber(long uSecFromStart) {        
        testTimeArgument(uSecFromStart);
           
        long elapsed = 0L;
        
        int n = 0;
        
        for (; n < iPhases.size() ; ++n)
            if (uSecFromStart < elapsed)
                break;
            else
                elapsed += iPhases.get(n).duration();
                
        return n - 1;
    }
    
     //get frame ordinal by time in uSec
    public int frameNumber(Instant aI) {   
        final long uSecFromStart = aI.toLong();
        
        testTimeArgument(uSecFromStart);
                
        int  phase = phaseNumber(uSecFromStart);
        int  ret = 0;
        long duration = 0L;
        
        for (int i = 0; i < phase; ++i) {            
            ret += iPhases.get(i).iNumberOfFrames;
            duration += iPhases.get(i).duration();            
        }
       
        return ret + (int)(uSecFromStart - duration) / iPhases.get(phase).iFrameDuration ;         
    }
    
    private void resamplePhase(PhaseInformation aP, int newFrameDuration) {
        if (newFrameDuration > aP.duration())
            throw new IllegalArgumentException("cannot step over phase boundary");
        
        int newNoOfFrames = aP.iNumberOfFrames * aP.iFrameDuration / newFrameDuration;
        aP.iNumberOfFrames = newNoOfFrames;
        aP.iFrameDuration = newFrameDuration;
    }
    
    public void resample(int newFrameDuration) {        
        iPhases.stream().forEach((i) -> {
            resamplePhase(i, newFrameDuration);
        });
            
        fillSlicesArray();
    }
                  
    public void resample(int aPhase, int newFrameDuration) {
        resamplePhase(iPhases.get(aPhase), newFrameDuration);        
        fillSlicesArray();
    }
   
    public int compareTo(TimeSliceVector aV) {       
        if (getNumPhases() == aV.getNumPhases() && getNumFrames() == aV.getNumFrames())
            return 0;
        
        return ((Long)duration()).compareTo(aV.duration()) ; //how to                
    }
    
    private static final Logger logger = LogManager.getLogger(TimeSliceVector.class);
}
