
package com.ivli.roim.core;

/**
 * Pixel Spacing in mm as described in (0028,0030) 
 * @author likhachev
 */
public class PixelSpacing { 
    public final static double UNITY_PIXEL_SPACING_X = 1.0;
    public final static double UNITY_PIXEL_SPACING_Y = 1.0;       
    public final static PixelSpacing UNITY_PIXEL_SPACING = new PixelSpacing(UNITY_PIXEL_SPACING_X, UNITY_PIXEL_SPACING_Y);
    
    protected double iX; 
    protected double iY;
    
    public PixelSpacing(double aX, double aY) {
        iX = aX;
        iY = aY;       
    }
    
    public PixelSpacing(double aX, double aY, double aZ) {
        iX = aX;
        iY = aY;       
    }
     //column spacing in mm
    public double getX() {
        return iX;
    } 
    
     //row spacing in mm
    public double getY() {
        return iY;
    }  
     //are in squared mm
    public double area() {
        return iX*iY;
    }  
    
}
