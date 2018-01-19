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
    public static final ImageType UNKNOWN = new ImageType() {@Override public String toString() {return _UNKNOWN;}};
    public static final ImageType IMAGE = new ImageType() {@Override public String toString() {return _IMAGE;}};
    public static final String NOTSET = "";
    // see C.7.6.1.1.2 Image Type
    // a. Pixel Data Characteristics
    public static final String ORIGINAL = "ORIGINAL";
    public static final String DERIVED = "DERIVED";
    // CT and MR C.8.16.1.1
    public static final String MIXED = "MIXED"; 
    
    // b. Patient Examination Characteristics
    public static final String PRIMARY = "PRIMARY";    
    public static final String SECONDARY = "SECONDARY";
    
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
    
    //TODO: check
    public final static String PT_TOMO = "PT";
    public final static String PT_TOMO_RECON = "PT_RECON";
    
    ///TBC...
    // d. Implementation specific identifiers
    // NM 
    public final static String EMISSION  = "EMISSION";
    public final static String TRANSMISSION = "TRANSMISSION";
    
    private static final String SEPARATOR = "\\";
    
    // a. Pixel Data Characteristics    
    protected String iPixels = NOTSET;     
    // b. Patient Examination Characteristics
    protected String iPatient = NOTSET;     
    // c. Modality Specific Characteristics    
    protected String iTypeName = NOTSET;        
    // d. Implementation specific identifiers    
    protected String iImplementationSpecific = NOTSET;

    private ImageType() {
    }
    
    private ImageType(String[] aN) {            
        switch(aN.length) {  
            case 4:        
                iImplementationSpecific = aN[3];                
            case 3: 
                iTypeName = aN[2];          
            case 2:
                iPatient = aN[1];
                iPixels = aN[0];  break;           
            case 1:
            default:
                throw new IllegalArgumentException("Syspicious DICOM: fields a and b are mandatory");               
        }       
    }    
           
    public String getTypeName() {
        return iTypeName;
    }
    
    public boolean isPrimary() {
        return iPixels.equalsIgnoreCase(PRIMARY);
    } 
    
    public boolean isOriginal() {
        return iPatient.equalsIgnoreCase(ORIGINAL);
    }
    
    public boolean isTomographic() {
        return iTypeName.contains(NM_TOMO) || iTypeName.contains(NM_TOMO_G) || iTypeName.contains(NM_VOLUME)  || iTypeName.contains(NM_VOLUME_G) 
               || iTypeName.contains(PT_TOMO) || iTypeName.contains(PT_TOMO_RECON) 
               || iTypeName.contains(CT_AXIAL)  || iTypeName.contains(CT_LOCALIZER);
    }
    
    public int getDimensions() {
        switch (iTypeName) {
            case NM_STATIC: return 2;
            case NM_WHOLEBODY: return 2;
            
            case NM_DYNAMIC: return 3;
            case NM_GATED: return 3;            
            
            case NM_TOMO: return 3;
            case NM_VOLUME: return 3;                        
            
            case NM_TOMO_G: return 4;            
            case NM_VOLUME_G: return 4;
            default:
                return 2;
        } 
    }
    
    static public ImageType create(String[] aDicomString) {        
        if (null != aDicomString)                        
            return new ImageType(aDicomString);
 
        return UNKNOWN;   
    }
    
    @Override
    public String toString() {   
        return iPixels + SEPARATOR + iPatient
             + (null != iTypeName ? SEPARATOR + iTypeName 
              + (null != iImplementationSpecific ? SEPARATOR + iImplementationSpecific : "") : "");                            
    }
}
