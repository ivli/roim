/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class DICOMImage extends IImage {
   // public class DICOMMultiframeImage {
    private static final boolean LOAD_ON_DEMAND = false;
    
    private ArrayList<ImageFrame> iFrames;    
    TimeSliceVector           iTimeSlices;     
    
   // private int iCurrent = 0;
    
    private DICOMImage() {
        iFrames = new ArrayList();
    }
    
    static DICOMImage New(final String aFile) throws IOException {
        DICOMImage self = new DICOMImage();
        self.open(aFile);
        return self;
    }
     
      
    public int getWidth() {
        return iFrames.get(0).getWidth();
    }
    
    public int getHeight() {
        return iFrames.get(0).getHeight();
    }  
    
    public int getNumFrames() {
        try {
            return iLoader.getNumImages();
        } catch (IOException e) {
            return -1;
        }
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
      
    ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException {
        try{
            if (anIndex > getNumFrames() || anIndex < 0)
                throw new IndexOutOfBoundsException();
        
                // load and cache image if it is not yet in cache
            if ((anIndex) >= iFrames.size() || null == iFrames.get(anIndex)) { 
                ImageFrame r = new ImageFrame(iLoader.readRaster(anIndex));
                iFrames.add(anIndex, r);
            }    
           
            logger.info("Frame -" + anIndex +                       // NOI18N
                        ", MIN"   + iFrames.get(anIndex).getStats().getMin() +  // NOI18N
                        ", MAX"   + iFrames.get(anIndex).getStats().getMax() +  // NOI18N
                        ", DEN"   + iFrames.get(anIndex).getStats().getIden()); // NOI18N
        } catch (IOException ex) {
            logger.error(ex); 
        } 
        
        return iFrames.get(anIndex);
    }
   
    public IMultiframeImage image() {
        return new MultiframeImage(this);
    }
    
    /* 
    
    public BufferedImage getBufferedImage() {
        return convert((WritableRaster)getCurrentFrame().iRaster);
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
    */
    
    /*
    public BufferedImage makeCompositeFrame(int aFrom, int aTo) throws IOException {
        if (-1 == aTo)
            aTo = getNumFrames();

        assert (aFrom >= 0 && aFrom < getNumFrames() || aTo > aFrom || aFrom < getNumFrames());  
        
        WritableRaster composite = iFrames.get(0).iRaster.createCompatibleWritableRaster();
                
        for (int n = aFrom; n < aTo; ++n) {
            final Raster r = iFrames.get(n).iRaster;
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   composite.setSample(i, j, 0, composite.getSample(i, j, 0) + r.getSample(i, j, 0));           
        }
        
        return convert(composite);
    }    
    */
    
    
     
    
    private ImageLoader iLoader = new ImageLoader(); 
    
    private static final Logger logger = LogManager.getLogger(DICOMImage.class);    
 
}


