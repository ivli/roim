
package com.ivli.roim.core;

/**
 * Pixel Spacing in mm as described in (0028,0030) 
 * @author likhachev
 */
public class PixelSpacing { 
    protected final static double UNITY_PIXEL_SPACING_X = 1.0;
    protected final static double UNITY_PIXEL_SPACING_Y = 1.0;
    protected final static double UNITY_PIXEL_SPACING_Z = 1.0;
    
    public final static PixelSpacing UNITY_SPACING = new PixelSpacing(UNITY_PIXEL_SPACING_X, UNITY_PIXEL_SPACING_Y);
    
    protected double iX; 
    protected double iY;
    protected double iZ;
    
    /*
    public PixelSpacing() {
        iX = UNITY_PIXEL_SPACING_X;
        iY = UNITY_PIXEL_SPACING_Y;
    }
    */
    
    public PixelSpacing(double aX, double aY) {
        iX = aX;
        iY = aY;
        iZ = UNITY_PIXEL_SPACING_Z;
    }
    
    public PixelSpacing(double aX, double aY, double aZ) {
        iX = aX;
        iY = aY;
        iZ = aZ;
    }
    
    public double getX() {return iX;} //column spacing in mm
    public double getY() {return iY;} //row spacing in mm
    public double getZ() {return iZ;} //row spacing in mm
}
