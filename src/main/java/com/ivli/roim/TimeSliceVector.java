
package com.ivli.roim;

import java.util.ArrayList;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class TimeSliceVector implements java.io.Serializable {

    public class PhaseInformation {
        int iFrameDuration;
        int iNumberOfFrames;
        
        PhaseInformation(int aF, int aN) {
            iFrameDuration  = aF;  
            iNumberOfFrames = aN;
        }
    }  
            
    ArrayList<PhaseInformation> iPhases; 
      //frame start time in millisecons from series begin
    ArrayList<Long>             iSlices; 
        
    public TimeSliceVector(Attributes aAttr) {        
        iPhases = new ArrayList();
        iSlices = new ArrayList();
        
        
        Sequence pid = (Sequence)aAttr.getValue(Tag.PhaseInformationSequence);
        
        if (null != pid) {
        
            for (Attributes a : pid) {
                int fd = a.getInt(Tag.ActualFrameDuration, 1);     
                int nf = a.getInt(Tag.NumberOfFramesInPhase, 1);  
                iPhases.add(new PhaseInformation(fd, nf));
            }
                       
            long n = 0L;
            
            for (PhaseInformation p : iPhases) {            
                for (int j = 0; j < p.iNumberOfFrames; ++j) 
                    iSlices.add(n += p.iFrameDuration);                 
            }
        } else {
            iSlices.add(0L); //        
        }
        
        logger.info(iSlices);
    }
    
    boolean isValidFrameNumber(int aFrame) {
        return aFrame >=0 && aFrame < iSlices.size();
    }
         
    public int noOfFrames() {
        int ret = 0;
        for (PhaseInformation p : iPhases) 
            ret += p.iNumberOfFrames;
        return ret;   
    }
    
    public long phaseDuration(int aPhase) {
        return iPhases.get(aPhase).iFrameDuration * iPhases.get(aPhase).iNumberOfFrames;
    }
    
    public int phaseFrame(int aFrameNumber) {
        int ctr = 0, phase = 1;
        
        for (PhaseInformation p:iPhases) {
            if (aFrameNumber > ctr && aFrameNumber < (ctr += p.iNumberOfFrames))
                return phase;
            ++phase;
        }
        
        return 0;
    }
    
    public long phaseStarts(int aPhaseNumber) {
        long ret = 0L;
        
        for (int i = 0; i < iPhases.size() && i <= aPhaseNumber; ++i)             
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
            long ret   = 0L;
            int  frame = 0;   
            int  phase = 1, ctr = 0;
            
            for (PhaseInformation p:iPhases) {
                if (aFrameNumber > ctr &&  aFrameNumber < (ctr += p.iNumberOfFrames))                                          
                    return ret += (aFrameNumber - ctr) * p.iFrameDuration;
                
                ret += p.iNumberOfFrames * p.iFrameDuration;                        
            }
  
            return ret;
        }
    }
    
    public int frameNumber(long anUSecFromStart) {
        if (0 == anUSecFromStart)
            return 0;
        else
            return 0;
    }
    
    ///public TimeSliceVector 
    
    private static final Logger logger = LogManager.getLogger(TimeSliceVector.class);
}
