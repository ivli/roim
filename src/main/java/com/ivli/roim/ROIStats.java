package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class ROIStats extends Measure<Double> implements java.io.Serializable {
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
    
    
   ROIStats(double aP, double aA, double aMi, double aMa, double aI) {
       super(aMi, aMa, aI);
       iPixels = aP;
       iArea = aA;
   }
           
   public double getPixels() {return iPixels;}
   public double getArea() {return iArea;}
}