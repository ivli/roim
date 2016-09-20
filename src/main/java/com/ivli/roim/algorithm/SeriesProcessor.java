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
    
    ISeries iSeries;
    
    public SeriesProcessor(ISeries aS) {iSeries = aS;}
    
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
        if (aLhs.size() != aRhs.size())
            return null;
        
        ISeries ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) + aRhs.get(i));
        
        return ret;    
    }
    
    static Series add(ISeries aLhs, double aRhs) {       
        Series ret = new Series(aLhs.getId());        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) + aRhs);        
        return ret;    
    }
    
    static Series sub(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) - aRhs.get(i));
        
        return ret;    
    }
    
    static Series sub(ISeries aLhs, double aRhs) {       
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) - aRhs);
        
        return ret;    
    }
    
    static Series mul(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) * aRhs.get(i));
        
        return ret;    
    }
    
    static Series mul(ISeries aLhs, double aRhs) {        
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) * aRhs);
        
        return ret;    
    }
    
    static Series div(ISeries aLhs, ISeries aRhs) {
        if (aLhs.size() != aRhs.size())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) / aRhs.get(i));
        
        return ret;    
    }
               
    static Series div(ISeries aLhs, double aRhs) {        
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.size(); ++i)
            ret.add(aLhs.get(i) / aRhs);
        
        return ret;    
    }
        
}
