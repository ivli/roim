package com.ivli.roim.core;

import java.util.ArrayList;
import com.ivli.roim.algorithm.SeriesProcessor;

/**
 *
 * @author likhachev
 */
public class Series extends ArrayList<Double> {     
    private final Measurement iId; 
    
    public Series(Measurement anId) {        
        iId = anId;        
    }
    
    public Series(Measurement anId, Double aV) {        
        iId = anId;    
        add(aV);
    }
    
    public Measurement getId() {
        return iId;
    }  
    
    public boolean isScalar() {
        return size() == 1;
    }
    
    public SeriesProcessor processor() {
        return new SeriesProcessor(this);
    }    
}
