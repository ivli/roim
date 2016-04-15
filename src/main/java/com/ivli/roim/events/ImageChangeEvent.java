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
package com.ivli.roim.events;

import com.ivli.roim.core.Range;
import com.ivli.roim.core.TimeSlice;

/**
 *
 * @author likhachev
 */
public class ImageChangeEvent extends java.util.EventObject {    
    private final int iFrame;//a number of current frame  
    private final int iTotal;//a number of frames in image
    
    private final Range iRange; //global range of the image
    private final TimeSlice iTimeSlice; // valid only for DYNAMIC images
    private final Double  iAngularStep;  // valid only for TOMO & TOMO_RECON images
    
    public ImageChangeEvent(Object aO, int aFrame, int aTotal, Range aRange, TimeSlice aTimeSlice) {
        super(aO); 
        iFrame = aFrame;
        iTotal = aTotal;
        iRange = aRange;
        iTimeSlice = aTimeSlice;
        iAngularStep = Double.NaN;
    }      
}
