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
public class ImageType {          
    public static final ImageType UNKNOWN = new ImageType(new String[]{}) {@Override public String toString() {return _UNKNOWN;}};
    // see C.7.6.1.1.2 Image Type
    // a. Pixel Data Characteristics
    private static final String ORIGINAL = "ORIGINAL";
    private static final String DERIVED = "DERIVED";
    // CT and MR C.8.16.1.1
    private static final String MIXED = "MIXED"; 
    
    // b. Patient Examination Characteristics
    private static final String PRIMARY = "PRIMARY";    
    private static final String SECONDARY = "SECONDARY";
    

    // c. Modality Specific Characteristics
    public final static String _UNKNOWN = "UNKNOWN";
    public final static String _IMAGE = "IMAGE";      
    //NM C.8.4.9.1.1
    public final static String NM_STATIC = "STATIC";        
    public final static String NM_DYNAMIC = "DYNAMIC"; 
    public final static String NM_GATED = "GATED"; 
    public final static String NM_WHOLEBODY = "WHOLE BODY";
    public final static String NM_TOMO = "TOMO";
    public final static String NM_TOMO_G = "GATED TOMO";
    public final static String NM_VOLUME = "RECON TOMO";
    public final static String NM_VOLUME_G = "RECON GATED TOMO";
    //CR/MR C.8.16.1.3 
    public final static String CT_AXIAL = "AXIAL";
    public final static String CT_LOCALIZER = "LOCALIZER";
    ///TBC...
    
    // d. Implementation specific identifiers
    // NM 
    public final static String EMISSION  = "EMISSION";
    public final static String TRANSMISSION = "TRANSMISSION";
    
    private static final String SEPARATOR = "\\";
    
    // a. Pixel Data Characteristics    
    protected String iPixels;     
    // b. Patient Examination Characteristics
    protected String iPatient;     
    // c. Modality Specific Characteristics    
    protected String iName;        
    // d. Implementation specific identifiers    
    protected String iSpecifics;

    private ImageType(String[] aN) {            
        switch(aN.length) {  
            case 4:        
                iSpecifics = aN[3];                
            case 3: 
                iName = aN[2];          
            case 2:
                iPatient = aN[1];
                iPixels = aN[0];                
            case 1:
            default: break;
        }       
    }    
           
    public String getTypeName() {
        return iName;
    }
          
    public boolean isPrimary() {
        return iPixels.equalsIgnoreCase(PRIMARY);
    } 
    
    public boolean isOriginal() {
        return iPatient.equalsIgnoreCase(ORIGINAL);
    }
      
    static public ImageType create(String[] aDicomString) {        
        if (null != aDicomString)                        
            return new ImageType(aDicomString);
 
        return UNKNOWN;   
    }
    
    @Override
    public String toString() {   
        return iPixels + SEPARATOR + iPatient
             + (null != iName ? SEPARATOR + iName 
              + (null != iSpecifics ? SEPARATOR + iSpecifics : "") : "");                            
    }
}
