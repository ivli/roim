/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;


import java.util.Iterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiframeImage implements IMultiframeImage {
    private final DICOMImage iSrc;
    private int iCurrent;
    
    MultiframeImage(DICOMImage aSrc) {
        iSrc = aSrc;
        iCurrent = 0;
    }
   
    public boolean hasAt(int aFrameNumber) {
        try {
            iSrc.loadFrame(aFrameNumber);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }
    
    public ImageFrame getAt(int aFrameNumber) throws IndexOutOfBoundsException {         
        ImageFrame ret = iSrc.loadFrame(aFrameNumber); //prevent iCurrent from change in the case of exception
        iCurrent = aFrameNumber;
        return ret;
    } 
    public int getCurrentNo() {
        return iCurrent;
    }
    
    public ImageFrame image() { 
        return iSrc.loadFrame(iCurrent);
    }
    
    public int getNumFrames() {
        return iSrc.getNumFrames();
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
    
    public Curve makeCurveFromRoi(ROI aRoi) {
        Curve ret = new Curve(aRoi.getName());  
        /*
        ROIExtractor r = new ROIExtractor(aRoi.getShape());
        
        for (ImageFrame f : iSrc.iFrames) {
            r.apply(f.iRaster);
            ret.add(new Measure(r.iStats.iMin, r.iStats.iMax, r.iStats.iIden));
        }
        */
        return ret;
    }
    
    public void extract(Extractor aEx) {
        aEx.apply(image().getRaster());
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

