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
package com.ivli.roim.events;

import com.ivli.roim.view.Overlay;

/**
 *
 * @author likhachev
 */
public final class ROIChangeEvent extends java.util.EventObject { 
    //TODO: rename logicaly    
    public final static int OVERLAYCREATED = 0;  
    public final static int ROICREATED = 1;  
    public final static int ROIDELETED = 2; 
    public final static int ROICHANGED = 3; 
    public final static int ROIMOVED = 4;        //ROI has been moved
    public final static int ROICHANGEDCOLOR = 5; //ROI's colour has been changed
    public final static int ROICHANGEDNAME = 6;  //ROI's name has been changed
    public final static int ROIALLDELETED = 7;   //all ROI have been removed    
    
    private final int iChange;   //what happened
    private final Overlay iObj;   //object    
    private final Object  iExtra; //depending on change it carries old name, old colour or ... 
    
    public ROIChangeEvent(Object aO, int aC, Overlay aR, Object aExtra) {
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
