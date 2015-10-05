package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class ROIStats extends Measure {   
    private final double iPixels;
    private final double iArea;
       
    ROIStats(int anAreaInPixels) {
        super();    
        iPixels = anAreaInPixels;  
        iArea   = Double.NaN;         
    }
    
    ROIStats(ROIStats aS) {
        super(aS);
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