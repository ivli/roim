
package com.ivli.roim;

import java.awt.Color;
import java.awt.RenderingHints;

/**
 *
 * @author likhachev
 */
public class Settings {
    public static final Color  ACTIVE_ROI_COLOR         = Color.RED;    
    public static final double ZOOM_SENSITIVITY_FACTOR  = 10.;
    public static final EDISPLAY_UNITS DISPLAY_UNITS    = EDISPLAY_UNITS.DISPLAY_UNITS_MM;
    public static final String DEFAULT_PRESENTATION_LUT = LutLoader.BUILTIN_LUTS[1]; //GRAYS
    public static final Object INTERPOLATION_METHOD     = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;//VALUE_INTERPOLATION_BILINEAR;//
    public static final String DEFAULT_FOLDER_LUT       = "D:\\temp\\Lookup_Tables\\"; //NOI18N
    public static final String FILE_SUFFIX_LUT          = "*.lut"; //NOI18N    
    public static final String DEFAULT_FOLDER_ROILIST = ".\\"; //NOI18N
    public static final String FILE_SUFFIX_ROILIST          = "*"; //NOI18N
    
    public enum EDISPLAY_UNITS {
        DISPLAY_UNITS_MM, DISPLAY_UNITS_CM;
        public static final String getFormatString() {
            return Settings.DISPLAY_UNITS == DISPLAY_UNITS_MM ? java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString(""): java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("");} 
    };
    
    private Settings(){/*this's just a sack of static configuration constans*/}
};

