/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

/**
 *
 * @author likhachev
 */

public class RoiStats {
    
    class Measurement {
        public double iVal;
        public final String iFormat;
        public String toString() {return String.format(iFormat, iVal);}
        Measurement(String aF) {iVal = Double.NaN; iFormat = aF;}
    }
   
    public RoiStats() {
    }
    
    public RoiStats(RoiStats aS) {
        iPixels  = aS.iPixels;
        iBounds  = aS.iBounds;
        iArea = aS.iArea;
        iMin  = aS.iMin;
        iMax  = aS.iMax;
        iIden = aS.iIden;
    }
   
   
   public double iPixels  = Double.NaN;  //new Measurement("measurement.pixels");  //number of pixels
   public double iBounds  = Double.NaN;  //area of bounding rectangle in pixels
   public double iArea = Double.NaN;     //area in mm2
   public double iMin  = Double.NaN;     //min value 
   public double iMax  = Double.NaN;     //max value
   public double iIden = Double.NaN;     //integral density (sum of pixels)   
}
