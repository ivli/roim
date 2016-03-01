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
package com.ivli.roim;

import java.awt.RenderingHints;

/**
 *
 * @author likhachev
 */
public class InterpolationMethod {
    public static final int INTERPOLATION_NEAREST_NEIGHBOR = 0;
    public static final int INTERPOLATION_BILINEAR = 1;
    public static final int INTERPOLATION_BICUBIC = 2;
    
    public static Object get(int aMethod) {        
        switch(aMethod) {            
            case INTERPOLATION_BILINEAR: return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case INTERPOLATION_BICUBIC: return RenderingHints.VALUE_INTERPOLATION_BICUBIC;//
            case INTERPOLATION_NEAREST_NEIGHBOR: //fall through
            default: return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
        }
    }
    
    public static int get(Object aMethod) {        
        if(aMethod.equals(RenderingHints.VALUE_INTERPOLATION_BILINEAR))
            return INTERPOLATION_BILINEAR;
            
        if(aMethod.equals(RenderingHints.VALUE_INTERPOLATION_BICUBIC))
            return INTERPOLATION_BICUBIC;
            
        //if(aMethod.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR))
        else
            return INTERPOLATION_NEAREST_NEIGHBOR;
       
    }
    
}
