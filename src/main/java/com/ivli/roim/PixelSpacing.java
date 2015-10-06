
package com.ivli.roim;

/**
 * Pixel Spacing in mm as described in (0028,0030) 
 * @author likhachev
 */
public class PixelSpacing { 
    protected final double DEFAULT_PIXEL_SPACING_X = 1.0;
    protected final double DEFAULT_PIXEL_SPACING_Y = 1.0;
    
    public final double iX; 
    public final double iY;
    
    public PixelSpacing() {
        iX = DEFAULT_PIXEL_SPACING_X;
        iY = DEFAULT_PIXEL_SPACING_Y;
    }
    
    public PixelSpacing(double aX, double aY) {
        iX = aX;
        iY = aY;
    }
    
    public double getX() {return iX;} //column spacing in mm
    public double getY() {return iY;} //row spacing in mm
    
}
