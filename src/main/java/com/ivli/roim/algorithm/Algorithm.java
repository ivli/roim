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

import java.util.function.Function;

/**
 *
 * @author likhachev
 */
public class Algorithm {
    public enum TYPE {
        LINEAR,
        QUADRATIC,
        CUBIC,
        EXPONENTIAL
    }
    /*
    return leastsquares fit of a series x, y on the interval n1 n2 
    */
    public static final Function<Double, Double> leastsquares(double[] aX, double[] aY, final int aFrom, final int aTo, TYPE aF) {        
        final int length = aTo - aFrom;
        double sum1 = .0;
        double sum2 = .0;
           
        /**/
        switch (aF) {
            case EXPONENTIAL: {
                for(int i = aFrom; i < aTo; ++i) {              
                    sum1 += aX[i];
                    sum2 += (aY[i] = Math.log(aY[i]));                                              
                } 
                };  break;       

            case LINEAR: //TODO: fallthrough unless polinomial fit get implemented
            default: {     
                for(int i = aFrom; i < aTo; ++i) {                                                        
                    sum1 += aX[i];
                    sum2 += aY[i];
                } 
            }; break;
        }
            
        final double meanX = sum1/(double)length;
        final double meanY = sum2/(double)length;            

        sum1 = sum2 = .0;
         //slope = sum((x - mean(x)) * (y' - mean(y')) / sum((x - mean(x))^2) // -B
        for(int i = aFrom; i < aTo; ++i) { 
           final double temp = aX[i] - meanX;
           sum1 += temp * (aY[i] - meanY);
           sum2 += temp * temp;
        }

        final double slope = sum1 / sum2;     
        
        final double intercept = meanY - slope*meanX;
       
        switch (aF) {
            case EXPONENTIAL: {        
                return (Double x) -> Math.exp(slope * x + intercept); 
            }
            case LINEAR: //TODO: fallthrough unless polinomial fit get implemented
            default: {     
                return (Double x) -> slope * x + intercept;
            }
        }         
    }
    
    
    /*
     * returns neares X index   
     */
    public static int getNearestIndex(final double []aS, double aX) {        
        if (aS.length > 0) {
        for (int i = 0; i < aS.length - 1; ++i) {        
            if (aX == aS[i] || aX > aS[i] && aX < aS[i+1] || aX < aS[i] && aX > aS[i+1]) 
                return i;          
        }
        if (aX == aS[aS.length-1])
            return aS.length-1;
        }
        return -1;
    } 
}
