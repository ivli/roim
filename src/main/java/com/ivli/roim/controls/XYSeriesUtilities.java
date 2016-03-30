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
     * computes Y = F(X) where X := [X0, X1, ... Xi, Xi+1 ... Xn] 
     * for the case actual 'x' value lies between samples say Xi and Xi+1     
     * for that neighbouring samples found using bisection method
     * linear fit used to obtain 'y'    
     */
    static double getNearestY(final double aX, final XYSeries aS) {        
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
}
