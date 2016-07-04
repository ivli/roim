/*
 * 
 */
package com.ivli.roim.core;

import com.ivli.roim.algorithm.SeriesProcessor;
import java.util.ArrayList;
        
/**
 *
 * @author likhachev
 */
public class Series {     
    private final Measurement iId; 
    private final ArrayList<Double> iData;
    
    
    public Series(Measurement anId) {        
        iId = anId; 
        iData = new ArrayList<> ();
    }
    
    public Series(Measurement anId, double aVal) {        
        this(anId);
        iData.add(aVal);
    }
    
    public SeriesProcessor processor() {
        return new SeriesProcessor(this);
    }
    
    public boolean isScalar() {
        return iData.size() == 1;
    }
    
    public Measurement getId() {
        return iId;
    }
    
    public int getNumFrames() {
        return iData.size();
    }
    
    public double get(int anI) {
        return iData.get(anI);
    }
    
    public void add(double aV) {
        iData.add(aV);
    } 
}
