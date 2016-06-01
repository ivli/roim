/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim.core;

/**
 * Pixel Spacing - i.e. physical distance between ajacent pixels in mm, as stored in dicom (0028,0030) 
 * @author likhachev
 */
public class PixelSpacing implements java.io.Serializable { 

    /**
     *  pixel-to-pixel spacing when physical measurements are not known
     */
    public static final double UNITY_PIXEL_SPACING_X = 1.0;
    public static final double UNITY_PIXEL_SPACING_Y = 1.0;       
    public static final PixelSpacing UNITY_PIXEL_SPACING = new PixelSpacing(UNITY_PIXEL_SPACING_X, UNITY_PIXEL_SPACING_Y);
    
    /**
     * column spacing
     */
    protected double iX; 

    /**
     * row spacing
     */
    protected double iY;
    
    /**
     *
     * @param aX
     * @param aY
     */
    public PixelSpacing(double aX, double aY) {
        iX = aX;
        iY = aY;       
    }
    
    /**
     *
     * @return column spacing in mm
     */
    public double getX() {
        return iX;
    } 
    
    /**
     *
     * @return row spacing in mm
     */
    public double getY() {
        return iY;
    }    

    /**
     *
     * @return flat area in squared mm
     */
    public double area() {
        return iX*iY;
    }      
}
