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

import com.ivli.roim.ROI;
import com.ivli.roim.core.Filter;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
/**
 *
 * @author likhachev
 */
public class ConcreteOperand  implements IOperand, ROIChangeListener, AutoCloseable {
    
    @FunctionalInterface
    interface IFilter {        
        public double get(ROI aR);
    }
    
    
    Filter iF; 
    ROI  iRoi;
   
    
    public ConcreteOperand(ROI aRoi, Filter aF) {
        //super(.0);//aRoi.getDensity());
        iRoi = aRoi;    
        if (null == aF)
            iF = Filter.DENSITY;
        else
            iF = aF;
        //iValue = iF.get(iRoi);
        iRoi.addROIChangeListener(this);
    }
       
    public ConcreteOperand(ROI aRoi) {
        this(aRoi, Filter.DENSITY);
    }
    
    public double value() {
        return iF.get(iRoi);
    }
    
    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {
        //calculate();
        //TODO: dispatch to upper level container
    } 
    
     @Override
    public void close() {
        iRoi.removeROIChangeListener(this);
    }
}
