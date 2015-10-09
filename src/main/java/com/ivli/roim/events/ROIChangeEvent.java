
package com.ivli.roim.events;

import com.ivli.roim.ROI;
/**
 *
 * @author likhachev
 */
public final class ROIChangeEvent extends java.util.EventObject { 
    private final ROI iROI;
    private final EStateChanged iChange;
    
    public ROIChangeEvent(Object aO, ROI aR, EStateChanged aC) {
        super (aO);
        iROI = aR;
        iChange = aC;
    }
        
    public EStateChanged getChange() {
        return iChange;
    }
    
    public ROI getROI() {
        return iROI;
    }    
}
