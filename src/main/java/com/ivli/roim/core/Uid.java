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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author likhachev
 */
public class Uid implements Comparable<Uid>, java.io.Serializable {
    private static final long serialVersionUID = 42L;
    private static final long INVALID_VALUE = -1L;
    private static final long DEFAULT_INITIAL_VALUE = 0L;
    private static long iGlobalUid = DEFAULT_INITIAL_VALUE;
    
    public static final Uid INVALID = new Uid(INVALID_VALUE); 
    
    private static final Map <Class, Long> iHash = new HashMap();
    
    private final long iUid;
    
    private Uid(long anId) {
        iUid = anId;
    }
    
    public long getLong() {return iUid;}
    
    public static synchronized Uid getNext() {            
        return new Uid(++iGlobalUid);
    }

    public static synchronized Uid getNext(Class aC) {     
        Long ret = INVALID_VALUE;
        if (iHash.containsKey(aC))
            ret = iHash.get(aC);            
                
        iHash.put(aC, ++ret);
        
        return new Uid(ret);
    }
    
    @Override
    public int compareTo(Uid o) {
        return (int)(this.iUid - o.iUid);
    }
    
    @Override
    public String toString() {
        return String.format("%d", iUid);
    }
}
