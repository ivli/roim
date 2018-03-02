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

import java.time.Duration;
import com.ivli.roim.core.Instant;
/**
 *
 * @author likhachev
 */
public class TimeSlice implements java.io.Serializable, Comparable<TimeSlice> {    
    private final Instant iFrom;
    private final Instant iTo;
    
    public static final TimeSlice INFINITY = new TimeSlice(Instant.ZERO, Instant.INFINITE);
  
    public TimeSlice(long aFrom, long aTo) {
        iFrom = new Instant(aFrom);
        iTo = new Instant(aTo);
    }
   
    public TimeSlice(Instant aFrom, Instant aTo) {
        iFrom = aFrom;
        iTo = aTo;
    }
    
    public Instant getFrom() {
        return iFrom;
    }
    
    public Instant getTo() {
        return iTo;
    }
    
    public long length() {
        return iTo.toLong() - iFrom.toLong();
    }
    
    public Duration duration() {    
        return Duration.ofMillis(length());
    }
       
    @Override
    public int compareTo(TimeSlice o) {
        if (o == this || (iFrom == o.iFrom && iTo == o.iTo))
            return 0;       
        else 
            return duration().compareTo(o.duration());
    }
   
}
