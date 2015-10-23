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

import com.ivli.roim.ROI;
/**
 *
 * @author likhachev
 */
public final class ROIChangeEvent extends java.util.EventObject { 
    private final ROI iROI;
    private final EStateChanged iChange;
    
    public ROIChangeEvent(Object aO, EStateChanged aC, ROI aR) {
        super (aO);
        iROI = aR;
        iChange = aC;
    }
        
    public EStateChanged getChange() {
        return iChange;
    }
    
    public ROI getROI() {
        return iROI;
    }    
}
