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

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
/**
 *
 * @author likhachev
 */
public class FrameOffsetVector implements Iterable<FrameOffset>, java.io.Serializable {  
    private static final long serialVersionUID = 042L;
            
    private final int iSize;
    private int iBaseFrame; 
     //implements sparse array 
    private final HashMap<Integer, FrameOffset> iMap; 
        
            
    public FrameOffsetVector(int aSize) { 
        iSize = aSize;
        iBaseFrame = 0;
        iMap = new HashMap();
    }
    
    public int getBaseFrame() {
        return iBaseFrame;
    }

    public void setBaseFrame(int aNewBase) {
        FrameOffset old = get(getBaseFrame());
        int offsetX = get(getBaseFrame()).getX();
        int offsetY = get(getBaseFrame()).getY();
                
        iBaseFrame = aNewBase;  
    }        
    
    public void put(int anIndex, FrameOffset anOffset) {
        if (!isValidIndex(anIndex) )
            throw new java.util.NoSuchElementException();
        iMap.put(anIndex, anOffset);
    }
    
    public FrameOffset get(int anIndex) throws NoSuchElementException {
        if (!isValidIndex(anIndex) )
            throw new java.util.NoSuchElementException();
            
        if (iMap.containsKey(anIndex))
            return iMap.get(anIndex);
        else
            return FrameOffset.ZERO;                
    }
    
    private boolean isValidIndex(int anIndex) {
        return anIndex >=0 && anIndex < iSize;
    }    
    
     @Override
    public Iterator<FrameOffset> iterator() {    
        return new Iterator<FrameOffset>() {
            private int _next = 0;
             @Override
            public boolean hasNext() {    
                return isValidIndex(_next);
            }

             @Override
            public FrameOffset next() {
                return get(_next++);
            }  
        };
    }
}
