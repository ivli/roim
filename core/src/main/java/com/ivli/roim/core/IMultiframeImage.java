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

import java.util.Iterator;
import com.ivli.roim.algorithm.ImageProcessor;

/**
 *
 * @author likhachev
 * 
 * 
 */
public abstract class IMultiframeImage implements IImage, Iterable<ImageFrame>, IFrameProvider, Cloneable {          
    
    /**
     *    
     * @return ImageProcessor - providing means of image manipulations
     */
    public abstract ImageProcessor processor();
    
    /**
     *    
     * @return parent image or null for the root image
     */
    public abstract IMultiframeImage parent();
    
    /**
     *
     * @param aFrameNumber
     * @return safe check whether frame is present or not
     */
    public abstract boolean hasAt(int aFrameNumber);
    
    /**
     * Creates a multiframe image with physical characteristics of original except Z or time axis whose length is set in the first parameter
     * frames are zeroed out  
     * @param aNoOfFrames - a number of frames
     * @return  multiframe image of the same physical dimensions   
     */
    public abstract IMultiframeImage createCompatibleImage(int aNoOfFrames); 
    
    /**
     * Creates pixel-wise compatible multi-frame image of certain dimensions  
     * frames are zeroed out 
     * @param aX - number of rows
     * @param aY - number of columns
     * @param aZ - a number of frames
     * @param aBuf - image data can be null
     * @return  multiframe image of the same physical dimensions   
     */
    public abstract IMultiframeImage createCompatibleImage(int aX, int aY, int aZ, int[][]aBuf); 
    
     /**
     * @return array of pixels as following [depth][width*height]       
     */
    public abstract int[][] getAsArray(); 
    
    /**
     * Returns a deep copy of original image    
     * @return  a multiframe image   
     */
    public abstract IMultiframeImage duplicate(); 
    
    @Override
    public Iterator<ImageFrame> iterator() {    
        return new Iterator<ImageFrame>() {
            private int _next = 0;
            @Override
            public boolean hasNext() {    
                return hasAt(_next);
            }

            @Override
            public ImageFrame next() {
                return get(_next++);
            }  
        };
    }
}
