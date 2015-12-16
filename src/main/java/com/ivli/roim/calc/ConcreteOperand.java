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
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
/**
 *
 * @author likhachev
 */
public class ConcreteOperand extends Operand implements  ROIChangeListener, AutoCloseable {
    
    @FunctionalInterface
    interface IFilter {        
        public double get(ROI aR);
    }
    
    public enum Filter {        
        DENSITY(new IFilter() {
                    public double get(ROI aR) {return aR.getDensity();}
                },
                "ROIFILTER.DENSITY"),
       
        AREAINPIXELS(new IFilter() {
                    public double get(ROI aR) {return aR.getAreaInPixels();}
                },
                "ROIFILTER.AREA_IN_PIXELS"),
        
        MINPIXEL(new IFilter() {
                    public double get(ROI aR) {return aR.getMinPixel();}
                },
                "ROIFILTER.MINPIXEL"),
        
        MAXPIXEL(new IFilter() {
                    public double get(ROI aR) {return aR.getMaxPixel();}
                },
                "ROIFILTER.MAXPIXEL");
        /*
         * to be continued
         */
        final String  iN;       
        final IFilter iF;
        //final Measurement iM; //TODO: make direct connection to extract name and units from  
        
        Filter(IFilter aF, String aN) {
            iN = aN;
            iF = aF;
        }
        
        public double get(ROI aRoi) {
            return iF.get(aRoi);
        }
        
        public static String[] getAllFilters() {            
            java.util.Set<Filter> so = java.util.EnumSet.allOf(Filter.class);

            String[] ret = new String[so.size()];//String();
            int n = 0;
            for (Filter o : Filter.values())
                ret[n++] = o.iN;
            return ret;        
        }
        
        public static Filter getFilter(String aS) {
            for (Filter o : Filter.values())
                if(aS == o.iN)
                    return o;
            return DENSITY;     
        }
    }
    
    Filter iF; 
    ROI  iRoi;
   
    
    public ConcreteOperand(ROI aRoi, Filter aF) {
        super(.0);//aRoi.getDensity());
        iRoi = aRoi;    
        if (null == aF)
            iF = Filter.DENSITY;
        else
            iF = aF;
        iValue = iF.get(iRoi);
        iRoi.addROIChangeListener(this);
    }
       
    public ConcreteOperand(ROI aRoi) {
        this(aRoi, Filter.DENSITY);
    }
    
    protected void calculate() {
        iValue = iF.get(iRoi);
    }
    
    public void ROIChanged(ROIChangeEvent anEvt) {
        calculate();
    } 
    
     @Override
    public void close() {
        iRoi.removeROIChangeListener(this);
    }
}
