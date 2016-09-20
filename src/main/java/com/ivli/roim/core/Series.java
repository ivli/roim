package com.ivli.roim.core;

import java.util.ArrayList;
import com.ivli.roim.algorithm.SeriesProcessor;

/**
 *
 * @author likhachev
 */
public class Series extends ISeries {         
    private ArrayList<Double> iData = new ArrayList<>();
    
    public Series(Measurement anID) {        
        super(anID);        
    }
    
    public int size() {return iData.size();}
    
    public double get(int anIndex) {return iData.get(anIndex);}
    
    public void add(double aV) {iData.add(aV);}
    
    public boolean isScalar() {
        return false;
    } 
}
