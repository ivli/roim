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
 *
 * @author likhachev
 * 
 * note: this is just an iterator/accessor actual frames are stored in descendants  
 * 
 */
public abstract class IMultiframeImage implements Iterable<ImageFrame>, java.io.Serializable {          
      
    public abstract ImageDataType getImageDataType();
    public abstract ImageType getImageType();
    /**
     *
     * @return image widht in pixels
     */
    public abstract int getWidth();  

    /**
     *
     * @return image heigdht in pixels
     */
    public abstract int getHeight();

    /**
     *
     * @return pixel spacing X and Y in mm
     */
    public abstract PixelSpacing getPixelSpacing();   

    /**
     *
     * @return temporal characteristics of the image 
     */
    public abstract TimeSliceVector getTimeSliceVector();

    /**
     *
     * @return a number of frames
     */
    public abstract int getNumFrames();  
    
    /**
     *
     * @return minimal pixel value either loaded fro dicom or calculated at loading time
     */
    public abstract double getMin();  

    /**
     *
     * @return maximal pixel value either loaded fro dicom or calculated at loading time
     */
    public abstract double getMax();
    
    /**
     *
     * @param aFrameNumber
     * @return safe check whether frame is present or not
     */
    public abstract boolean hasAt(int aFrameNumber);
    
    /**
     *
     * @param aFrameNumber - requested frame number
     * @return - a frame
     * @throws java.util.NoSuchElementException
     */
    public abstract ImageFrame get(int aFrameNumber) throws java.util.NoSuchElementException; 
    
    /**
     * Creates a multiframe image with physical characteristics of original but Z or time axis whose lenght is set in the first parementer
     * frames are zeroed out  
     * @param anInitialCapacity - initial number of frames
     * @return - a multiframe image of the same physical dimensions   
     */
    public abstract IMultiframeImage createCompatibleImage(int anInitialCapacity); 
    
    /**
     * Creates a deep coopy of original image    
     * @return - a multiframe image   
     */
    public abstract IMultiframeImage duplicate(); 
    
    @Override
    public java.util.Iterator<ImageFrame> iterator() {    
        return new java.util.Iterator<ImageFrame>() {
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
