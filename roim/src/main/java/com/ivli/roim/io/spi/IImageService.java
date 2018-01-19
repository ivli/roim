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
package com.ivli.roim.io.spi;

import com.ivli.roim.core.IImage;
import java.io.IOException;
/**
 *  
 * @author likhachev
 * 
 */
public interface IImageService {      
    public String[] getSuffixes();
    public String getDescription();
       
    /**
     *
     * @param aFullPath
     * @throws ImageServiceException
     */
    public void open(final String aFullPath) throws ImageServiceException;
    public void close();
    public IImage image();
    public int getNumFrames();
    /**      
     * @param aFrameNumber frame index must be in range {@code 0 >= anIndex < getNumFrames() } otherwise <i>IndexOutOfBoundsException</i>
     * @param aBuffer preallocated buffer of appropriate size {@code getImageDataType().bufferSize(getWidth(), getHeight());},
     *                a null value is permitted then it returns newly allocated buffer  
     * @return a buffer containing an image frame referred by <i>aFrameNumber</i> as a row of pixels 
     * @throws ImageServiceException 
     */
    public int[] readFrame(int aFrameNumber, int [] aBuffer) throws ImageServiceException;         
               
}
