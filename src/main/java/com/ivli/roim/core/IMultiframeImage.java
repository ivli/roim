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
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends Iterable<ImageFrame>, java.io.Serializable {      
    int getWidth();  
    int getHeight();
    PixelSpacing getPixelSpacing();   
    TimeSliceVector getTimeSliceVector();
    int getNumFrames();  
    
     //
    boolean hasAt(int aFrameNumber);
    
     //returns a frame at aFrameNumber leaving cursor unaltered 
    ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException; 
    
        
    @Override
    default public java.util.Iterator<ImageFrame> iterator() {    
        return new java.util.Iterator<ImageFrame>() {
            int _next = 0;
            @Override
            public boolean hasNext() {    
                return hasAt(_next);
            }

            @Override
            public ImageFrame next() {
                return getAt(_next++);
            }  
        };
    }
}
