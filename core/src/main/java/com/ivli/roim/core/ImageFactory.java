/*
 * Copyright (C) 2021 likhachev
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
