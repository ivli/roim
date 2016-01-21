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
package com.ivli.roim.core;

import java.util.HashMap;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
public class Curve extends Histogram {
        
    public XYSeries getSeriesRebinned(final String aName, final int aNoOfBins) {               
        final int binSize = Math.max(1, (iMax - 0) / aNoOfBins);
     
        HashMap<Integer, Integer> reb = new HashMap<>();
                
        for (int i=0; i < aNoOfBins; ++i) {
            final Integer key = i * binSize; 
            Integer val = get(key);
            reb.put(key, val);           
        }
                
        return convert(aName, reb); 
    }
}
