/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.ArrayList;
/**
 *
 * @author likhachev
 */

public class RoiStats { 
    public double iPixels;  //new Measurement("measurement.pixels");  //number of pixels
    //public double iBounds = Double.NaN;  //area of bounding rectangle in pixels
    public double iArea;     //area in mm2

    public double iMin;     //min value 
    public double iMax;     //max value
    public double iIden;     //integral density (sum of pixels)  
   
    
    RoiStats() {
        iPixels = Double.NaN;  
        iArea = Double.NaN;     
        iMin = Double.NaN;  
        iMax = Double.NaN; 
        iIden = Double.NaN;  
    }
    
    RoiStats(RoiStats aS) {
        iPixels = aS.iPixels;        
        iArea = aS.iArea;
        iMin = aS.iMin;  
        iMax = aS.iMax; 
        iIden = aS.iIden; 
    }
   
}
