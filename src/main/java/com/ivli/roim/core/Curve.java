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
public class Curve extends java.util.ArrayList<Integer> {    
    private int iMinX = Integer.MAX_VALUE;
    private int iMaxX = Integer.MIN_VALUE;
    private int iMinY = Integer.MAX_VALUE;
    private int iMaxY = Integer.MIN_VALUE;
           
    public void put(final Integer aNdx, final Integer aVal) {
        if (aNdx < iMinX)
            iMinX = aNdx;
        if (aNdx > iMaxX)
            iMaxX = aNdx;
        
        if (aVal < iMinY)
            iMinY = aNdx;
        if (aVal > iMaxY)
            iMaxY = aVal;
        
        super.add(aNdx, aVal);
    }
    
    public Range getRangeX() {return new Range(iMinX, iMaxX);}
    public Range getRangeY() {return new Range(iMinY, iMaxY);}
   

}
