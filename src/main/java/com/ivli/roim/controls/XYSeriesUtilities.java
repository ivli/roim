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
package com.ivli.roim.controls;

import com.ivli.roim.algorithm.Algorithm;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.Histogram;
import java.util.function.Function;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
class XYSeriesUtilities {  
    
    static double getDomainValueOfMinimum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MAX_VALUE;
        double valX = Double.NaN;
        
        for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] < valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }  
        return valX;
    }
    
    static double getDomainValueOfMaximum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MIN_VALUE;
        double valX = Double.NaN;
        
        for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] > valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    /*
     * returns maximum Y value left or right of aX   
     */
    static double getDomainValueOfMaximum(XYSeries aS, double aX, boolean aLookLeft) {
        final double [][] v = aS.toArray();
        double valY = Double.MIN_VALUE;
        double valX = Double.NaN;
        
         final int ndx = Algorithm.getNearestIndex(v[0], aX);
        
        if (ndx < 0 || ndx >  v[0].length)
            return Double.NaN;
        
        int start;
        int end;
        
        if (aLookLeft) {
            start = 0;
            end = ndx;
        } else {
            start = ndx;
            end =  v[0].length;
        }
                    
        for (int i=start; i < end; ++i) {
            if (v[1][i] > valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    static double getDomainValueOfMinimum(XYSeries aS, double aX, boolean aLookLeft) {
        final double [][] v = aS.toArray();
        double valY = Double.MAX_VALUE;
        double valX = Double.NaN;
        
        final int ndx = Algorithm.getNearestIndex(v[0], aX);
        
        if (ndx < 0 || ndx >  v[0].length)
            return Double.NaN;
        
        int start;
        int end;
        
        if (aLookLeft) {
            start = 0;
            end = ndx;
        } else {
            start = ndx;
            end =  v[0].length;
        }
                    
        for (int i=start; i < end; ++i) {
            if (v[1][i] < valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    static double getDomainValueOfMinimum(XYSeries aS, double aFrom, double aTo) {
        final double [][] v = aS.toArray();
        double valY = Double.MAX_VALUE;
        double valX = Double.NaN;
        
        final int ndx1 = Algorithm.getNearestIndex(v[0], aFrom);
        final int ndx2 = Algorithm.getNearestIndex(v[0], aTo);
        
        if (ndx1 < 0 || ndx1 >  v[0].length)
            return Double.NaN;
        
        if (ndx2 < 0|| ndx1 >  v[0].length)
            return Double.NaN;
        
        int start;
        int end;
                                   
        for (int i = ndx1; i < ndx2; ++i) {
            if (v[1][i] < valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    static double getNearestX(XYSeries aS, double aV) {
        final double [][] v = aS.toArray();
       
        int ndx = Algorithm.getNearestIndex(v[1], aV); //illegal use
        
        if (ndx >= 0 && ndx < aS.getItemCount()) 
            return v[0][ndx] ;
        else
            return Double.NaN;
    }
    
    /*
     * computes Y = F(X) where X := [X0, X1, ... Xi, Xi+1 ... Xn] 
     * for the case actual 'x' value lies between samples say Xi and Xi+1     
     * for that neighbouring samples found using bisection method
     * linear fit used to obtain 'y'    
     */
    static double getNearestY(final XYSeries aS, final double aX) {        
        int i = aS.getItemCount();
        
        do{ // bisection
            i = i/2;
        } while(i > 0 && aS.getX(i).doubleValue() > aX);
        
        for (; i < aS.getItemCount() - 1; ++i) { 
            Double i1 = aS.getX(i).doubleValue();
            Double i2 = aS.getX(i+1).doubleValue();
            if (aX >=i1 && aX < i2) { //linear fit
                final double x0 = aS.getX(i).doubleValue();
                final double y0 = aS.getY(i).doubleValue();                    
                return y0 + (aX - x0) * (aS.getY(i+1).doubleValue() - y0) / (aS.getX(i+1).doubleValue() - x0);
            }
        }
        return Double.NaN;
    }
    
    /*
     * fits given series aS on the interval [aFrom, aTo)   
     * uses least squares to find intercept and slope
     * in the case aFrom and/or aTo lie outside the series then method returns extrapolation 
     */        
    public static XYSeries fit(XYSeries aS, double aFrom, double aTo, boolean aExp, XYSeries aRet) {            
        if (null == aS || aS.isEmpty())
            throw new IllegalArgumentException("XYSeries cannot be null"); //NOI18N
        
        if (aFrom == aTo)
            throw new IllegalArgumentException("aFrom cannot be equal to aTo"); //NOI18N
        
        if (null == aRet)
            aRet = new XYSeries("INTERPOLATION" + aS.getKey().toString()); //NOI18N

        final double [][] v = aS.toArray();
                
        final double userFrom = Math.min(aFrom, aTo);
        final double userTo = Math.max(aFrom, aTo);
        final double availFrom = Math.max(userFrom, aS.getMinX());
        final double availTo = Math.min(userTo, aS.getMaxX());
        
        final int n1 = Algorithm.getNearestIndex(v[0], availFrom);
        final int n2 = Algorithm.getNearestIndex(v[0], availTo);
        
        if (n1 < 0 || n2 < 0 || n2 <= n1)
            throw new IllegalArgumentException(String.format("n1 = %d, n2 = %d", n1, n2));//NOI18N
             
        final Function<Double, Double> f = Algorithm.leastsquares(v[0], v[1], n1, n2, aExp ? Algorithm.TYPE.EXPONENTIAL : Algorithm.TYPE.LINEAR);
        
         //interpolate      
        for(int i = n1; i <= n2; ++i) 
            aRet.add(v[0][i], f.apply(v[0][i])); 

         //extrapolate right       
        if (userTo > availTo) {            
            final double step = v[0][v[0].length - 1] - v[0][v[0].length - 2];        
            double d = availTo;
            while (d < userTo) {
                aRet.add(d, f.apply(d)); 
                d += step;
            }
        }
         //extrapolate left
        if (userFrom < availFrom) {            
            final double step = v[0][1] - v[0][0];        
            double d = availTo;
            while (d > userFrom) {
                aRet.add(d, f.apply(d)); 
                d-=step;
            }
        }   
        return aRet;
    }   
   
    public static XYSeries convert(final String aName, java.util.HashMap<Integer, Integer> aMap){            
        XYSeries ret = new XYSeries(aName, true, false);
        
        aMap.entrySet().stream().forEach((entry) -> { 
            ret.add((double)entry.getKey(), (double)entry.getValue());
        });
                        
        return ret;
    }
     
    public static XYSeries convert(final String aName, java.util.ArrayList<Integer> aList){            
        XYSeries ret = new XYSeries(aName, true, false);
        
        for (int i = 0; i < aList.size(); ++i) { 
            ret.add((double)i, (double)aList.get(i));
        }                        
        return ret;
    }
    
    public static XYSeries convert(final String aName, Histogram aHist){            
        XYSeries ret = new XYSeries(aName, true, false);
        if (null != aHist)
            for (int i = 0; i < aHist.size(); ++i) 
                ret.add((double)i * aHist.getBinSize(), (double)aHist.get(i));
                                 
        return ret;
    }
    
    
    public static XYSeries getSeriesRebinned(final String aName, Curve aC, int aNoOfBins, double aMin, double aMax) {     
        //Range r = null != aR ? aR: aC.getRangeX();
        
        final int binSize = Math.max(1, (int)(aMax-aMin) / aNoOfBins);
     
        XYSeries ret = new XYSeries(aName, true, false);
        
        for (int i = (int)aMin; i < aMax; i += binSize) {                
            ret.add(i, aC.get(i));           
        }
        
        return ret; 
    }
        
}
