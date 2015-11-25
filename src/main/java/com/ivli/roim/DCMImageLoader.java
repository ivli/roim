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


import java.io.File;
import java.io.IOException;
import java.awt.image.Raster;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;

/* IFDEF_DCM4CHE2 
import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.imageioimpl.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che2.data.Attributes;
import org.dcm4che2.io.DicomInputStream;
/* ENDIF */
/* IFDEF_DCM4CHE3 */
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.data.Attributes;

import org.dcm4che3.data.Sequence;

import com.ivli.roim.core.PhaseInformation;
import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.IImageLoader;

/* ENDIF */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Tag;


public class DCMImageLoader implements IImageLoader {
    
    static {   
        ImageIO.scanForPlugins(); 
    }
    
    static final ImageReader _installImageReader() {    
        
        ImageReader ir = ImageIO.getImageReadersByFormatName("DICOM").next(); //NOI18N

        if (null == ir) {
            logger.error("It seems there's no DICOM reader available, make a try to install one"); //NOI18N
            IIORegistry registry = IIORegistry.getDefaultInstance();
            registry.registerServiceProvider(new DicomImageReaderSpi());            
            ir = ImageIO.getImageReadersByFormatName("DICOM").next();  //NOI18N
        } 
           
        return ir;      
    }  
    
    private String iFile;
    
    private ImageReader iReader = _installImageReader();   
    private Attributes  iDataSet;
        
    @Override
    public void open(String aFile) throws IOException {
         
        try (DicomInputStream dis = new DicomInputStream(new File(iFile = aFile))) {  
            
            iDataSet = dis.readDataset(-1, -1);//readFileMetaInformation();
           
        } catch (IOException e) {
            logger.error("FATAL!", e);              
        }        
        
        ImageInputStream iis = ImageIO.createImageInputStream(new File(aFile));
        iReader.setInput(iis);  
    }
        
    @Override
    public TimeSliceVector getTimeSliceVector() throws IOException {        
       
        ArrayList<PhaseInformation> phases = new ArrayList();
                   
        Sequence pid = (Sequence)iDataSet.getValue(Tag.PhaseInformationSequence);
        
        if (null != pid) {        
            for (Attributes a : pid) {
                int fd = a.getInt(Tag.ActualFrameDuration, 1);     
                int nf = a.getInt(Tag.NumberOfFramesInPhase, 1);  
                phases.add(new PhaseInformation(nf, fd));
            }  
        } else {  
             // either image is single frame or phase information is not present
            if (getNumImages() != 1)
                logger.info("file is suspicious");
            phases.add(new PhaseInformation(Math.max(1, getNumImages()), iDataSet.getInt(Tag.ActualFrameDuration, 1000)));   
        }
         
        return new TimeSliceVector(phases);
    }   
    
    @Override
    public PixelSpacing getPixelSpacing() throws IOException {        
        double[] ps = iDataSet.getDoubles(Tag.PixelSpacing); 
        if (null != ps && ps.length >=2 )
            return new PixelSpacing (ps[0], ps[1]);
        else
            return new PixelSpacing();
    }
    
    @Override
    public int getNumImages() throws IOException {
        return iReader.getNumImages(false);
    }    

    @Override
    public Raster readRaster(int aIndex) throws IOException {
        return iReader.readRaster(aIndex, readParam());
    }

    private ImageReadParam readParam() {
        DicomImageReadParam param =
                (DicomImageReadParam) iReader.getDefaultReadParam();
        //param.setWindowCenter(windowCenter);
        //param.setWindowWidth(windowWidth);
        param.setAutoWindowing(false);
        //param.setWindowIndex(windowIndex);
        //param.setVOILUTIndex(voiLUTIndex);
        //param.setPreferWindow(preferWindow);
        //param.setPresentationState(prState);
        //param.setOverlayActivationMask(overlayActivationMask);
        //param.setOverlayGrayscaleValue(overlayGrayscaleValue);
        return param;
    }
    
    
    private static final Logger logger = LogManager.getLogger(DCMImageLoader.class);
}