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
 * 
 * Overlay based objects usually send to subscribers to notify on changes 
 * However, some events can be received from an OverlayManager particularly: CREATED, DELETED 
 * 
 */
public class OverlayChangeEvent extends java.util.EventObject {  
    public enum CODE {
        /* OverlayManager initiated events */
        CREATED,       // OverlayManager sends it thus getSource() => ref::<OverlayManager> while getObject() => ref::<Overlay> created
        DELETED,       // OverlayManager sends it thus getSource() => ref::<OverlayManager> while getObject() => ref::<Overlay> to be deleted
        MOVED,         // OverlayManager sends it thus getSource() => ref::<OverlayManager> while getObject() => ref::<Overlay>
                       // extra => double [] {adX, adY} - offset to move to  
        RESHAPED,      // object's shape is changed - extra => old shape of the figure
        
        /* Overlay initiated events */        
        SELECTED,      // extra => new select state
        PINNED,        // extra => new pin state
        //MOVING,        // extra => double [] {adX, adY} - offset to move to         
        //RESHAPING,     // object's shape is changed - extra => old shape of the figure

        //SIC: following events initiated by an Overlay but responsible OverlayMnager would also resent them outside 
        COLOR_CHANGED, // color has been changed: extra => Color - old color
        NAME_CHANGED,  // name has been changed: extra => String - oldName      
        PRESENTATION   // presentation state has been changed, for example: Multi/Single - line, Filters etc
    }
    
    private final CODE    iCode;  //what happened
    private final Overlay iObj;   //object    
    private final Object  iExtra; //depending on change it carries old name, old colour or ... 
    
    public OverlayChangeEvent(Object aO, CODE aC, Object aExtra) {
        super (aO);
        iObj = (Overlay)aO;
        iCode = aC;
        iExtra = aExtra;
    }
    
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
