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
package com.ivli.roim.io;

import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;

/**
 *  
 * @author likhachev
 */
public interface IImageProvider {                  
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
     *
     * @param anIndex
     * @return a frame of the image specified by parameter anIndex 
     */
    ImageFrame get(int anIndex) throws IndexOutOfBoundsException; 
    
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
     * @return minimal serie pixel value
     */
    double getMin();  

    /**
     *
     * @return maximal serie pixel value
     */
    double getMax();
    
    public PValueTransform getTransform();        
    
}
