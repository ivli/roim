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

public class PhaseInformation implements java.io.Serializable, Comparable<PhaseInformation> {   
    private static final long serialVersionUID = 42L;
    
    int iNumberOfFrames;
    int iFrameDuration;
   
    public PhaseInformation(int aNoOfFRames, int aFrameDurationMilliseconds) {
        iNumberOfFrames = aNoOfFRames;
        iFrameDuration  = aFrameDurationMilliseconds;          
    }
    
    public PhaseInformation(PhaseInformation aP) {
        iNumberOfFrames = aP.iNumberOfFrames;
        iFrameDuration  = aP.iFrameDuration;      
    }
    
    public long duration() {
        return iNumberOfFrames * iFrameDuration;
    }
    
    @Override
    public int compareTo(final PhaseInformation aP) {
        
        if (iNumberOfFrames == aP.iNumberOfFrames && iFrameDuration == aP.iFrameDuration)
            return 0;
        else {
             int ret = ((Long)duration()).compareTo(aP.duration());    
             if (0 == ret)
                 --ret; //the case phases have the same duration while number of frames and frame duration are different 
             return ret;
        }
    }
}  

