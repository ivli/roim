
package com.ivli.roim;


import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiframeImage implements IMultiframeImage {
    private final IImageProvider iSrc;
    private int iCurrent;
    
    public MultiframeImage(IImageProvider aSrc) {
        iSrc = aSrc;
        iCurrent = 0;
    }
   
    @Override
    public boolean hasAt(int aFrameNumber) {
        try {
            iSrc.frame(aFrameNumber);
        } catch (IOException | IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }
    
    public ImageFrame current(int aFrameNumber) throws java.util.NoSuchElementException {           
        ImageFrame ret = null;
        try {
            ret = iSrc.frame(aFrameNumber); //prevent iCurrent from change in the case of exception
            iCurrent = aFrameNumber;
        } catch (IOException ex) {
            throw( new java.util.NoSuchElementException());
        }
        return ret;
    } 
    
    public ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException {           
        ImageFrame ret = null;
        try {
            ret = iSrc.frame(aFrameNumber); //prevent iCurrent from change in the case of exception           
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
        MultiframeImage ret = null;        
        try {     
            ret = new MultiframeImage(iSrc.collapse(new TimeSlice (aFrom, aTo))); 
        } catch (IOException ex) {
            logger.error(ex);
        }
        return ret;
    }    
    
    public void extract(Extractor aEx) {  
       aEx.apply(image().getRaster());   
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

