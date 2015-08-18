package com.ivli.roim;

import java.io.Serializable;

/**
 *
 * @author likhachev
 */

public class ROIStats extends Measure<Double> implements Serializable {
    double iPixels;
    double iArea;
       
    ROIStats() {
        iPixels = Double.NaN;  
        iArea   = Double.NaN;     
        iMin    = Double.NaN;  
        iMax    = Double.NaN; 
        iIden   = Double.NaN;  
    }
    
    ROIStats(ROIStats aS) {
        iPixels = aS.iPixels;        
        iArea   = aS.iArea;
        iMin    = aS.iMin;  
        iMax    = aS.iMax; 
        iIden   = aS.iIden; 
    }
    
   public double getPixels() {return iPixels;}
   public double getArea() {return iArea;}
}