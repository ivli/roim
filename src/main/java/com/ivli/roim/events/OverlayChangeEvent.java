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
    public enum CODE {
        CREATED,       
        DELETED, 
        MOVED,       // extra => double [] {adX, adY}
        COLOR,       // color has been changed
        NAME,        // name has been changed, extra => oldName      
        PRESENTATION // other specifis proterties have been changed, such as Multi/Single - line, Filters etc
    }
    
    private final CODE    iCode; //what happened
    private final Overlay iObj;   //object    
    private final Object  iExtra; //depending on change it carries old name, old colour or ... 
    
    public OverlayChangeEvent(Object aO, CODE aC, Overlay aR, Object aExtra) {
        super (aO);
        iObj = aR;
        iCode = aC;
        iExtra = aExtra;
    }
    
    public CODE getCode() {
        return iCode;
    }
       
    public Overlay getObject() {
        return iObj;
    }   
    
    public Object getExtra() {
        return iExtra;
    }   
    
    @Override
    public String toString() {
        return this.source.getClass().getName() + ":" + iObj.toString() + "," + iCode.toString() + ((null != iExtra) ? ", " + iExtra.getClass().getName() : "");
    } 
}
