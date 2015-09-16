package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class ROIStats extends Measure {   
    double iPixels;
    double iArea;
       
    ROIStats() {
        super(Double.NaN, Double.NaN, Double.NaN);
        iPixels = Double.NaN;  
        iArea   = Double.NaN;             
    }
    
    ROIStats(ROIStats aS) {
        super(aS.iMin, aS.iMax, aS.iIden);
        iPixels = aS.iPixels;        
        iArea   = aS.iArea;        
    }    
    
    ROIStats(double aP, double aA, double aMi, double aMa, double aI) {
        super(aMi, aMa, aI);
        iPixels = aP;
        iArea = aA;
    }
           
   public double getPixels() {return iPixels;}
   public double getArea() {return iArea;}
}