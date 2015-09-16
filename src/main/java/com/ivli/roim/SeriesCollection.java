
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class SeriesCollection {
    
    TimeSliceVector iTimeSliceVector = null;
    final java.util.ArrayList<Series> iSeries = new java.util.ArrayList<>(); 
    
    public void setTimeSliceVector(TimeSliceVector aV) {
        if (iSeries.isEmpty() || iSeries.get(0).noOfFrames() == aV.noOfFrames() )
            iTimeSliceVector = aV;
        else
            throw new IllegalArgumentException();
    }
    
    public void addSeries(Series aC) {
        if (null == iTimeSliceVector || iTimeSliceVector.noOfFrames() == aC.noOfFrames())
            iSeries.add(aC);
        else
            throw new IllegalArgumentException();                  
    } 
    
        
 
}
