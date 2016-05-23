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

import java.io.IOException;

import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;
import java.awt.image.Raster;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
class DCMImageProvider implements IImageProvider {    
    private final DCMImageLoader iLoader;    
    
    int iWidth;
    int iHeight;
    int iNumFrames;     
    TimeSliceVector iTimeSliceVector;
    PixelSpacing iPixelSpacing;    
    SliceSpacing iSliceSpacing;
    ImageType iImageType;
   
    
    
    private void doInit(DCMImageLoader ldr) throws IOException {

        iImageType = ldr.getImageType();
        iTimeSliceVector = ldr.getTimeSliceVector();        

        try {
            iPixelSpacing = ldr.getPixelSpacing();
        } catch (IOException ex) {
            iPixelSpacing = PixelSpacing.UNITY_PIXEL_SPACING;
        }

        iNumFrames = ldr.getNumImages();
        //Raster f = ldr.readRaster(0);

        iWidth = ldr.getWidth();
        iHeight = ldr.getHeight();           
    } 
    
    protected DCMImageProvider(DCMImageLoader aLoader) throws IOException {
        doInit(aLoader);
        iLoader = aLoader;
    }
    /*
    private DCMImageProvider(final String aFile) throws IOException {         
        try (DCMImageLoader ldr = new DCMImageLoader(aFile)) {           
            doInit(ldr);
            iLoader = ldr;
        } catch (IOException ex) {
            logger.error("FATAL!!", ex);
        }            
       
    }  
    */
    
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
            Raster r = iLoader.readRaster(anIndex);                       
            return new ImageFrame(iWidth, iHeight, r.getSamples(0, 0, iWidth, iHeight, 0, new int[iWidth*iHeight]));
        } catch (IOException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public PValueTransform getTransform() {
        return iLoader.getRescaleTransform();
    }
            
    public double getMin() {
        return iLoader.getMin();
    } 

    public double getMax() {
        return iLoader.getMax();
    }
    
    private static final Logger logger = LogManager.getLogger(DCMImageProvider.class);       
}
