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

import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
public class Curve /*extends Histogram*/ {
    
    private ArrayList<Integer> iList = new ArrayList<>();
    private int iMinX = Integer.MAX_VALUE;
    private int iMaxX = Integer.MIN_VALUE;
    private int iMinY = Integer.MAX_VALUE;
    private int iMaxY = Integer.MIN_VALUE;
    
    public Integer get(final Integer aNdx) {
        return iList.get(aNdx);
    }
    
    public void put(final Integer aNdx, final Integer aVal) {
        if (aNdx < iMinX)
            iMinX = aNdx;
        if (aNdx > iMaxX)
            iMaxX = aNdx;
        
        if (aVal < iMinY)
            iMinY = aNdx;
        if (aVal > iMaxY)
            iMaxY = aVal;
        iList.add(aNdx, aVal);
    }
    
    public XYSeries getSeriesRebinned(final String aName, int aNoOfBins, Range aR) {     
  
        Range r = null != aR ? aR: new Range(iMinX, iMaxX);                
        final int binSize = Math.max(1, (int)r.range() / aNoOfBins);
     
        XYSeries ret = new XYSeries(aName, true, false);
        for (int i = (int)r.getMin(); i < r.getMax(); i += binSize) {    
            Integer val = get(i);
            ret.add(i, val);           
        }
        
        return ret; 
    }

}
