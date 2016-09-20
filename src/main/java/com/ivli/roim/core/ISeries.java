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
    
    public abstract boolean isScalar();    
    public abstract int size();    
    public abstract double get(int anIndex);    
    public abstract void add(double aV);   
    
    public SeriesProcessor processor() {
        return new SeriesProcessor(this);
    }
}
