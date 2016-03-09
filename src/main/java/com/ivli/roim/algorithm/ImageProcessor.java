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
package com.ivli.roim.algorithm;

import com.ivli.roim.InterpolationMethod;
import com.ivli.roim.core.IMultiframeImage;

/**
 *
 * @author likhachev
 */
public class ImageProcessor {    
    private final IMultiframeImage iImage;
    private int iInterpol;
    
    public ImageProcessor(IMultiframeImage anImage) {
        iImage = anImage;
        iInterpol = InterpolationMethod.INTERPOLATION_NONE;
    }
    
    public void setInterpolation(boolean aI) {
        iInterpol = aI ? InterpolationMethod.INTERPOLATION_BILINEAR : InterpolationMethod.INTERPOLATION_NONE;  
    }
    
    public void flipVert(int aFrom, int aTo) {         
        if (aFrom < 0 || aTo > iImage.getNumFrames())
            throw new IllegalArgumentException("wrong frame number");
      
        for (int i = aFrom; i < aTo; ++i) {
            FrameProcessor fp = new FrameProcessor(iImage.get(i), iInterpol);
            fp.flipVert();
        }
    }
    
    public void flipHorz(int aFrom, int aTo) {          
        if (aFrom < 0 || aTo > iImage.getNumFrames())
            throw new IllegalArgumentException("wrong frame number");
                      
        for (int i = aFrom; i < aTo; ++i) {
            FrameProcessor fp = new FrameProcessor(iImage.get(i), iInterpol);
            fp.flipHorz();
        }
    }
         
    public void rotate(final double anAngle, int aFrom, int aTo) {             
        if (aFrom < 0 || aTo > iImage.getNumFrames())
            throw new IllegalArgumentException("wrong frame number");
                    
        for (int i = aFrom; i < aTo; ++i) {
            FrameProcessor fp = new FrameProcessor(iImage.get(i), iInterpol);
            fp.rotate(anAngle);
        }
    }
    
    public void flipVert() {
        flipVert(0, iImage.getNumFrames());
    }
    
    public void flipHorz() {
        flipHorz(0, iImage.getNumFrames());
    }
            
    public void rotate(final double anAngle) {
        rotate(anAngle, 0, iImage.getNumFrames());
    }           
}
