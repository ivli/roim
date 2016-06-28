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
package com.ivli.roim.calc;

import com.ivli.roim.view.ROI;
import com.ivli.roim.core.Filter;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.OverlayChangeListener;
import com.ivli.roim.view.OverlayManager;
/**
 *
 * @author likhachev
 */
public class ConcreteOperand implements IOperand, OverlayChangeListener, AutoCloseable {   
    private Filter iF; 
    private ROI  iRoi;
    private OverlayManager iM;
    
    public ConcreteOperand(ROI aRoi, Filter aF, OverlayManager aM) {        
        iRoi = aRoi;    
        iM = aM;
        iF = (null != aF) ?  aF : Filter.DENSITY;   
        iRoi.addChangeListener(this);
    }
       
    public ConcreteOperand(ROI aRoi, OverlayManager aM) {
        this(aRoi, Filter.DENSITY, aM);
    }
    
    public ROI getROI() {return iRoi;}
    public Filter getFilter() {return iF;}    
    
    public double value() {
        return iF.filter(iRoi, iM);
    }
    
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {   
        //TODO: dispatch to upper level container
        System.out.print("called");
    } 
    
     @Override
    public void close() {
        iRoi.removeChangeListener(this);
    }
        
    public String getString() {
        return iF.getMeasurement().getString(value());
    }
    
    public String format() {
        return iF.getMeasurement().format(value());
    }
}
