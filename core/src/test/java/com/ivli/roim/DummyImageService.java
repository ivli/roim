/*
 * Copyright (C) 2017 likhachev
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

import java.io.IOException;
import java.io.File;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import com.ivli.roim.core.IImage;
import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Modality;
import com.ivli.roim.core.ModalityTransform;
import com.ivli.roim.core.Photometric;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;

import com.ivli.roim.io.spi.IImageService;
import com.ivli.roim.io.spi.ImageServiceException;


/**
 *
 * @author likhachev
 */
public class DummyImageService implements IImageService {    
    
    static {
        ImageIO.scanForPlugins();
    } 

    private BufferedImage iBi = null;

    public DummyImageService() {		
        LOG.info ("Instantiating BITMAP SPI...");
    }
	
    final static String[] SUFFIXES = {"bmp", "jpg"};
    
     @Override
    public String[] getSuffixes() {        
        return SUFFIXES;
    }   
    
     @Override
    public String getDescription() {        
        return "Image file";
    }   
       
    @Override
    public void open(final String aFile) throws ImageServiceException {
        try {
            LOG.info ("Opening file: " + aFile + " ...");
            File fi = new File(aFile);           
            BufferedImage image = ImageIO.read(fi);     
            
            if (image.getColorModel().getNumColorComponents() > 1)
                throw new ImageServiceException("Unsupported color model");
            
            iBi = image;
            
        } catch (IOException ex) {	           
            throw new ImageServiceException(ex.getMessage(), ex.getCause());           	
        }		
    }
     
    @Override
    public void close () {
    } 
	
    @Override
    public int getNumFrames() {
        return 1;
    }
   
    @Override
    public int[] readFrame(int anIndex, int[] aBuffer) throws ImageServiceException {        
        final int w = image().getWidth();
        final int h = image().getHeight();  
        
        if (null == aBuffer)
            aBuffer = new int[w*h];       
        else if (aBuffer.length != w*h)
            throw new IllegalArgumentException();
                      
        return iBi.getRaster().getPixels(0, 0, w, h, aBuffer);//iReader.readRaster(0, null).getSamples(0, 0, w, h, 0, aBuffer);       
    }

    public IImage image() {
        return new IImage() {                    

            @Override
            public int getWidth() {        
                return iBi.getWidth();        
            }
            @Override
            public int getHeight() {       
                return iBi.getHeight();
            } 
            @Override
            public Modality getModality() {        
                return Modality.UNKNOWN;
            }

            @Override
            public Photometric getPhotometric() {
                return Photometric.UNKNOWN;
            }

            @Override
            public int getNumFrames() {     
                return 1;    
            }

            @Override
            public ImageDataType getImageDataType() {
                ///TODO:
                return ImageDataType.GRAYS32;
            }

            //TODO: dcm4chee applies this transform to image data before ??? 
            @Override
            public ModalityTransform getModalityTransform() { 
                return ModalityTransform.DEFAULT;
            }

             @Override
            public TimeSliceVector getTimeSliceVector() {               
                return TimeSliceVector.ONESHOT;// (phases);
            }

            public PixelSpacing getPixelSpacing() {
                return PixelSpacing.UNITY_PIXEL_SPACING;
            }

             @Override
            public double getMin() {
                return getImageDataType().getMin();       
            }

             @Override
            public double getMax() {
                return getImageDataType().getMax();       
            }

             @Override
            public ImageType getImageType() {                
                return ImageType.IMAGE;
            }

             @Override
            public SliceSpacing getSliceSpacing() {
                return SliceSpacing.UNITY_SLICE_SPACING;
            }
        };
    }
	
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();       
}
