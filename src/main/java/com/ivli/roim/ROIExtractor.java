/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.Raster;

/**
 *
 * @author likhachev
 */

class ROIExtractor implements Extractor {
    ROI iRoi;
    RoiStats iStats = new RoiStats();
    public ROIExtractor(ROI aRoi){iRoi = aRoi;}
    
    @Override
    public void extract(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        
        final Shape shape = (null != iRoi) ? iRoi.getShape() : aRaster.getBounds();
        final Rectangle bnds = shape.getBounds();
                
        double min = 65535, max = .0, sum = .0, pix = .0;
                        
        double temp[] = new double [aRaster.getNumBands()];
  
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                if (shape.contains(i, j)) {
                    ++pix;
                    temp = aRaster.getPixel(i, j, temp);
                    if (temp[0] > max) 
                        max = temp[0];
                    else if (temp[0] < min) 
                        min = temp[0];
                    sum += temp[0];
                }
        
        iStats.iMin = min;
        iStats.iMax = max;
        iStats.iIden = sum;
        iStats.iPixels = pix;
        iStats.iBounds = bnds.getWidth() * bnds.getHeight();          
    }
}

