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

/**
 *
 * @author likhachev
 */
public class Histogram {    
    private int iMin = Integer.MAX_VALUE;
    private int iMax = Integer.MIN_VALUE;    
    
    private double iBinSize = 1.0;
    private int iNoOfBins = 0;
    private ArrayList<Integer> iData = new ArrayList();
    
    public Histogram(int[] aV, double aS) {
        
        iBinSize = aS;
               
        for (int i = 0; i < aV.length; ++i)
            put(i, aV[i]);    
    }
    
    public Histogram(int[] aV) {
        this(aV, 1.);  
    }
       
    public Histogram(int aNoOfBins) {     
        iNoOfBins = aNoOfBins;
        iData.ensureCapacity(iNoOfBins);
        
        for(int i = 0; i < iData.size(); ++i)
            iData.add(i, 0);
    }
    
    public int getNoOfBins() {
        return iData.size();
    }
    
    public double getBinSize() {
        return iBinSize;
    }
    
    /**
     * assigns values to a given bin
     * @param aBin bin index to assign
     * @param aVal value to assign to a bin
     * @return assigned value
     */
    public Integer put(final Integer aBin, final Integer aVal) {
        if (aVal > iMax)
            iMax = aVal;
        if (aVal < iMin)
            iMin = aVal;
       
        iData.add(aBin, aVal);
        return  aVal;
    }
    
    public Integer get(final Integer aBin) {     
        return iData.get(aBin);    
    } 
             
    public Integer inc(final Integer aBin) {
        Integer val = iData.get(aBin) + 1;        
        return put(aBin, val);
    }
  
    /**
     *
     * @param rebin
     * @return
     */
    /**/
    public Histogram rebin(int aNoOfBins) throws IllegalArgumentException {                
        if (aNoOfBins > getNoOfBins())
            throw new IllegalArgumentException("new number of bins must be less than current");
        
        Histogram ret = new Histogram(aNoOfBins);
                
        int step = getNoOfBins() / aNoOfBins;
        
        for (int i = 0; i < aNoOfBins; ++i) {
            int val = 0;
            for (int j = 0; j<step; ++j) 
                val += get(i+j); 
            
            ret.put(i, val); 
        }
        
        return ret;        
    }
    
    
    public double min() {return iMin;}
    public double max() {return iMax;}
}
