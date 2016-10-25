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
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Modality;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.PhaseInformation;
import com.ivli.roim.core.Photometric;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che3.io.DicomInputStream;

/**
 *
 * @author likhachev
 */
class DCMImageProvider implements IImageProvider {    
    
     static {
        ImageIO.scanForPlugins();
    }

    private static ImageReader installImageReader() {
        ImageReader ir = ImageIO.getImageReadersByFormatName("DICOM").next(); //NOI18N

        if (null == ir) {
            LOG.error("It seems there's no DICOM reader available, make a try to install one"); //NOI18N
            IIORegistry registry = IIORegistry.getDefaultInstance();
            registry.registerServiceProvider(new DicomImageReaderSpi());
            ir = ImageIO.getImageReadersByFormatName("DICOM").next();  //NOI18N
        }

        return ir;
    }

    private final ImageReader iReader;
    private final Attributes iDataSet;               
    
    protected DCMImageProvider(final String aFile) throws IOException {
        File f = new File(aFile);
        DicomInputStream dis = new DicomInputStream(f);
        iDataSet = dis.readDataset(-1, -1);   
        iReader = installImageReader();
        iReader.setInput(ImageIO.createImageInputStream(f));  
        
        dumpFileInmormation(aFile);   
    }
    
    private void dumpFileInmormation(String aN) {        
        StringBuilder sb = new StringBuilder();
        sb.append("\n-----------------------------------");
        sb.append("\nFILE: ");
        sb.append(aN);       
        sb.append("\nMODALITY: ");
        sb.append(getModality());
        sb.append("\nTYPE: ");
        sb.append(getImageType());        
        sb.append("\nPHOTOMETRIC INTERPRETATION: ");
        sb.append(getPhotometric());
        sb.append(String.format("\nFRAMES:%d; WIDTH:%d; HEIGHT:%d", getNumFrames(), getWidth(), getHeight()));
        sb.append("\nTIMESLICE VECTOR:");
        sb.append(getTimeSliceVector());
        sb.append("\nP-VALUE TRANSFORM:");
        sb.append(getRescaleTransform());                
        sb.append("\n-----------------------------------\n");
        LOG.info(sb);
    }
    
    public Modality getModality() {
        Object o = iDataSet.getValue(Tag.Modality);        
        
        if (null != o) {            
            final String s = new String((byte[])o);
            for(Modality t : Modality.values())
                if (s.contains(t.iName))
                    return t;
        }
        return Modality.UNKNOWN;
    }
    
    public Photometric getPhotometric() {
        Object o = iDataSet.getValue(Tag.PhotometricInterpretation);        
        
        if (null != o) {            
            final String s = new String((byte[])o);
            for(Photometric t : Photometric.values())
                if (s.contains(t.iName))
                    return t;
        }
        return Photometric.UNKNOWN;
    }
        
    public int getWidth() {        
        return iDataSet.getInt(Tag.Columns, 0);        
    }

    public int getHeight() {       
        return iDataSet.getInt(Tag.Rows, 0);
    }
   
    public int getNumFrames() {     
        if (getModality() == Modality.CR)
            return 1;
        else if (getModality() == Modality.CT && getImageType() == ImageType.AXIAL)
            return 1;
        else if (getModality() == Modality.UNKNOWN)
            return 1;
        return iDataSet.getInt(Tag.NumberOfFrames, 0);      
    }
     
    public ImageDataType getImageDataType() {
        return ImageDataType.GRAYS32;
    }
    
    //TODO: dcm4chee applies this transform to image data before ??? 
    public PValueTransform getRescaleTransform() { 
        /*
        double  s = iDataSet.getDouble(Tag.RescaleSlope, 1.);   
        double  i = iDataSet.getDouble(Tag.RescaleIntercept, .0);                    
        return new PValueTransform(s, i);           
        */
        return PValueTransform.DEFAULT_TRANSFORM;
    }
     
    public int[] readFrame(int anIndex, int[] aBuffer) throws IndexOutOfBoundsException, IOException {        
        final int w = getWidth();
        final int h = getHeight();  
        
        LOG.debug("Loading frame # = {}", anIndex);
        
        if (null == aBuffer)
            aBuffer = new int[w*h];       
        else if (aBuffer.length != w*h)
            throw new IllegalArgumentException();
                      
        return iReader.readRaster(anIndex, readParam()).getSamples(0, 0, w, h, 0, aBuffer);       
    }

    public TimeSliceVector getTimeSliceVector() {
        ArrayList<PhaseInformation> phases = new ArrayList();

        Sequence pid = (Sequence) iDataSet.getValue(Tag.PhaseInformationSequence);

        if (null != pid) {
            for (Attributes a : pid) {
                int fd = a.getInt(Tag.ActualFrameDuration, 1);
                int nf = a.getInt(Tag.NumberOfFramesInPhase, 1);
                phases.add(new PhaseInformation(nf, fd));
            }
        } else {
            // either image is single frame or phase information is not present
            //if (getNumFrames() != 1) {
            //    LOG.info("file is suspicious");
           // }
            phases.add(PhaseInformation.ONESHOT);//new PhaseInformation(Math.max(1, getNumFrames()), iDataSet.getInt(Tag.ActualFrameDuration, 1000)));
        }

        return new TimeSliceVector(phases);
    }

    public PixelSpacing getPixelSpacing() {
        double[] ps = iDataSet.getDoubles(Tag.PixelSpacing);
        if (null != ps && ps.length >= 2) {
            return new PixelSpacing(ps[0], ps[1]);
        } else {
            return PixelSpacing.UNITY_PIXEL_SPACING;
        }
    }

     @Override
    public double getMin() {
        return iDataSet.getDouble(Tag.SmallestImagePixelValue, Double.NaN);       
    }

     @Override
    public double getMax() {
        return iDataSet.getDouble(Tag.LargestImagePixelValue, Double.NaN);       
    }

     @Override
    public ImageType getImageType() {
        Object o = iDataSet.getValue(Tag.ImageType);    //TODO: use getString    
        return ImageType.create((null != o) ? new String((byte[])o) : "");
    }

     @Override
    public SliceSpacing getSliceSpacing() {
        return SliceSpacing.UNITY_SLICE_SPACING;
    }
    
    private ImageReadParam readParam() {
        DicomImageReadParam param = (DicomImageReadParam) iReader.getDefaultReadParam();                        
        param.setAutoWindowing(false);     
       
        return param;
    }
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();       
}
