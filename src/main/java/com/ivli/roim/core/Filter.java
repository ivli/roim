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

import com.ivli.roim.view.ROI;


/**
 *
 * @author likhachev
 */
public enum Filter {        
        DENSITY((ROI aR) -> aR.getDensity(),
                Measurement.DENSITY),
       
        AREAINPIXELS((ROI aR) -> aR.getAreaInPixels(),
                     Measurement.AREAINPIXELS),
        
        MINPIXEL((ROI aR) -> aR.getMinPixel(),
                 Measurement.MINPIXEL),
        
        MAXPIXEL((ROI aR) -> aR.getMaxPixel(),
                 Measurement.MAXPIXEL),
        
        AREAINLOCALUNITS((ROI aR) -> (aR.getMaxPixel() * aR.getManager().getImage().getPixelSpacing().getX()),
                         Measurement.AREAINLOCALUNITS);
        
        
        /*
         * to be continued
         */
        
        protected final Measurement iM;
        protected final IFilter     iF;
                
        private Filter(IFilter aF, Measurement aM) {             
            iM = aM;
            iF = aF;
        }
                             
        public double filter(ROI aRoi) {
            return iF.filter(aRoi);
        }
        
        public Measurement getMeasurement() {
            return iM;
        }
        
        public static String[] getAllFilters() {            
            java.util.Set<Filter> so = java.util.EnumSet.allOf(Filter.class);

            String[] ret = new String[so.size()];
            int n = 0;
            for (Filter o : Filter.values())
                ret[n++] = o.iM.iName;
            return ret;        
        }
        
        public static Filter getFilter(final String aS) {
            for (Filter o : Filter.values())
                if(o.iM.iName.equals(aS))
                    return o;
            return DENSITY;     
        }
    }
    
