/*
 * Copyright (C) 2016 likhachev
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
package com.ivli.roim.io;

import com.ivli.roim.algorithm.MIPProjector;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;

/**
 *
 * @author likhachev
 */
public class MIPImageProvider implements IImageProvider { 
    protected MIPProjector iProvider;
    
    private final boolean []b;
    
    public MIPImageProvider(IMultiframeImage aSrc, int aProjections) {
        //iSrc = aSrc;
        iProvider = new MIPProjector(aSrc, aProjections);
         
        b = new boolean[aProjections];
    }
    
    public synchronized ImageFrame get(int anIndex) throws IndexOutOfBoundsException {
        if (anIndex < 0 || anIndex > iProvider.getNumFrames())
            throw new IndexOutOfBoundsException();
        
        if (false != b[anIndex]) {
            return iProvider.getDst().get(anIndex);                        
        } else {
            ImageFrame ret = iProvider.get(anIndex);
            b[anIndex] = true;
            return ret;
        }
    }    
    
    public int getWidth() {return iProvider.getDst().getWidth();}  
    public int getHeight() {return iProvider.getDst().getHeight();}       
    public int getNumFrames() {return iProvider.getDst().getNumFrames();}      
    public PValueTransform getRescaleTransform() {return iProvider.getDst().getRescaleTransform();}
    
    public ImageDataType getImageDataType(){return iProvider.getDst().getImageDataType();}
    public ImageType getImageType(){return iProvider.getDst().getImageType();}
    
    //TODO: following needs to get checked
    public PixelSpacing getPixelSpacing(){return iProvider.getDst().getPixelSpacing();}
    public SliceSpacing getSliceSpacing(){return iProvider.getDst().getSliceSpacing();}
    public TimeSliceVector getTimeSliceVector(){return iProvider.getDst().getTimeSliceVector();}
    
    public double getMin() {
        return iProvider.getDst().getMin();
    }
    
    public double getMax() {
        return iProvider.getDst().getMax();
    }        
}
