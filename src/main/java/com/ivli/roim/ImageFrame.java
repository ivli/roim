/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.image.Raster;

/**
 *
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable {
    Raster   iRaster;  
    ROIStats iStats;   
    
    public Raster getRaster() {
        return iRaster;
    }
    
    public ROIStats getStats() {
        return iStats;
    }
    
    public int getWidth() {
        return iRaster.getWidth();
    }
    
    public int getHeight() {
        return iRaster.getHeight();
    }
    
    ImageFrame(Raster aRaster) {
        iRaster = aRaster;
        ROIExtractor ex = new ROIExtractor(iRaster.getBounds());
        ex.apply(aRaster);
        iStats = ex.iStats;
    }
     
}
