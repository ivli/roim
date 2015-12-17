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


/**
 *
 * @author likhachev
 */
public enum Filter {        
        DENSITY(new IFilter() {
                    public double get(ROI aR) {return aR.getDensity();}
                },
                Measurement.DENSITY),
       
        AREAINPIXELS(new IFilter() {
                    public double get(ROI aR) {return aR.getAreaInPixels();}
                },
                Measurement.AREAINPIXELS),
        
        MINPIXEL(new IFilter() {
                    public double get(ROI aR) {return aR.getMinPixel();}
                },
                Measurement.MINPIXEL),
        
        MAXPIXEL(new IFilter() {
                    public double get(ROI aR) {return aR.getMaxPixel();}
                },
                Measurement.MAXPIXEL);
        /*
         * to be continued
         */
        
        final Measurement iM;
        final IFilter     iF;
                
        Filter(IFilter aF, Measurement aM) {
            iM = aM;
            iF = aF;
        }
                      
        public double get(ROI aRoi) {
            return iF.get(aRoi);
        }
        
        public Measurement getMeasurement() {
            return iM;
        }
        
        public static String[] getAllFilters() {            
            java.util.Set<Filter> so = java.util.EnumSet.allOf(Filter.class);

            String[] ret = new String[so.size()];//String();
            int n = 0;
            for (Filter o : Filter.values())
                ret[n++] = o.iM.iName;
            return ret;        
        }
        
        public static Filter getFilter(String aS) {
            for (Filter o : Filter.values())
                if(aS == o.iM.iName)
                    return o;
            return DENSITY;     
        }
    }
    
