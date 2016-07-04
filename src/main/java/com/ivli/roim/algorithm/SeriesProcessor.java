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

import com.ivli.roim.core.Series;

/**
 *
 * @author likhachev
 */
public class SeriesProcessor {
    
    Series iSeries;
    
    public SeriesProcessor(Series aS) {iSeries = aS;}
    
    public Series add(Series aS) {
        return SeriesProcessor.add(iSeries, aS);
    }
    
    public Series add(double aS) {
        return SeriesProcessor.add(iSeries, aS);
    }
    
    public Series sub(Series aS) {
        return SeriesProcessor.sub(iSeries, aS);
    }
    
    public Series sub(double aS) {
        return SeriesProcessor.sub(iSeries, aS);
    }
    
     public Series mul(Series aS) {
        return SeriesProcessor.mul(iSeries, aS);
    }
    
    public Series mul(double aS) {
        return SeriesProcessor.mul(iSeries, aS);
    }
    
    public Series div(Series aS) {
        return SeriesProcessor.div(iSeries, aS);
    }
    
    public Series div(double aS) {
        return SeriesProcessor.div(iSeries, aS);
    }
    
    static Series add(Series aLhs, Series aRhs) {
        if (aLhs.getNumFrames() != aRhs.getNumFrames())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) + aRhs.get(i));
        
        return ret;    
    }
    
    static Series add(Series aLhs, double aRhs) {       
        Series ret = new Series(aLhs.getId());        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) + aRhs);        
        return ret;    
    }
    
    static Series sub(Series aLhs, Series aRhs) {
        if (aLhs.getNumFrames() != aRhs.getNumFrames())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) - aRhs.get(i));
        
        return ret;    
    }
    
    static Series sub(Series aLhs, double aRhs) {       
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) - aRhs);
        
        return ret;    
    }
    
    static Series mul(Series aLhs, Series aRhs) {
        if (aLhs.getNumFrames() != aRhs.getNumFrames())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) * aRhs.get(i));
        
        return ret;    
    }
    
    static Series mul(Series aLhs, double aRhs) {        
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) * aRhs);
        
        return ret;    
    }
    
    static Series div(Series aLhs, Series aRhs) {
        if (aLhs.getNumFrames() != aRhs.getNumFrames())
            return null;
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) / aRhs.get(i));
        
        return ret;    
    }
               
    static Series div(Series aLhs, double aRhs) {        
        Series ret = new Series(aLhs.getId());
        
        for (int i=0; i<aLhs.getNumFrames(); ++i)
            ret.add(aLhs.get(i) / aRhs);
        
        return ret;    
    }
        
}
