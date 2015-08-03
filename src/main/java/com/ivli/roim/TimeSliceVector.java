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
    ArrayList<Integer> iSlices;
    
    
    TimeSliceVector(Attributes aAttr) {        
        Sequence pid = (Sequence)aAttr.getValue(Tag.PhaseInformationSequence);
        int ns = aAttr.getInt(Tag.NumberOfPhases, 1);
        
        assert (ns == pid.size()); //a paranoid sanity check
        
        iPhases = new ArrayList();
        
        for (Attributes a : pid) {
            int fd = a.getInt(Tag.ActualFrameDuration, 1);     
            int nf = a.getInt(Tag.NumberOfFramesInPhase, 1);  
            iPhases.add(new PhaseInformation(fd, nf));
        }
        
        for (PhaseInformation p : iPhases) {
            
            
        }
    }
}
