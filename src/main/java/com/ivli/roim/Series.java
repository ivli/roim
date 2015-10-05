/*
 * 
 */
package com.ivli.roim;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;

/**
 *
 * @author likhachev
 */

public class Series extends java.util.ArrayList<Measure> {
   
    String iName;
    
    Series(String aName) {
        iName = aName;
    }
    
    public int getNumFrames() {
        return size();
    }
    
    /*
    public Series fit() {
        
     SplineInterpolator ipo = new SplineInterpolator();
     
     double[] x = new double[this.size()];
     double[] y = new double[this.size()];
     
     for (int n=0; n < size(); ++n)
         x[n] = (double)n;
     
     ipo.interpolate(x, this.toArray(y));
        
        
    }
    */
    
}
