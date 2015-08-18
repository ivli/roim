/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.Serializable;
import java.util.ArrayList;
/**
 *
 * @author likhachev
 */

class _ROIStats<T> extends Measure<T> implements Serializable {
    T iPixels;
    T iArea;
   
    
}

public class RoiStats extends _ROIStats<Double> implements Serializable { 
    //double iPixels; //new Measurement("measurement.pixels");  //number of pixels   
    //double iArea;   //area in mm2

    //double iMin;  //min value 
    //double iMax;  //max value
    //double iIden; //integral density (sum of pixels)  
   
    
    RoiStats() {
        iPixels = Double.NaN;  
        iArea   = Double.NaN;     
        iMin    = Double.NaN;  
        iMax    = Double.NaN; 
        iIden   = Double.NaN;  
    }
    
    RoiStats(RoiStats aS) {
        iPixels = aS.iPixels;        
        iArea   = aS.iArea;
        iMin    = aS.iMin;  
        iMax    = aS.iMax; 
        iIden   = aS.iIden; 
    }
   
}
