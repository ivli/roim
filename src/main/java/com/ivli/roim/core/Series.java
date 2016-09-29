package com.ivli.roim.core;

import java.util.ArrayList;
import com.ivli.roim.algorithm.SeriesProcessor;

/**
 *
 * @author likhachev
 */
public class Series extends ISeries {         
    private final ArrayList<Double> iData = new ArrayList<>();
    
    public Series(Measurement anID) {        
        super(anID);        
    }
    
    @Override
    public int size() {
        return iData.size();
    }
    
    @Override
    public double get(int anIndex) {
        return iData.get(anIndex);
    }
    
    @Override
    public void add(double aV) {
        iData.add(aV);
    }
    
    @Override
    public boolean isScalar() {
        return false;
    } 
}
