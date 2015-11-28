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
package com.ivli.roim;


import com.ivli.roim.core.IImageProvider;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.TimeSliceVector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiframeImage extends IMultiframeImage {
    private final IImageProvider iProvider;   
    
    public MultiframeImage(IImageProvider aProvider) {
        iProvider = aProvider;        
    }
   
     @Override
    public TimeSliceVector getTimeSliceVector() {
        return iProvider.getTimeSliceVector();
    }
    
     @Override
    public boolean hasAt(int aFrameNumber) {               
        return (aFrameNumber >=0 && aFrameNumber < iProvider.getNumFrames()) ;          
    }
       
     @Override
    public ImageFrame get(int aFrameNumber) throws java.util.NoSuchElementException {                  
        return iProvider.frame(aFrameNumber);         
    }     
       
     @Override
    public int getNumFrames() {        
        return iProvider.getNumFrames();       
    }
    
     @Override
    public int getWidth() {
        return iProvider.getWidth();
    }
    
     @Override
    public int getHeight() {
        return iProvider.getHeight();
    }  
    
     @Override
    public PixelSpacing getPixelSpacing() {
        return iProvider.getPixelSpacing();
    }
    
     @Override             
    public double getMin() { 
        return iProvider.getMin();
    }  
    
     @Override
    public double getMax() { 
        return iProvider.getMax();
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

