
package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class PhaseInformation implements java.io.Serializable, Comparable<PhaseInformation> {
   
    private static final long serialVersionUID = 42L;
    
    int iNumberOfFrames;
    int iFrameDuration;
   
    PhaseInformation(int aNoOfFRames, int aFrameDurationMilliseconds) {
        iNumberOfFrames = aNoOfFRames;
        iFrameDuration  = aFrameDurationMilliseconds;          
    }
    
    PhaseInformation(PhaseInformation aP) {
        iNumberOfFrames = aP.iNumberOfFrames;
        iFrameDuration  = aP.iFrameDuration;      
    }
    
    public long duration() {
        return iNumberOfFrames * iFrameDuration;
    }
    
    public int compareTo(final PhaseInformation aP) {
        
        if (iNumberOfFrames == aP.iNumberOfFrames && iFrameDuration == aP.iFrameDuration)
            return 0;
        else {
             int ret = ((Long)duration()).compareTo(aP.duration());    
             if (0 == ret)
                 --ret; //the case phases have the same duration while number of frames and frame duration are different 
             return ret;
        }
    }
}  

