/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.core;

import com.ivli.roim.events.ProgressListener;
import com.ivli.roim.io.ImageServiceProvider;
import com.ivli.roim.io.spi.IImageService;
import com.ivli.roim.io.spi.ImageServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class ImageFactory {            
    public static IMultiframeImage create(final String aFileName, ProgressListener aPL) {
        ImageServiceProvider s = ImageServiceProvider.getInstance();
        
        final String ext = aFileName.substring(aFileName.lastIndexOf('.') + 1).toLowerCase();
        IImageService is = s.service(ext);
        
        if (null == is) {
            LOG.error("No SPI found for file " + aFileName);
            return null;            
        }
        try {
            is.open(aFileName);  
            
        } catch (ImageServiceException ex) {
            LOG.throwing(ex);
        }
        return MultiframeImage.create(is);
    }
    
    static Logger LOG = LogManager.getLogger();
}
