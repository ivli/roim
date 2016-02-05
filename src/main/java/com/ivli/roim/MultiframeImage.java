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


import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.TimeSliceVector;
import java.util.ArrayList;


public class MultiframeImage extends IMultiframeImage {
    protected int iWidth;
    protected int iHeight;
    protected int iNumFrames;   
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    protected PixelSpacing iPixelSpacing;
    protected TimeSliceVector iTimeSlices; 
    protected ArrayList<ImageFrame> iFrames = new ArrayList();;
    protected final ImageProvider iProvider;
        
    public MultiframeImage(ImageProvider aP) {
        iProvider = aP;
        
        iWidth = iProvider.getWidth();
        iHeight = iProvider.getHeight();
        iNumFrames = iProvider.getNumFrames();   
        iMin = Double.NaN;
        iMax = Double.NaN;

        iPixelSpacing = iProvider.getPixelSpacing();
        iTimeSlices = iProvider.getTimeSliceVector(); 
        
        
        //ArrayList<ImageFrame> iFrames       
    }
   
    protected void computeStatistics() {
        
        iProvider.iFrames.stream().forEach((f) -> {
            if (f.getMin() < iMin)
                iMin = f.getMin();
            else if (f.getMax() > iMax)
                iMax = f.getMax();
        });               
    }
    
     @Override
    public int getWidth() {
        return iWidth;
    }
    
     @Override
    public int getHeight() {
        return iHeight;
    }  
    
     @Override
    public int getNumFrames() {       
        return iNumFrames;    
    }
    
     @Override
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSlices;
    }
    
     @Override
    public boolean hasAt(int aFrameNumber) {               
        return (aFrameNumber >=0 && aFrameNumber < iProvider.getNumFrames());          
    }
       
     @Override
    public ImageFrame get(int aFrameNumber) throws java.util.NoSuchElementException {                  
        return iProvider.frame(aFrameNumber);         
    }     
                 
     @Override
    public PixelSpacing getPixelSpacing() {
        return iProvider.getPixelSpacing();
    }
    
     @Override             
    public double getMin() { 
        if (Double.isFinite(iMin))
            computeStatistics();
        return iMin;
    }  
    
     @Override
    public double getMax() { 

        if (Double.isFinite(iMax))
            computeStatistics();
        return iMax;
    }  
}

