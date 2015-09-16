
package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class PhaseInformation implements java.io.Serializable {
   
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
}  

