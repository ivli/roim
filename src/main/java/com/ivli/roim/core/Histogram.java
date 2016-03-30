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
public class Histogram {    
    protected final HashMap<Integer, Integer> iMap = new HashMap<>();
    
    /**
     *
     * @param aName
     * @return
     */
    public XYSeries getSeries(final String aName){                                           
        return convert(aName, iMap);
    }
      
    /**
     *
     * @param aKey
     * @return
     */
    public Integer get(final Integer aKey) {
        return iMap.get(aKey);
    }
         
    /**
     *
     * @param aKey
     */
    public void increment(final Integer aKey) {
        Integer val = iMap.get(aKey);
        if (null != val)
            iMap.put(aKey, ++val);
        else
            this.put(aKey, 1);
    }
    
    /**
     *
     * @param aKey
     * @param aVal
     */
    public void put(final Integer aKey, final Integer aVal) {       
        iMap.put(aKey, aVal);
    }
 
    /**
     *
     * @param aName
     * @param aMap
     * @return
     */
    protected XYSeries convert(final String aName, HashMap<Integer, Integer> aMap){            
        XYSeries ret = new XYSeries(aName, true, false);
        
        aMap.entrySet().stream().forEach((entry) -> {     
            ret.add((Number)entry.getKey(), (Number)entry.getValue());
        });
                        
        return ret;
    }
        
    /**
     *
     * @param aName
     * @param aNoOfBins
     * @return
     */
    public XYSeries getSeriesRebinned(String aName, int aNoOfBins) {        
        Integer min = Integer.MIN_VALUE;
        Integer max = Integer.MAX_VALUE;
               
        for (Integer r : iMap.keySet()) {
            if (r < min)
                min = r;
            else if (r>max)
                max = r;        
        }
        
        final int binSize = Math.max(1, (Math.abs(max) - Math.abs(min)) / aNoOfBins);
     
        XYSeries ret = new XYSeries(aName, true, false);
        
        for (int i=0; i < aNoOfBins; ++i) {
            final Integer key = i * binSize; 
            Integer val = get(key);
            ret.add((Number)key, null != val ? val : 0);           
        }
        
        return ret;
    }
}
