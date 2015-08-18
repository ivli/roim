/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.image.Raster;
import java.io.Serializable;

/**
 *
 * @author likhachev
 */
public class ImageFrame implements Serializable {
              Raster   iRaster;  
    transient ROIStats iStats;   
    
    public Raster getRaster() {
        return iRaster;
    }
    
    public ROIStats getStats()  {
        return iStats;
    }
            
}
