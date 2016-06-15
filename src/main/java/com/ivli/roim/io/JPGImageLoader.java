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


import com.ivli.roim.core.PhaseInformation;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.TimeSliceVector;
import java.awt.image.Raster;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author likhachev
 */
class JPGImageLoader {    
    static {   
        ImageIO.scanForPlugins(); 
    }
    
    static final ImageReader _installImageReader() {    
        
        ImageReader ir = ImageIO.getImageReadersByFormatName("JPEG").next(); //NOI18N

        if (null == ir) {
            LOG.error("It seems there's no DICOM reader available, make a try to install one"); //NOI18N
           // IIORegistry registry = IIORegistry.getDefaultInstance();
            //registry.registerServiceProvider(new DicomImageReaderSpi());            
            //ir = ImageIO.getImageReadersByFormatName("DICOM").next();  //NOI18N
        } 
           
        return ir;      
    }  
    
    private String iFile;
    
   // private ImageReader iReader = _installImageReader();   
    //private Attributes  iDataSet;
        
    BufferedImage iImage;
    
    
    public void open(String aFile) throws IOException {
         
        iImage = ImageIO.read(new File(aFile));
        
    }
        
   
    public TimeSliceVector getTimeSliceVector() throws IOException {        
       
        ArrayList<PhaseInformation> phases = new ArrayList();
        phases.add(new PhaseInformation(Math.max(1, getNumImages()), 1000));   
        return new TimeSliceVector(phases);
    }   
    
    
    public PixelSpacing getPixelSpacing() throws IOException {             
        return PixelSpacing.UNITY_PIXEL_SPACING;
    }
    
    
    public double getMin() {
       //double ret = iDataSet.getDouble(Tag.SmallestImagePixelValue, Double.NaN);
       return Double.NaN;
    }
    
    
    public double getMax() {
       return Double.NaN;   
    }
    
    
    public int getNumImages() throws IOException {
        return 1;
    }    

   
    public Raster readRaster(int aIndex) throws IOException {
        return iImage.getRaster();
    }

   
    private static final Logger LOG = LogManager.getLogger();
}
