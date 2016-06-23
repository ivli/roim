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

import com.ivli.roim.view.Overlay;

/**
 *
 * @author likhachev
 */
public class OverlayChangeEvent extends java.util.EventObject {    
    public final static int CHANGEDCOLOR = 5; //ROI's colour has been changed
    public final static int CHANGEDNAME = 6;  //ROI's name has been changed      
    
    private final int iChange;   //what happened
    private final Overlay iObj;   //object    
    private final Object  iExtra; //depending on change it carries old name, old colour or ... 
    
    public OverlayChangeEvent(Object aO, int aC, Overlay aR, Object aExtra) {
        super (aO);
        iObj = aR;
        iChange = aC;
        iExtra = aExtra;
    }
    
    public int getChange() {
        return iChange;
    }
       
    public Overlay getObject() {
        return iObj;
    }   
    
    public Object getExtra() {
        return iExtra;
    }    
}
