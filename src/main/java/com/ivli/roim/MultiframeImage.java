
package com.ivli.roim;


import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiframeImage implements IMultiframeImage, java.io.Serializable {
    private final IImageProvider iSrc;
    private int iCurrent;
    
    public MultiframeImage(IImageProvider aSrc) {
        iSrc = aSrc;
        iCurrent = 0;
    }
   
    @Override
    public boolean hasAt(int aFrameNumber) {
        try {
            iSrc.loadFrame(aFrameNumber);
        } catch (IOException | IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }
    
    public ImageFrame current(int aFrameNumber) throws java.util.NoSuchElementException {           
        ImageFrame ret = null;
        try {
            ret = iSrc.loadFrame(aFrameNumber); //prevent iCurrent from change in the case of exception
            iCurrent = aFrameNumber;
        } catch (IOException ex) {
            throw( new java.util.NoSuchElementException());
        }
        return ret;
    } 
    
    public ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException {           
        ImageFrame ret = null;
        try {
            ret = iSrc.loadFrame(aFrameNumber); //prevent iCurrent from change in the case of exception           
        } catch (IOException ex) {
            throw (new java.util.NoSuchElementException());
        }
        return ret;
    } 
    
    public int getCurrent() {
        return iCurrent;
    }
    
    public ImageFrame image() { 
        return getAt(iCurrent);
    }
    
    public int getNumFrames() {
        int ret = 0;
        try {
            ret = iSrc.getNumFrames();
        } catch (IOException ex) {
            logger.error(ex);
        }
        return ret;
    }
    
    public int getWidth() {
        return iSrc.getWidth();
    }
    
    public int getHeight() {
        return iSrc.getHeight();
    }  
    
    public PixelSpacing getPixelSpacing() {
        return iSrc.getPixelSpacing();
    }
              
    public IMultiframeImage makeCompositeFrame(int aFrom, int aTo)  {
        if (-1 == aTo)
            aTo = getNumFrames();

        assert (aFrom >= 0 && aFrom < getNumFrames() || aTo > aFrom || aFrom < getNumFrames());  
        
        java.awt.image.WritableRaster comp = getAt(0).iRaster.createCompatibleWritableRaster();
                
        for (int n = aFrom; n < aTo; ++n) {
            final java.awt.image.Raster r = getAt(n).iRaster;
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   comp.setSample(i, j, 0, comp.getSample(i, j, 0) + r.getSample(i, j, 0));           
        }
     
        return new MultiframeImage(new VirtualImageProvider(iSrc, new ImageFrame(comp))); 
    }    
    
    
    
    public void extract(Extractor aEx) {  
       aEx.apply(image().getRaster());   
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

