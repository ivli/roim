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

import com.ivli.roim.core.Window;

/**
 *
 * @author likhachev
 */
public final class WindowChangeEvent extends java.util.EventObject {   
    //private final boolean iRangeChanged;
    //private final double  iMin;
    //private final double  iMax;
    private final Window  iWindow; 
    
    /*
    public WindowChangeEvent(Object aO, Window aW, double aMin, double aMax) {
        super(aO); 
        iWindow = aW;
       // iMin = aMin;
       // iMax = aMax;        
    }
    */
    
    public WindowChangeEvent(Object aO, Window aW) {
        super(aO); 
        iWindow = aW;
       // iMin = 0;
       // iMax = 256;
        //iRangeChanged = false;      
    }
    
    public final Window getWindow() {return iWindow;}   
   // public final double getMin() {return iMin;}
   // public final double getMax() {return iMax;}
   // public final boolean isRangeChanged() {return iRangeChanged;}    
}


