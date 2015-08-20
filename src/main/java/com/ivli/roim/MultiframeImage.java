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


public class MultiframeImage extends IMultiframeImage {
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
    
    public ImageFrame current() { 
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
        aEx.apply(current().getRaster());
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

