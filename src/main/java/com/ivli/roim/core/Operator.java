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
package com.ivli.roim.core;

import com.ivli.roim.ROI;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
/**
 *
 * @author likhachev
 */
public class Operator implements ROIChangeListener {
    ROI iRoi;
    
    public Operator(ROI aRoi){
        iRoi = aRoi;        
    }
    
    public Operator(Operator anOp){
        iRoi = anOp.iRoi;
    }
    
    protected void calculate() {
        
    }
    
    public void ROIChanged(ROIChangeEvent anEvt) {
        calculate();
    } 
}
