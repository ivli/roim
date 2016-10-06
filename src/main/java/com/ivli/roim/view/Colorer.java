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
    public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
    public static final Color VIOLET     = new Color(0.93333334f, 0.50980395f, 0.93333334f);
    
    private final static Color[] RAINBOW = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, VIOLET};    
    private static final Map <Class, Integer> iHash = new HashMap();
    
    
    public static synchronized Color getNextColor(Object aO) {        
        int ndx = 0;
        
        if (iHash.containsKey(aO.getClass())) 
            ndx = iHash.get(aO.getClass()) + 1;            
                
        iHash.put(aO.getClass(), ndx);
        LOG.debug("Colorer class=" + aO.getClass().getCanonicalName() + ", index=" + ndx);
        return RAINBOW[ndx%RAINBOW.length];
    }
    
    public static Color getColor(Integer anIndex) {
        return RAINBOW[anIndex%RAINBOW.length];
    }
    
    private Colorer() {}
         
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
