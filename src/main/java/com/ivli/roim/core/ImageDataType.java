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
public enum ImageDataType {
    GRAYS8 (8,  false, false),
    GRAYS16(16, false, false),
    GRAYS32(32, false, false),
    RGB    (32, true,  false),
    RGBA   (32, true,  true );
    
    private final int     iDataSize;
    private final boolean iColoured;
    private final boolean iHasAlpha;
    
    private ImageDataType(int aDataSize, boolean aColoured, boolean aHasAlpha) {
        iDataSize = aDataSize;
        iColoured = aColoured;
        iHasAlpha = aHasAlpha;        
    }
    
    public int bufferSize(int aRows, int aCols) {
        return iDataSize * aRows * aCols;
    }
}
