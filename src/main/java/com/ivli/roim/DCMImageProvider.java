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


import java.io.IOException;
import com.ivli.roim.core.IImageLoader;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.PixelSpacing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class DCMImageProvider extends ImageProvider {
    private static final boolean LOAD_ON_DEMAND = true;
    private static final int     FIRST_FRAME_TO_LOAD = 0;
    
    protected final IImageLoader iLoader;
    
    public DCMImageProvider(String aFile) throws IOException {
        iLoader = new DCMImageLoader(); 
        
        try {           
            iLoader.open(aFile);
            iTimeSlices = iLoader.getTimeSliceVector();        

            try{
                iPixelSpacing = iLoader.getPixelSpacing();
            } catch (IOException ex) {

                iPixelSpacing = new PixelSpacing(1.0, 1.0);
            }
            
            iNoOfFrames = iLoader.getNumImages();
            iFrames.clear();
            iFrames.ensureCapacity(iNoOfFrames);

            if (!LOAD_ON_DEMAND) 
                for (int i = 0; i < iNoOfFrames; ++i)
                    doLoadFrame(i);

            /**/
            ImageFrame f = doLoadFrame(FIRST_FRAME_TO_LOAD);

            iWidth = f.getWidth();
            iHeight = f.getHeight();        
        } catch (IOException ex) {
            logger.error("FATAL!!", ex);
        }            
    }  
    
    @Override
    public ImageFrame frame(int anIndex) throws IndexOutOfBoundsException/*, IOException*/ {
        return doLoadFrame(anIndex);
    }
    
    protected ImageFrame doLoadFrame(int anIndex) throws IndexOutOfBoundsException/*, IOException */ {        
        if (anIndex > getNumFrames() || anIndex < 0)
            throw new IndexOutOfBoundsException();
        
        ImageFrame f = null;
        
        try {
            f = iFrames.get(anIndex);                        
        } catch (IndexOutOfBoundsException ex) {   
            try {
                f = new ImageFrame(iLoader.readRaster(anIndex));

                iFrames.add(anIndex, f);
                 //record only cache misses
                logger.info("Frame " + anIndex +                                  
                            ", MIN"   + f.getMin() +  // NOI18N
                            ", MAX"   + f.getMax() +  // NOI18N
                            ", DEN"   + f.getIden()); // NOI18N     
                
                  
            } catch (IOException sex) {
                logger.error("FATAL!!!", sex);
            }
        }
        return f;
    } 
                
    public double getMin() {
        if (!iMin.isNaN())
            return iMin;        
        try {
            return iLoader.getMin();
        } catch (IOException ex) {
            return super.getMin();
        }
    }
    
    public double getMax() {
        if (!iMax.isNaN())
            return iMax;
        
        try {
            return iLoader.getMax();
        } catch (IOException ex) {            
           return super.getMax(); 
        }
    }
    
    private static final Logger logger = LogManager.getLogger(DCMImageProvider.class);       
}
