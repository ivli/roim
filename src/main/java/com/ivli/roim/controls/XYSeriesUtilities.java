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
     * returns index of neares element of scale X   
     */
    static int getDomainIndex(final double []aS, double aX) {        
        for (int i = 0; i < aS.length - 1; ++i) {        
            if (aX == aS[i] || aX > aS[i] && aX < aS[i+1]) //this is time axis so only values greater than [i] and less than [i+1] needs to get tested    
                return i;          
        }
        if (aX == aS[aS.length-1])
            return aS.length-1;
        return -1;
    } 
    /*
     * returns maximum Y value left or right of aX   
     */
    static double getDomainValueOfMaximum(XYSeries aS, double aX, boolean aLookLeft) {
        final double [][] v = aS.toArray();
        double valY = Double.MIN_VALUE;
        double valX = Double.NaN;
        
         final int ndx = getDomainIndex(v[0], aX);
        
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
        
        final int ndx = getDomainIndex(v[0], aX);
        
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
        
        final int ndx1 = getDomainIndex(v[0], aFrom);
        final int ndx2 = getDomainIndex(v[0], aTo);
        
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
     * performs exponential fit of a given series aS through interval [aFrom, aTo)   
     * uses least squares formula to find intercept and slope
     */        
    public static XYSeries exponentialFit(XYSeries aS, double aFrom, double aTo, XYSeries aRet) {            
        if (null == aS)
            throw new IllegalArgumentException();
        if (null == aRet)
            aRet = new XYSeries("INTRPOLATION" + aS.getKey().toString());

        final double [][] v = aS.toArray();
        //y' = log(y) = A - B * x;
        //slope = sum((x - mean(x)) * (y' - mean(y')) / sum((x - mean(x))^2) // -B
        //intercept = mean(y' - x * slope) // A
        final int n1 = getDomainIndex(v[0], Math.min(aFrom, aTo));
        final int n2 = getDomainIndex(v[0], Math.max(aFrom, aTo));
        
        final int length = n2-n1;

        double[] x = new double [n2-n1];
        double[] y = new double [n2-n1];

        double sum1 = .0;
        double sum2 = .0;

        for(int i=0; i<x.length; ++i) {  
            x[i] = v[0][i+n1];
            y[i] = Math.log(v[1][i+n1]);
            sum1 += x[i];
            sum2 += y[i];
        } 

        final double meanX = sum1/(double)length;
        final double meanY = sum2/(double)length;            

        sum1 = sum2 = .0;

        for(int i=0; i<x.length; ++i) { 
           final double temp = x[i] - meanX;
           sum1 += temp * (y[i] - meanY);
           sum2 += temp * temp;
        }

        final double slope = sum1 / sum2;
        /*
        sum1 = .0;
        for(int i=0; i<x.length; ++i) {
            sum1 += (y[i] - x[i] * slope);
        } 

        final double intercept = sum1 / (double)length;
        */
        
        final double intercept = meanY - slope*meanX;
        
        for(int i=0; i<x.length; ++i) 
            aRet.add(v[0][i+n1], Math.exp(slope*v[0][i+n1] + intercept)); 

        return aRet;
    }

    public static XYSeries leastsquaresFit(XYSeries aS, double aFrom, double aTo, XYSeries aRet) {
        if (null == aS)
            throw new IllegalArgumentException();
        if (null == aRet)
            aRet = new XYSeries("INTRPOLATION" + aS.getKey().toString());

        final double [][] v = aS.toArray();            
        /*
            slope = sum((x - mean(x)) * (y' - mean(y')) / sum((x - mean(x))^2)
            intercept = mean(y) - slope * mean(x)            
        */

        final int n1 = XYSeriesUtilities.getDomainIndex(v[0], Math.min(aFrom, aTo));
        final int n2 = XYSeriesUtilities.getDomainIndex(v[0], Math.max(aFrom, aTo));
        final int length = n2-n1;

        double sum1 = .0;
        double sum2 = .0;

        for(int i=n1; i<length+n1; ++i) {                              
            sum1 += v[0][i];
            sum2 += v[1][i];
        } 

        final double meanX = sum1 / (double)length;            
        final double meanY = sum2 / (double)length;

        for(int i=n1; i<length+n1; ++i) {  
            final double temp = (v[0][i] - meanX);
            sum1 += temp * (v[1][i] - meanY) ;
            sum2 += temp * temp;
        }

        final double slope = sum1 / sum2;

        final double intercept = meanY - slope*meanX;


        for(int i = n1; i < length+n1; ++i) {
            aRet.add(v[0][i], intercept + slope*v[0][i]);                        
        }

        return aRet;
    }    
}
