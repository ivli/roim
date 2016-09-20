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
public enum Units {
    DIMENTIONLESS("","", "%02d"),
    ///TIME
    HOUR("Hour", "h", "%02d"),
    MINUTE("Minute", "m", "%02d"),
    SECOND("Second", "s", "%02d"),
    //DISTANCE
    MILLIMETERS("Millimeters", "mm", "%3d"),
    CENTIMETERS("Centimeters", "cm", "%2d"),
    METERS("Meters", "m", "%d"),
    //AREA
    M2("Square meters","m2", "%d"),
    //WEIGHT
    KILOGRAMM("Kilogramm", "kG", "%d"),
    //VOLUME
    LITER("Liter", "l", "%d"),
    //DISPLAY
    PIXELS("Pixels", "pix", "%d"),
    //NM SPECIFIC
    COUNTS("Counts", "cnts", "%d"),
    COUNTRATE("Count rate", "cpm", "%d"),
    //ToBeContinued
    ;

    final static String SPACE = " ";
    
    final String iName;
    final String iUnits;
    final String iFormat;

    private Units(String aName, String aShort, String aFormat) {
        iName = aName;
        iUnits = aShort;
        iFormat = aFormat;
    }
    
    public String getName() {
        return iName;
    }
    
    public String getUnits() {
        return iUnits;
    }
    
    public String format(int aV) {
        return String.format(iFormat, aV) + SPACE + iUnits;
    }
    
    public String format(double aV) {
        return String.format(iFormat, (int)Math.floor(aV)) + SPACE + iUnits;
    }
}
