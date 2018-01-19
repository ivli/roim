/*
 * Copyright (C) 2015 likhachev
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
public class FrameOffset {
    protected int iX;
    protected int iY;
    
    public static final FrameOffset ZERO = new FrameOffset(0, 0);
    
    public FrameOffset() {
        iX = 0;
        iY = 0;
    }
    
    public FrameOffset(int aX, int aY) {
        iX = aX;
        iY = aY;
    }
    
    public void setX(int aX) {iX = aX;}
    public void setY(int aY) {iY = aY;}
              
    public int getX() {return iX;}
    public int getY() {return iY;}                     
}
