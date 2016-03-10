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
package com.ivli.roim.provider;

import java.io.IOException;
import java.io.File;

import com.ivli.roim.core.IImageProvider;
import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class DCMImageProvider implements IImageProvider {    
    private final DCMImageLoader iLoader;    
    
    int iWidth;
    int iHeight;
    int iNumFrames;     
    TimeSliceVector iTimeSliceVector;
    PixelSpacing iPixelSpacing;    
    SliceSpacing iSliceSpacing;
    ImageType iImageType;
    
    public DCMImageProvider(File aFile) throws IOException { 
        iLoader = new DCMImageLoader();
        
        try {           
            iLoader.open(aFile);
            iImageType = iLoader.getImageType();
            iTimeSliceVector = iLoader.getTimeSliceVector();        
            
            try{
                iPixelSpacing = iLoader.getPixelSpacing();
            } catch (IOException ex) {
                iPixelSpacing = new PixelSpacing(1.0, 1.0);
            }
            
            iNumFrames = iLoader.getNumImages();
           
            java.awt.image.Raster f = iLoader.readRaster(0);

            iWidth  = f.getWidth();
            iHeight = f.getHeight();        
        } catch (IOException ex) {
            logger.error("FATAL!!", ex);
        }            
    }  
    
    @Override
    public int getWidth() {
        return iWidth;
    }
    
    @Override
    public int getHeight() {
        return iHeight;
    }
   
    @Override
    public int getNumFrames() {
        return iNumFrames;
    }
    
    @Override
    public PixelSpacing getPixelSpacing() {
        return iPixelSpacing;
    }
    
    public SliceSpacing getSliceSpacing() {
        return iSliceSpacing;    
    }
    
    public ImageType getImageType() {       
        return iImageType;
    }
    
    public ImageDataType getImageDataType() {
        return ImageDataType.GRAYS32;
    }
    
    @Override
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSliceVector;
    }
    
    @Override
    public ImageFrame get(int anIndex) throws IndexOutOfBoundsException/*, IOException*/ {
        try {
            java.awt.image.Raster r = iLoader.readRaster(anIndex);                       
            return new ImageFrame(iWidth, iHeight, r.getSamples(0, 0, iWidth, iHeight, 0, new int[iWidth*iHeight]));
        } catch (IOException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public double getMin() {
        return iLoader.getMin();
    } 

    public double getMax() {
        return iLoader.getMax();
    }
    
    private static final Logger logger = LogManager.getLogger(DCMImageProvider.class);       
}
