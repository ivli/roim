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
import com.ivli.roim.algorithm.SeriesProcessor;

/**
 *
 * @author likhachev
 */
public class Series extends ISeries {         
    private final ArrayList<Double> iData = new ArrayList<>();
    
    public Series(Measurement anID) {        
        super(anID);        
    }
    
    @Override
    public int size() {
        return iData.size();
    }
    
    @Override
    public double get(int anIndex) {
        return iData.get(anIndex);
    }
    
    @Override
    public void add(double aV) {
        iData.add(aV);
    }
    
    @Override
    public boolean isScalar() {
        return false;
    } 
}
