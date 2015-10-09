
package com.ivli.roim;

import com.ivli.roim.core.Series;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class SeriesCollection implements java.io.Serializable {
    private static final long serialVersionUID = 42L;
    
    //private TimeSliceVector iTimeSliceVector = null;
    private final java.util.HashMap<Integer, Series> iSeries = new java.util.HashMap<>(); 
    
    
    public SeriesCollection(/*TimeSliceVector aV*/) {
      //  iTimeSliceVector = aV;
    }
    
    //public SeriesCollection() {}
    
    
    public void addSeries(Series aC) {
        //if (iTimeSliceVector.getNumFrames() != aC.getNumFrames())
        //   throw new IllegalArgumentException();      
               
        iSeries.put(aC.getId(), aC);               
    } 
    
    public Series get(int anId) {
        
        return iSeries.get(anId);
    }
        
    
    private static final Logger logger = LogManager.getLogger(SeriesCollection.class); 
}
