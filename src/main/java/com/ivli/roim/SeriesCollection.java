
package com.ivli.roim;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class SeriesCollection implements java.io.Serializable {
    private static final long serialVersionUID = 42L;
    
    TimeSliceVector iTimeSliceVector = null;
    final java.util.ArrayList<Series> iSeries = new java.util.ArrayList<>(); 
    
    
    SeriesCollection(TimeSliceVector aV) {
        iTimeSliceVector = aV;
    }
    
    public void addSeries(Series aC) {
        if (iTimeSliceVector.getNumFrames() != aC.getNumFrames())
            throw new IllegalArgumentException();      
               
        iSeries.add(aC);               
    } 
    
        
    
    private static final Logger logger = LogManager.getLogger(SeriesCollection.class); 
}
