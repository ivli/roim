/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;


import java.io.IOException;
import java.util.ArrayList;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiframeImage {//extends ImageBase {
 
    private static final boolean LOAD_ON_DEMAND = false;
    
    ArrayList<ImageFrame> iFrames;    
    TimeSliceVector       iTimeSlices;     
    
    private int iCurrent = 0;
    
    private MultiframeImage() {
        iFrames = new ArrayList();
    }
    
    static MultiframeImage New(final String aFile) throws IOException {
        MultiframeImage self = new MultiframeImage();
        self.open(aFile);
        return self;
    }
     
    int getFrameNumber() {
        return iCurrent;
    }
    
    ImageFrame getCurrentFrame() { 
        return iFrames.get(getFrameNumber());
    }
    
    int getWidth() {
        return getCurrentFrame().getWidth();
    }
    
    int getHeight() {
        return getCurrentFrame().getHeight();
    }  
    
    int getNumFrames() throws IOException {
        return iLoader.getNumImages();
    }
    
    public void open(String aFile) throws IOException {           
        iLoader.open(aFile);
        iTimeSlices = iLoader.getTimeSliceVector();        
       
        iFrames.clear();
        iFrames.ensureCapacity(getNumFrames());
        
        if (!LOAD_ON_DEMAND) 
            for (int i = 0; i < getNumFrames(); ++i)
                loadFrame(i);

        
        loadFrame(0);
    }
                 
    public BufferedImage getBufferedImage() {
        return convert((WritableRaster)getCurrentFrame().iRaster);
    }     
    
    public void loadFrame(int anIndex) throws IndexOutOfBoundsException{
        try{
            if (!(anIndex < getNumFrames() && anIndex >= 0))
                throw new IndexOutOfBoundsException();
        
                // load and cache image if it is not yet in cache
            if ((iCurrent = anIndex) >= iFrames.size() || null == iFrames.get(iCurrent)) { 
                ImageFrame r = new ImageFrame(iLoader.readRaster(iCurrent));
                iFrames.add(iCurrent, r);
            }    
           
            logger.info("Frame -" + getFrameNumber() +                       // NOI18N
                        ", MIN"   + getCurrentFrame().getStats().getMin() +  // NOI18N
                        ", MAX"   + getCurrentFrame().getStats().getMax() +  // NOI18N
                        ", DEN"   + getCurrentFrame().getStats().getIden()); // NOI18N
        } catch (IOException ex) {
            logger.error(ex); 
        } 
    }
         
    private BufferedImage convert(WritableRaster raster) {
        ColorModel cm ;
       // if (pmi.isMonochrome()) {
           
            cm = createColorModel(8, DataBuffer.TYPE_USHORT);//TYPE_BYTE);
          //  SampleModel sm = createSampleModel(DataBuffer.TYPE_BYTE, false);
          //  raster = applyLUTs(raster, frameIndex, param, sm, 8);
          //  for (int i = 0; i < overlayGroupOffsets.length; i++) {
          //      applyOverlay(overlayGroupOffsets[i], 
          //              raster, frameIndex, param, 8, overlayData[i]);
       //     }
      //  } else {
      //      cm = createColorModel(bitsStored, dataType);
      //  }
        //WritableRaster r = raster.createCompatibleWritableRaster();
        return new BufferedImage(cm, raster , false, null);
    }
    
    static ColorModel createColorModel(int bits, int dataType) {
        return new ComponentColorModel(
                        ColorSpace.getInstance(ColorSpace.CS_GRAY),
                        new int[] { bits },
                        false, // hasAlpha
                        false, // isAlphaPremultiplied
                        Transparency.OPAQUE,
                        dataType);
    }
    
    public BufferedImage makeCompositeFrame(int aFrom, int aTo) throws IOException {
        if (-1 == aTo)
            aTo = getNumFrames();

        assert (aFrom >= 0 && aFrom < getNumFrames() || aTo > aFrom || aFrom < getNumFrames());  
        
        WritableRaster composite = iFrames.get(0).iRaster.createCompatibleWritableRaster();
        
        final int cols = iFrames.get(0).iRaster.getWidth();
        final int rows = iFrames.get(0).iRaster.getHeight();
        
        for (int n = aFrom; n < aTo; ++n) {
            final Raster r = iFrames.get(n).iRaster;
            for (int i = 0; i < cols; ++i)
               for (int j = 0; j < rows; ++j) 
                   composite.setSample(i, j, 0, composite.getSample(i, j, 0) + r.getSample(i, j, 0));           
        }
        
        return convert(composite);
    }    
    
    Curve makeCurveFromRoi(ROI aRoi) {
        Curve ret = new Curve(aRoi.getName());  
        
        ROIExtractor r = new ROIExtractor(aRoi.getShape());
        
        for (ImageFrame f:iFrames) {
            r.apply(f.iRaster);
            ret.add(new Measure(r.iStats.iMin, r.iStats.iMax, r.iStats.iIden));
        }
        
        return ret;
    }
    
    public void extract(Extractor aEx) {
        aEx.apply(getCurrentFrame().getRaster());
    }
     
    
    private ImageLoader iLoader = new ImageLoader(); 
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

