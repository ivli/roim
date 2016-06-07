/*
 * Copyright (C) 2016 likhachev
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
 *
 * @author likhachev
 */
public interface IImage {
   /**
     *
     * @return image width in pixels
     */
    int getWidth();  
    /**
     *
     * @return image width in pixels
     */
    int getHeight();   
    /**
     *
     * @return a number of frames
     */
    int getNumFrames();   
    
    /**
     * it always returs GRAYS32
     * @return type of the image
     * @see ImageDataType
     *      
     */
    ImageDataType getImageDataType();
        
    /**
     *
     * @return type of the image
     * @see ImageType
     */
    ImageType getImageType();
    /**
     *
     * @return pixel physical dimensions in mm
     */
    PixelSpacing getPixelSpacing();  
    /**
     *
     * @return for TOMO RECON images - physical slice thickness 
     */
    SliceSpacing getSliceSpacing();    
    /**
     *
     * @return for DYNAMIC images - temporal characteristics of multiframe image 
     */
    TimeSliceVector getTimeSliceVector();            
    /**
     *
     * @return minimal pixel value in serie 
     */
    double getMin();  

    /**
     *
     * @return maximal pixel value in serie 
     */
    double getMax();
    
    PValueTransform getTransform();        
     
}