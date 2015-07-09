/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.image.Raster;
import java.awt.Point;

/**
 *
 * @author likhachev
 */
public class ProfileExtractor implements Extractor {
    
    Point iFrom, iTo;
    int iThickness = 1;
    
    ProfileExtractor(Point aFrom, Point aTo, int aThickness) {        
        assert(aFrom.y == aTo.y); //unless skewed profile line get supported
        assert(aFrom.x < aTo.x); 
        
        iFrom = aFrom;
        iTo = aTo;
        iThickness = Math.max(iThickness, aThickness);
    }
    
    @Override
    public void extract(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        
        double temp[] = new double [aRaster.getNumBands()];
        
        for (int i = iFrom.x; i < iTo.x; ++i) {
           temp = aRaster.getPixel(i, iFrom.y, temp) ;
            
        }
            
    }
}
