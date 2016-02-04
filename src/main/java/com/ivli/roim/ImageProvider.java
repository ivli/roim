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


import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.ImageFrame;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public abstract class ImageProvider {               
    protected int iWidth;
    protected int iHeight;
    protected int iNoOfFrames;   
    //protected Double iMin = Double.NaN;
    //protected Double iMax = Double.NaN;
    
    protected PixelSpacing iPixelSpacing;
    protected TimeSliceVector iTimeSlices; 
    protected ArrayList<ImageFrame> iFrames;
        
    protected ImageProvider() {        
        iFrames = new ArrayList();        
    }
    
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
    }  
   
    public int getNumFrames() {       
        return iNoOfFrames;    
    }
    
    /*
    protected void calcGlobals() {
        Double min = Double.MAX_VALUE;
        Double max = Double.MIN_VALUE;
        
        for (int i = 0; i < getNumFrames(); ++i) {
            final ImageFrame f = frame(i);
            if (f.getMin() < min)
                min = f.getMin();
            else if (f.getMax() > max)
                max = f.getMax();
        }
        
        iMin = min;
        iMax = max;
    }
    
    public double getMin() {
        if (!iMin.isNaN())
            return iMin;
        else {
           calcGlobals();
           return iMin;
        }
    }
    
    public double getMax() {
        if (!iMax.isNaN())
            return iMax;
        else {       
            calcGlobals(); 
            return iMax; 
        }        
    }
    */
    
    public PixelSpacing getPixelSpacing() {
        return iPixelSpacing;        
    }
    
   
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSlices;
    }
    
   
    public ImageFrame frame(int anIndex) throws IndexOutOfBoundsException/*, IOException */{
       return iFrames.get(anIndex);
    }
    
   
    public ImageProvider slice(TimeSlice aS) {
        VirtualImageProvider ret = new VirtualImageProvider(this);
        
        return ret;
    }
       
    public ImageProvider collapse(TimeSlice aS) /*throws IOException */{   
        int frameTo = aS.getTo().isInfinite() ? getNumFrames() : iTimeSlices.frameNumber(aS.getTo());
        int frameFrom = iTimeSlices.frameNumber(aS.getFrom());        
       
        
        java.awt.image.WritableRaster comp = iFrames.get(0).getRaster().createCompatibleWritableRaster();
                
        for (int n = frameFrom; n < frameTo; ++n) {
            final java.awt.image.Raster r = frame(n).getRaster();
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   comp.setSample(i, j, 0, comp.getSample(i, j, 0) + r.getSample(i, j, 0));           
        
        }
        
        VirtualImageProvider ret = new VirtualImageProvider(this);
        
        ret.iTimeSlices = iTimeSlices.slice(aS);
        ret.iFrames.add(new ImageFrame(comp));
        ret.iNoOfFrames = 1;
        
        return ret; 
    }
    
    private static final Logger logger = LogManager.getLogger(ImageProvider.class);     
}


