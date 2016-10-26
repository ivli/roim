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

///see C.7.6.1.1.2 Image Type
/// a. Pixel Attributes
/// ORIGINAL|DERIVED
/// b. Patient examination
/// PRIMARY|SECONDARY
/// c. Modality Specific Characteristics
/// for NM 
/// d. Implementation specific identifiers
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
    
    static final String ORIGINAL = "ORIGINAL";
    static final String DERIVED = "DERIVED";
    static final String PRIMARY = "PRIMARY";
    static final String SECONDARY = "SECONDARY";
    
    protected final String iName;
    
    protected boolean iOriginal = true;
    protected boolean iPrimary = true; 
    
    private ImageType(String aN) {               
        iName = aN;
    }    
 
    public String getName() {
        return iName;
    }
    
    private void setDerived() {
        iOriginal = false;
    } 
    
    private void setSecondary() {
        iPrimary = false;
    } 
    
    private boolean isPrimary() {
        return iPrimary;
    } 
    
    public boolean isOriginal() {
        return iOriginal;
    }
      
    static public ImageType create(String[] aDicomString) {        
        if (null != aDicomString) {                        
            ImageType ret; 
            
            boolean original = false;
            boolean primary = false;
            
            if (aDicomString[0].equalsIgnoreCase("ORIGINAL"))
                original = true;
            
            if (aDicomString[1].equalsIgnoreCase("PRIMARY"))
                primary = true;
            
            for(ImageType t : ImageType.values())
                if (aDicomString[2].contains(t.getName())) {
                    ret = t;
                    if (!original)
                        t.setDerived();
                    if(!primary)
                        t.setSecondary();
                    return ret;
                }            
        }
       
        return ImageType.UNKNOWN;   
    }
}
