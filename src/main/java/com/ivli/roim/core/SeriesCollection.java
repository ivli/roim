
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public class SeriesCollection implements java.io.Serializable {
    private static final long serialVersionUID = 42L;
    
    //private TimeSliceVector iTimeSliceVector = null;
    private final java.util.HashMap<Measurement, Series> iSeries = new java.util.HashMap<>(); 
        
    public boolean isEmpty() {
        return iSeries.isEmpty();
    }    
    
    public void addSeries(Series aC) {
        iSeries.put(aC.getId(), aC);               
    } 
    
    public Series get(Measurement anId) {        
        return iSeries.get(anId);
    }          
}
