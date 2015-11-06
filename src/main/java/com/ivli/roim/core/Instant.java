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
    protected long iInstant;    
    
    public static Instant INFINITE = new Instant(-1L);
    public static Instant ZERO     = new Instant(0L);
    
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
        return ((Long)iInstant).compareTo(o.iInstant);
    }
    
       
    private static final long MINUTES_IN_HOUR   = 60;
    private static final long SECONDS_IN_MINUTE = 60;
    private static final long MILLIS_IN_SECOND  = 1000;        
    private static final long MILLIS_IN_MINUTE  = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    private static final long MILLIS_IN_HOUR    = MILLIS_IN_MINUTE * MINUTES_IN_HOUR;
    
    private static final String SUFFIX_MILLIS  = "mS";
    private static final String SUFFIX_SECONDS = "S";
    private static final String SUFFIX_MINUTES = "m";
    private static final String SUFFIX_HOURS   = "H";
    private static final String STR_TIME_ZERO  = "0 " + SUFFIX_MILLIS;
    
    private static String format(long theMillis) {                       
        final long hours   = theMillis / MILLIS_IN_HOUR;        
        final long minutes = (theMillis - hours*MILLIS_IN_HOUR) / MILLIS_IN_MINUTE;        
        final long seconds = (theMillis - hours*MILLIS_IN_HOUR - minutes*MILLIS_IN_MINUTE) / MILLIS_IN_SECOND;        
        final long millis  = theMillis - hours*MILLIS_IN_HOUR - minutes*MILLIS_IN_MINUTE - seconds*MILLIS_IN_SECOND ;
               
            if (0L != millis) {

                if (0L == hours && 0L == minutes && 0L == seconds)
                    return String.format("%d " + SUFFIX_MILLIS, millis);               
                else if (0L == hours && 0L == minutes)
                    return String.format("%2d:%d " + SUFFIX_MILLIS, seconds, millis);                
                else if (0L == hours)
                    return String.format("%2d:%2d:%d " + SUFFIX_MILLIS, minutes, seconds, millis);
                else
                    return String.format("%2d:%2d:%2d:%3d " + SUFFIX_MILLIS, hours, minutes, seconds, millis);  
                
            } else if (0L != seconds) {
             
                 if (0L == hours && 0L == minutes)
                    return String.format("%2d " + SUFFIX_SECONDS, seconds);
                 else if (0L == hours)
                    return String.format("%2d:%2d " + SUFFIX_SECONDS, minutes, seconds);
                else
                    return String.format("%2d:%2d:%2d " + SUFFIX_SECONDS, hours, minutes, seconds);
                
            } else if (0L != minutes) {  
                
                if (0L == hours)               
                    return String.format("%2d " + SUFFIX_MINUTES, minutes);                     
                else
                    return String.format("%2d:%2d " + SUFFIX_MINUTES, hours, minutes);
                
            } else if (0L != hours)            
                return String.format("%2d " + SUFFIX_HOURS, hours);
        
            else
               return String.format(STR_TIME_ZERO);
    }
    
}
