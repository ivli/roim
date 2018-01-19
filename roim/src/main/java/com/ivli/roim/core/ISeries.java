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

import com.ivli.roim.algorithm.SeriesProcessor;

/**
 *
 * @author likhachev
 */
public abstract class ISeries {
    protected final Measurement iID; 
    
    protected ISeries(Measurement anID) {
        iID = anID;
    }
        
    public Measurement getId() {
        return iID;
    }
    
    /**
     *
     * @return whether this object holds a scalar value or a series    
     */
    public abstract boolean isScalar();
    
    /**
     *
     * @return if this object holds a scalar value it always returns 1 otherwise a size of the series    
     */
    public abstract int size();  
    
    /**
     *
     * @return  in the case of a series it returns series value at a given index
     *          for scalars it always returns it's value parameter anIndex is ignored
     */
    public abstract double get(int anIndex); 
    
    /**   
     * 
     * @param aV appends given value to the end of the series
     *           for scalars it always changes it's value to provided one 
     */
    public abstract void add(double aV);    
    
    /**
     *
     * @return object of the SeriesProcessor class that provides base means of math manipulations  
     */
    public SeriesProcessor processor() {
        return new SeriesProcessor(this);
    }
}
