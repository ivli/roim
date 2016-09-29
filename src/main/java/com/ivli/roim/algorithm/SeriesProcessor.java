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
package com.ivli.roim.algorithm;

import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.Series;

/**
 *
 * @author likhachev
 */
public class SeriesProcessor {    
    private ISeries iSeries;
    
    public SeriesProcessor(ISeries aS) {
        iSeries = aS;
    }
    
    public ISeries add(ISeries aS) {
        return SeriesProcessor.add(iSeries, aS);
    }
  
    public ISeries sub(ISeries aS) {
        return SeriesProcessor.sub(iSeries, aS);
    }
  
     public ISeries mul(ISeries aS) {
        return SeriesProcessor.mul(iSeries, aS);
    }
      
    public ISeries div(ISeries aS) {
        return SeriesProcessor.div(iSeries, aS);
    }
    
    static ISeries add(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size() && !aRhs.isScalar())
            return null;
        
        ISeries ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) + aRhs.get(i));
        
        return ret;    
    }
       
    static ISeries sub(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size() && !aRhs.isScalar())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) - aRhs.get(i));
        
        return ret;    
    }
       
    static ISeries mul(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size() && !aRhs.isScalar())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) * aRhs.get(i));
        
        return ret;    
    }       
    
    static ISeries div(ISeries aLhs, ISeries aRhs) {       
        if (aLhs.size() != aRhs.size() && !aRhs.isScalar())
            return null;
        
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) / aRhs.get(i));
        
        return ret;    
    }
    
    public Double avg() {
        if (iSeries.isScalar())
            return iSeries.get(0);
        Double ret = 0.;
        
        for(int i = 0; i < iSeries.size(); ++i)
            ret += iSeries.get(i);
        
        return ret / iSeries.size();
    } 
    
}
