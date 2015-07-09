/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.File;
import java.io.IOException;
import java.awt.image.Raster;
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
import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;
/* ENDIF */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ImageLoader {
    
    static {   
        ImageIO.scanForPlugins(); 
    }
 
    private ImageReader iReader = _installImageReader();   
  
    public ImageReader getImageReader() {
        return iReader;
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
       
    public void open(String aFile) throws IOException {
        File src = new File(aFile);   
        Attributes fmi;
        Attributes dataset;
        try (DicomInputStream dis = new DicomInputStream(src)) {            
            fmi = dis.readFileMetaInformation();
            dataset = dis.readDataset(-1, -1);
        } catch (Exception e) {
            logger.info(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle/Bundle_ru_RU").getString("CANNOT READ DICOM DATASET"));              
        }
        
        ImageInputStream iis = ImageIO.createImageInputStream(src);
        iReader.setInput(iis);  
    }
    
    public int getNumImages() throws IOException {return iReader.getNumImages(false);}    

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
    
    
    private static final Logger logger = LogManager.getLogger(ImageLoader.class);
}