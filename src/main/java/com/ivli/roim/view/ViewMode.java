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
package com.ivli.roim.view;

import com.ivli.roim.core.IFrameProvider;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageType;

/**
 *
 * @author likhachev
 */
public class ViewMode {
    enum VIEWMODETYPE {
        DEFAULT, //
        FRAME, // display single frame of a number 
        CINE,  // display single frame, can move from first to last
        RANGE, // display single frame, can move within the range
        COMPOSITE, // composite frame (a summ of frames within the range)
        SLICE, // single slice at a number
        VOLUME // MIP volume
    }

    public static final ViewMode DEFAULT = new ViewMode(VIEWMODETYPE.DEFAULT, IFrameProvider.FIRST, IFrameProvider.LAST);
    public static final ViewMode DEFAULT_IMAGE_MODE = DEFAULT;//new ViewMode(VIEWMODETYPE.FRAME, 0, IFrameProvider.LAST);
    public static final ViewMode DEFAULT_STATIC_IMAGE_MODE = DEFAULT_IMAGE_MODE;
    public static final ViewMode DEFAULT_DYNAMIC_IMAGE_MODE = new ViewMode(VIEWMODETYPE.CINE, IFrameProvider.FIRST, IFrameProvider.LAST);
    public static final ViewMode DEFAULT_TOMO_IMAGE_MODE = new ViewMode(VIEWMODETYPE.CINE, IFrameProvider.FIRST, IFrameProvider.LAST);
    public static final ViewMode DEFAULT_VOLUME_IMAGE_MODE = ViewMode.volume(0, -1);
    public static final ViewMode DEFAULT_COMPOSITE_IMAGE_MODE = ViewMode.composite(IFrameProvider.FIRST, IFrameProvider.LAST);    
    public static final ViewMode DEFAULT_DUNAMIC_SECOND_IMAGE_MODE = DEFAULT_COMPOSITE_IMAGE_MODE;    
    public static final ViewMode DEFAULT_TOMO_SECOND_IMAGE_MODE = DEFAULT_VOLUME_IMAGE_MODE;
    
    public static ViewMode range(int aFrom, int aTo) {
        return new ViewMode(VIEWMODETYPE.RANGE, aFrom, aTo);
    }

    public static ViewMode composite(int aSummFrom, int aSummTo) {
        return new ViewMode(VIEWMODETYPE.COMPOSITE, aSummFrom, aSummTo);
    }

    public static ViewMode slice(int aSliceNumber) {
        return new ViewMode(VIEWMODETYPE.FRAME, aSliceNumber, IFrameProvider.LAST);
    }

    public static ViewMode volume(int aSliceFrom, int aSliceTo) {
        return new ViewMode(VIEWMODETYPE.VOLUME, aSliceFrom, aSliceTo);
    }

    public boolean isCompatible(IMultiframeImage aI) {           
        switch (iType) {            
            case FRAME: {               
                switch (aI.getImageType().getTypeName()) {
                    case ImageType._IMAGE:
                    case ImageType.NM_STATIC:
                    case ImageType.NM_DYNAMIC:
                    case ImageType.NM_GATED:
                    case ImageType.NM_WHOLEBODY:
                        if (aI.hasAt(iFrameFrom))                
                            return true;    
                }
            }                
            case CINE:  
            case RANGE:
            case COMPOSITE:
                switch (aI.getImageType().getTypeName()) {                        
                    case ImageType.NM_DYNAMIC:
                    case ImageType.NM_GATED:     
                        if (aI.hasAt(iFrameFrom) && aI.hasAt(iFrameTo))
                            return true;                   
                }                

            case SLICE:  ///break;  
            case VOLUME:  
                switch (aI.getImageType().getTypeName()) {                        
                    case ImageType.NM_VOLUME:
                    case ImageType.NM_VOLUME_G:                       
                        return true;                   
                }  
            case DEFAULT:
                return true;
        }

        return false;
    }

    private ViewMode(VIEWMODETYPE aType, int aF, int aT) {
        iType = aType;
        iFrameFrom = aF;
        iFrameTo = aT;
    }
        
    VIEWMODETYPE iType;
    int iFrameFrom;
    int iFrameTo; 
}
