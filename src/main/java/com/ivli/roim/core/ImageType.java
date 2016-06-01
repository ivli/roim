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
public enum ImageType {   
    IMAGE("IMAGE"),
    //NM
    STATIC("STATIC"),        
    DYNAMIC("DYNAMIC"), 
    GATED("DYNAMIC/GATED"), 
    WHOLEBODY("WHOLE BODY"),
    TOMO("TOMO"),
    TOMO_G("TOMO/GATED"),
    VOLUME("RECON TOMO"),
    VOLUME_G("RECON TOMO/GATED"),
    //CR/CT
    AXIAL("AXIAL"),
    LOCALIZER("LOCALIZER"),
    UNKNOWN("UNKNOWN");
    
    public final String iName;
    private final boolean iDerived;
    
    private ImageType(String aN) {               
        iName = aN;
        iDerived = false;
    }
    
}
