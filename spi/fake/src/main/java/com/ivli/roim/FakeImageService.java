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
package com.ivli.roim;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;


import com.ivli.roim.core.IImage;
import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Modality;
import com.ivli.roim.core.ModalityTransform;
import com.ivli.roim.core.PhaseInformation;
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
public class FakeImageService implements IImageService {    
   
    public FakeImageService() {		
        LOG.info ("Instantiating FAKE SPI...");
    }

    final static String[] SUFFIXES = {"fck", "fake"};
    
    @Override
    public String getDescription() {
        return "Fake image";
    }
    
    @Override
    public String[] getSuffixes() {        
        return SUFFIXES;
    }   
      
    @Override
    public void open(final String aFile) {
        
        LOG.info ("Opening file: " + aFile + " ...");                   
      	
    }
     
    @Override
    public void close () {} 
	
    @Override
    public int getNumFrames() {
        return 1;
    }
   
    @Override
    public int[] readFrame(int anIndex, int[] aBuffer) throws ImageServiceException {        
        final int w = 100;
        final int h = 200;  
        
        if (null == aBuffer)
            aBuffer = new int[w*h];       
        else if (aBuffer.length != w*h)
            throw new ImageServiceException("Invalid frame number");
        


            for (int i=0; i< w; i++)
                for (int j=0; j<h; j++)
                    aBuffer[i*w+j] = 0;
            return aBuffer;      

    }

    public IImage image() { 
        return new IImage() {
                @Override
                public Modality getModality() {        
                    return Modality.OT;
                }

                @Override
                public Photometric getPhotometric() {

                    return Photometric.UNKNOWN;
                }

                @Override
                public int getWidth() {        
                    return 100;        
                }

                @Override
                public int getHeight() {       
                    return 200;
                }

                @Override
                public int getNumFrames() {     
                    return 1;    
                }

                @Override
                public ImageDataType getImageDataType() {
                    return ImageDataType.GRAYS32;
                }

                //TODO: dcm4chee applies this transform to image data before ??? 
                @Override
                public ModalityTransform getModalityTransform() { 
                    /*
                    double  s = iDataSet.getDouble(Tag.RescaleSlope, 1.);   
                    double  i = iDataSet.getDouble(Tag.RescaleIntercept, .0);                    
                    return new ModalityTransform(s, i);           
                    */
                    return ModalityTransform.DEFAULT;
                }

                 @Override
                public TimeSliceVector getTimeSliceVector() {
                    return TimeSliceVector.ONESHOT;
                }

                public PixelSpacing getPixelSpacing() {
                   return PixelSpacing.UNITY_PIXEL_SPACING;
                }

                 @Override
                public double getMin() {
                    return Double.NaN;       
                }

                 @Override
                public double getMax() {
                    return Double.NaN;       
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
