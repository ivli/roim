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
public enum Measurement {//implements java.io.Serializable {
        
    DENSITY(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.DENSITY"), 
            java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.CTS"), 
            "%d"), // NOI18N
    AREAINPIXELS(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.AREA_IN_PIXELS"), 
                 java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.PIXELS"), 
                 "%d"), // NOI18N
    MINPIXEL(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.MINPIXEL"), 
             java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.PIXELS"), 
             "%d"), // NOI18N
    MAXPIXEL(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.MAXPIXEL"), 
             java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MEASUREMENT.PIXELS"), 
             "%d"); // NOI18N 
   
    
    final String iName;
    final String iUnits;
    final String iFormat;
    
    
    Measurement(String aName, String aUnits, String aFormat) {
        iName   = aName;
        iUnits  = aUnits;
        iFormat = aFormat;
    }
    
    public String getName() {return iName;}
    public String getUnits() {return iUnits;}
    
    
    public static String[] getAllMeasurements() {            
        java.util.Set<Measurement> so = java.util.EnumSet.allOf(Measurement.class);

        String[] ret = new String[so.size()];//String();
        int n = 0;
        for (Measurement o : Measurement.values())
            ret[n++] = o.iName;
        return ret;        
    }
    
    public String getFormatString() {
        return iName + ": " + iFormat + " " + iUnits; // NOI18N 
    }
    
    public String getString(int aInt) {        
        if (iFormat == "%f") // NOI18N 
            return String.format(iFormat, (double)aInt);
        else
            return String.format(iFormat, aInt);
    }
    
    public String getString(double aDouble) {
        if (iFormat == "%d") // NOI18N 
            return String.format(iFormat, (int)aDouble);
        else
            return String.format(iFormat, aDouble);
    }
        
    public String format(int aInt) {        
        if (iFormat == "%f") // NOI18N 
            return String.format(getFormatString(), (double)aInt);
        else
            return String.format(getFormatString(), aInt);
    }
    
    public String format(double aDouble) {
        if (iFormat == "%d") // NOI18N 
            return String.format(getFormatString(), (int)aDouble);
        else
            return String.format(getFormatString(), aDouble);
    }
    
}
