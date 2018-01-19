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

import java.util.EnumSet;
import java.util.ResourceBundle;
import java.util.Set;

/**
 *
 * @author likhachev
 */
public enum Measurement {        
    DENSITY(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.DENSITY"), 
            Units.COUNTS), 
    AREAINPIXELS(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.AREA_IN_PIXELS"), 
                 Units.PIXELS), 
    MINPIXEL(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.MINPIXEL"), 
             Units.PIXELS), 
    MAXPIXEL(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.MAXPIXEL"), 
             Units.PIXELS),    
    AREAINLOCALUNITS(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.AREAINLOCALUNITS"), 
                     Units.M2),  
    DISTANCE(ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.DISTANCE"), 
                     Units.MILLIMETERS), 
    UNDEFINED("",Units.DIMENSIONLESS);
    
    final static String SEPARATOR = ":";
    final static String SPACE = " ";
    
    final String iName;
    final Units  iUnits;    
        
    private Measurement(String aName, Units aUnits) {
        iName   = aName;
        iUnits  = aUnits;        
    }
    
    public String getName() {return iName;}
    public Units getUnits() {return iUnits;}

    public static String[] getAllMeasurements() {            
        Set<Measurement> so = EnumSet.allOf(Measurement.class);

        String[] ret = new String[so.size()];
        int n = 0;
        for (Measurement o : Measurement.values())
            ret[n++] = o.iName;
        return ret;        
    }
                  
    public String format(int aVal, boolean bLong) {               
        return !bLong ? iUnits.format(aVal) : iName + SEPARATOR + iUnits.format(aVal);
    }
    
    public String format(double aVal, boolean bLong) {
        return !bLong ? iUnits.format(aVal) : iName + SEPARATOR + iUnits.format(aVal);
    }    
}
