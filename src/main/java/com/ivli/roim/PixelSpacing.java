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
public class PixelSpacing {
    protected final double DEFAULT_PIXEL_SPACING_X = 1.0;
    protected final double DEFAULT_PIXEL_SPACING_Y = 1.0;
    
    public double iX;
    public double iY;
    
    public PixelSpacing() {
        iX = DEFAULT_PIXEL_SPACING_X;
        iY = DEFAULT_PIXEL_SPACING_Y;
    }
    
    public PixelSpacing(double aX, double aY) {
        iX = aX;
        iY = aY;
    }
}
