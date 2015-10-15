
package com.ivli.roim;

import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.core.PixelSpacing;
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
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.data.Attributes;

/* ENDIF */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dcm4che3.data.Tag;


public class ImageLoader {
    
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
        
    void open(String aFile) throws IOException {
         
        try (DicomInputStream dis = new DicomInputStream(new File(iFile = aFile))) {  
            
            iDataSet = dis.readDataset(-1, -1);//readFileMetaInformation();
           
        } catch (IOException e) {
            logger.error(e);              
        }        
        
        ImageInputStream iis = ImageIO.createImageInputStream(new File(aFile));
        iReader.setInput(iis);  
    }
    
    TimeSliceVector getTimeSliceVector() throws IOException {        
        //DicomInputStream dis = new DicomInputStream(new File(iFile));  
        //Attributes fmi;
        //Attributes ds = iDIS.readDataset(-1, -1);
        //fmi = dis.readFileMetaInformation();
         
        return new TimeSliceVector(iDataSet);
    }
    
    
    PixelSpacing getPixelSpacing() throws IOException {        
        double[] ps = iDataSet.getDoubles(Tag.PixelSpacing); 
        if (null != ps && ps.length >=2 )
            return new PixelSpacing (ps[0], ps[1]);
        else
            return new PixelSpacing();
    }
    
    int getNumImages() throws IOException {
        return iReader.getNumImages(false);
    }    

    Raster readRaster(int aIndex) throws IOException {
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