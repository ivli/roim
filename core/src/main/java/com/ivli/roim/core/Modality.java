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
public enum Modality {
    /* 
     * according to C.7.3.1.1.1 Modality 
     * 0008,0060
     */
    NM("NM"), // Nuclear Medicine incl. SPECT 
    PT("PT"), // Positron Emission Tomography 
    CR("CR"), // Computed Radiography 
    CT("CT"), // Computed Tomography (X-Ray) 
    MR("MR"), // Magnetic Resonance Tomography 
    US("US"), // Ultrasound 
    DX("DX"), // Digital Radiography
    OT("OT"); // Other
    
    public static final Modality UNKNOWN = OT;
            
    public static Modality create(final String aS) {    
        for(Modality t : Modality.values())
            if (aS.contains(t.getName()))
                return t;
        return UNKNOWN;
    }
    
    public boolean isTomographic() {
        return CR.equals(this) || PT.equals(this) || MR.equals(this);
    }
    
    private Modality(final String aName) {
        iName = aName;
    }
    
    public String getName() {
        return iName;
    }
    
    private final String iName;
}
