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
public class Instant implements java.io.Serializable, Comparable<Instant> {      
    private static final long MINUTES_IN_HOUR   = 60L;
    private static final long SECONDS_IN_MINUTE = 60L;
    private static final long MILLIS_IN_SECOND  = 1000L;        
    private static final long MILLIS_IN_MINUTE  = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    private static final long MILLIS_IN_HOUR    = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;    
    
    public static final Instant ZERO     = new Instant(0L);
    public static final Instant SECOND   = new Instant(MILLIS_IN_SECOND);
    public static final Instant MINUTE   = new Instant(MILLIS_IN_MINUTE);    
    public static final Instant HOUR     = new Instant(MILLIS_IN_HOUR);
    public static final Instant INFINITE = new Instant(-1L);
    
    protected final long iInstant; //time in milliseconds
     
    public Instant(long aI) {
        iInstant = aI;
    }
    
    public long toLong() {
        return iInstant;
    }
        
    public String format() {
        return Instant.format(iInstant);
    }
            
    @Override
    public int compareTo(Instant o) {
        if (o == this)
            return 0;
        else
            return ((Long)iInstant).compareTo((Long)o.iInstant);
    }
    
    public boolean isInfinite() {
        return INFINITE.iInstant == this.iInstant; 
    }           
    
    private static final String SUFFIX_MILLIS  = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INSTANT.SUFFIX_MILLIS");
    private static final String SUFFIX_SECONDS = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INSTANT.SUFFIX_SECONDS");
    private static final String SUFFIX_MINUTES = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INSTANT.SUFFIX_MINUTES");
    private static final String SUFFIX_HOURS   = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INSTANT.SUFFIX_HOURS");
    private static final String FORMAT_SEPARATOR = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INSTANT.FORMAT_SEPARATOR");   
    
    private static final String FORMAT_MILLIS  = "%d"; //NOI18N
    private static final String FORMAT_SECONDS = "%2d"; //NOI18N
    private static final String FORMAT_MINUTES = "%2d"; //NOI18N
    private static final String FORMAT_HOURS   = "%2d"; //NOI18N
         
    private static final String FORMAT_MS = FORMAT_MILLIS + SUFFIX_MILLIS;
    private static final String FORMAT_SS_MS = FORMAT_SECONDS + FORMAT_SEPARATOR + FORMAT_MS;
    private static final String FORMAT_MM_SS_MS = FORMAT_MINUTES + FORMAT_SEPARATOR + FORMAT_SS_MS;
    private static final String FORMAT_HH_MM_SS_MS = FORMAT_HOURS + FORMAT_SEPARATOR + FORMAT_MM_SS_MS;    
    private static final String FORMAT_SS = FORMAT_SECONDS + SUFFIX_SECONDS;    
    private static final String FORMAT_MM_SS = FORMAT_MINUTES + FORMAT_SEPARATOR + FORMAT_SS;
    private static final String FORMAT_HH_MM_SS = FORMAT_HOURS + FORMAT_SEPARATOR + FORMAT_MM_SS;   
    private static final String FORMAT_MM = FORMAT_MINUTES + SUFFIX_MINUTES;
    private static final String FORMAT_HH_MM = FORMAT_HOURS + FORMAT_SEPARATOR + FORMAT_MM;
    private static final String FORMAT_HH = FORMAT_HOURS + SUFFIX_HOURS;
    private static final String FORMAT_STUB  = "--"; //NOI18N
    
    private static String format(long theMillis) {                       
        final long hours   = theMillis / MILLIS_IN_HOUR;        
        final long minutes = (theMillis - hours*MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;        
        final long seconds = (theMillis - hours*MILLIS_IN_HOUR - minutes*MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;        
        final long millis  = theMillis - hours*MILLIS_IN_HOUR - minutes*MILLIS_IN_MINUTE - seconds*MILLIS_IN_SECOND ;
               
        if (0L != millis) {
            if (0L == hours && 0L == minutes && 0L == seconds)
                return String.format(FORMAT_MS, millis);               
            else if (0L == hours && 0L == minutes)
                return String.format(FORMAT_SS_MS, seconds, millis);                
            else if (0L == hours)
                return String.format(FORMAT_MM_SS_MS, minutes, seconds, millis);
            else
                return String.format(FORMAT_HH_MM_SS_MS, hours, minutes, seconds, millis);                  
        } else if (0L != seconds) {             
            if (0L == hours && 0L == minutes)
                return String.format(FORMAT_SS, seconds);
            else if (0L == hours)
                return String.format(FORMAT_MM_SS, minutes, seconds);
            else
                return String.format(FORMAT_HH_MM_SS, hours, minutes, seconds);                
        } else if (0L != minutes) {                  
            if (0L == hours)               
                return String.format(FORMAT_MM, minutes);                     
            else
                return String.format(FORMAT_HH_MM, hours, minutes);                
        } else if (0L != hours)            
            return String.format(FORMAT_HH, hours);        
        else
           return String.format(FORMAT_STUB);
    }    
}
