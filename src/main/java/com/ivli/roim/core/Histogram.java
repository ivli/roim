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

/**
 *
 * @author likhachev
 */
public class Histogram extends HashMap<Integer, Integer> {    
    ///HashMap<Integer, Integer> iMap = new HashMap<>();
   
    public int iMin = Integer.MAX_VALUE;
    public int iMax = Integer.MIN_VALUE;    
    
    public Histogram() {}
    
    public Histogram(int[] aV, int aV0, int aS) {
        for (int i = 0; i < aV.length; ++i)
            this.put(aV0+i*aS, aV[i]);    
    }
    
    public Histogram(int[] aV) {
        this(aV, 0, 1);  
    }
                                 
    /**
     *
     * @param aKey
     * @param aVal
     */
    public Integer put(final Integer aKey, final Integer aVal) {
        if (aKey > iMax)
            iMax = aKey;
        if (aKey < iMin)
            iMin = aKey;
        
        return super.put(aKey, aVal);
    }
 
  
    /**
     *
     * @param aNoOfBins
     * @return
     */
    public Histogram rebin(int aNoOfBins) {              
        final int binSize = Math.max(1, (iMax-0) / aNoOfBins);
     
        //HashMap<Integer, Integer> reb = new HashMap<>();
        Histogram ret = new Histogram();
             
        for (int i=0; i<aNoOfBins; ++i) {
            final Integer key = i * binSize; 
            Integer val = get(key);
            ret.put(key, null != val ? val:0);           
        }
        
        return ret;        
    }
}
