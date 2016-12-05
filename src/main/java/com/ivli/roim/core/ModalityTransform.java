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


/**
 *
 * @author likhachev
 */
public class ModalityTransform implements java.io.Serializable {
    private static final long serialVersionUID = 42L;

    public static final ModalityTransform DEFAULT = new  ModalityTransform(1.0, .0);
    
    private final double iSlope;
    private final double iIntercept;
  
    public ModalityTransform(double aS, double aI) {
        iSlope = aS; 
        iIntercept = aI;
    }
       
    public final double transform(double aV) {
        return iSlope * aV + iIntercept;
    }

    public final double[] transform(double[] aSrc, double[] aDst) {        
        if (aDst == null)
            aDst = new double[aSrc.length];
        
        for (int i=0; i<aDst.length; ++i)
            aDst[i] = iSlope * aSrc[i] + iIntercept;
        
        return aDst;
    }
    
    @Override
    public String toString() {
        return String.format("%f*x%+f", iSlope, iIntercept);
    }    
}
