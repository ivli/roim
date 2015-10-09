package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */

public class ROIStats extends Measure {   
    private final double iPixels;
    private final double iArea;
       
    public ROIStats(double anAreaInPixels, PixelSpacing aSpacing) {
        super();    
        iPixels = anAreaInPixels;  
        iArea   = anAreaInPixels * aSpacing.iX;         
    }
    
    public ROIStats(ROIStats aS) {
        super(aS);
        iPixels = aS.iPixels;        
        iArea   = aS.iArea;        
    }    
        
    public ROIStats(double aP, double aA, double aMi, double aMa, double aI) {
        super(aMi, aMa, aI);
        iPixels = aP;
        iArea = aA;
    }
           
   public double getPixels() {return iPixels;}
   public double getArea() {return iArea;}  
}