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
package com.ivli.roim;

import com.ivli.roim.core.TimeSlice;
import java.io.IOException;
import com.ivli.roim.core.PixelSpacing;

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
    
    @Override
    public ImageFrame advance(int aFrameNumber) throws java.util.NoSuchElementException {           
        ImageFrame ret = null;
        try {
            ret = iSrc.frame(aFrameNumber); //prevent iCurrent from change in the case of exception
            iCurrent = aFrameNumber;
        } catch (IOException ex) {
            throw( new java.util.NoSuchElementException());
        }
        return ret;
    } 
    
    @Override
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
            logger.error("FATAL!", ex);
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
            logger.error("FATAL!", ex);
        }
        return ret;
    }    
    
    public void extract(Extractor aEx) {  
       aEx.apply(image().getRaster());   
    }
    
    private static final Logger logger = LogManager.getLogger(MultiframeImage.class);    
}

