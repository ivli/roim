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
import java.util.HashSet;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYDataItem;
/**
 *
 * @author likhachev
 */
public class Histogram {//extends HashMap<Integer, Integer>{
    
    HashMap<Integer, Integer> iMap = new HashMap<>();
   
    int iMin = Integer.MAX_VALUE;
    int iMax = Integer.MIN_VALUE;
    
    public XYSeries getSeries(final String aName){                                           
        return convert(aName, iMap);
    }
      
    public Integer get(final Integer aKey) {
        return iMap.get(aKey);
    }
    
                
    public void increment(final Integer aKey) {
        Integer val = iMap.get(aKey);
        if (null != val)
            iMap.put(aKey, ++val);
        else
            this.put(aKey, 1);
    }
    
    public void put(final Integer aKey, final Integer aVal) {
        if (aKey > iMax)
            iMax = aKey;
        else if (aKey < iMin)
            iMin = aKey;
        
        iMap.put(aKey, aVal);
    }
    /*  */
    
    protected XYSeries convert(final String aName, HashMap<Integer, Integer> aMap){            
        XYSeries ret = new XYSeries(aName);
        
        aMap.entrySet().stream().forEach((entry) -> { 
            ret.add(entry.getKey(), entry.getValue());
        });
                        
        return ret;
    }
        
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
