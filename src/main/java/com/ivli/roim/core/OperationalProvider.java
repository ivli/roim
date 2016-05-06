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
package com.ivli.roim.core;

import com.ivli.roim.io.IImageProvider;

/**
 *
 * @author likhachev
 */
public abstract class OperationalProvider implements IImageProvider {
    IImageProvider iImage;// = mi2.createCompatibleImage(128);
   // MIPProjector prj = new MIPProjector(mi2, iImage);
   // boolean []b = new boolean[iImage.getNumFrames()];
    public int getWidth() {return iImage.getWidth();}  
    public int getHeight() {return iImage.getHeight();}   
    public int getNumFrames() {return iImage.getNumFrames();}      
    public ImageDataType getImageDataType(){return iImage.getImageDataType();}
    public ImageType getImageType(){return iImage.getImageType();}
    public PixelSpacing getPixelSpacing(){return iImage.getPixelSpacing();}
    public SliceSpacing getSliceSpacing(){return iImage.getSliceSpacing();}
    public TimeSliceVector getTimeSliceVector(){return iImage.getTimeSliceVector();}
    /*
    public ImageFrame get(int anIndex) throws IndexOutOfBoundsException{

        if (false != b[anIndex]) {
            return iImage.get(anIndex);                        
        } else {
            b[anIndex] = true;
            return prj.makeProjection(anIndex);
        }
    } 
*/
    public double getMin() {return iImage.getMin();}
    public double getMax() {return iImage.getMax();}
    public PValueTransform getTransform() {return iImage.getTransform();}


}
