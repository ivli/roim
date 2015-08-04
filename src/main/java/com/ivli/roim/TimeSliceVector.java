/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class TimeSliceVector {

    public class PhaseInformation {
        int iFrameDuration;
        int iNumberOfFrames;
        
        PhaseInformation(int aF, int aN) {
            iFrameDuration  = aF;  
            iNumberOfFrames = aN;
        }
    }  
            
    ArrayList<PhaseInformation> iPhases;       
    ArrayList<Long>             iSlices;
        
    TimeSliceVector(Attributes aAttr) {        
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
    
    private static final Logger logger = LogManager.getLogger(TimeSliceVector.class);
}
