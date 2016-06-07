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
package com.ivli.roim.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author likhachev
 */
public class Colorer {    
    private static final Color [] COLORS = new Color[] {Color.RED, Color.PINK, Color.ORANGE, 
                                                        Color.YELLOW, Color.GREEN, Color.MAGENTA,
                                                        Color.CYAN, Color.BLUE, Color.GRAY
                                                       };
    
    private static final Map <Class, Integer> iHash = new HashMap();
    
    static synchronized Color getNextColor(Object aO) {        
        int ret = 0;
        
        if (iHash.containsKey(aO.getClass())) {
            int ndx = iHash.get(aO.getClass()) + 1;
            if (ndx >= COLORS.length)
                ret = 0;
            else
                ret = ndx;
        }
        
        iHash.put(aO.getClass(), ret);
        
        return COLORS[ret];
    }
    
    private Colorer() {}
}
