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
            iFrameDuration = aF;  
            iNumberOfFrames = aN;
        }
    }  
            
    ArrayList<PhaseInformation> iPhases;       
    ArrayList<Long>             iSlices;
    
    
    TimeSliceVector(Attributes aAttr) {        
        iPhases = new ArrayList();
        iSlices = new ArrayList();
        iSlices.add(0L); //
        
        Sequence pid = (Sequence)aAttr.getValue(Tag.PhaseInformationSequence);
        
        if (null != pid) {
        
            int ns = aAttr.getInt(Tag.NumberOfPhases, 1);

            assert (ns == pid.size()); //a paranoid sanity check

            for (Attributes a : pid) {
                int fd = a.getInt(Tag.ActualFrameDuration, 1);     
                int nf = a.getInt(Tag.NumberOfFramesInPhase, 1);  
                iPhases.add(new PhaseInformation(fd, nf));
            }
           
            int i = 1;
            long n = 0L;
            for (PhaseInformation p : iPhases) {            
                for (int j = i; j < p.iNumberOfFrames; ++i, ++j) 
                    iSlices.add(i, n += p.iFrameDuration);                 
            }
        }
        logger.info(iSlices);
    }
    
    private static final Logger logger = LogManager.getLogger(TimeSliceVector.class);
}
