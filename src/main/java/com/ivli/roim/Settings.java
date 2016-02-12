
package com.ivli.roim;

import java.awt.Color;
import java.awt.RenderingHints;
import javax.swing.event.EventListenerList;
import com.ivli.roim.events.SettingsChangeListener;
/**
 *
 * @author likhachev
 */
public class Settings implements java.io.Serializable {
    public static final Color  ACTIVE_ROI_COLOR         = Color.RED;    
    public static final double ZOOM_STEP_FACTOR  = 10.;
    public static final EDISPLAY_UNITS DISPLAY_UNITS    = EDISPLAY_UNITS.DISPLAY_UNITS_MM;
    public static final String DEFAULT_PRESENTATION_LUT = LutLoader.BUILTIN_LUTS[1]; //GRAYS
    public static final Object INTERPOLATION_METHOD     = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;//VALUE_INTERPOLATION_BILINEAR;//
    public static final String DEFAULT_FOLDER_LUT       = "D:\\temp\\Lookup_Tables\\"; //NOI18N
    public static final String FILE_SUFFIX_LUT          = "*.lut"; //NOI18N    
    public static final String DEFAULT_FOLDER_ROILIST   = ".\\"; //NOI18N
    public static final String FILE_SUFFIX_ROILIST      = "*"; //NOI18N
    
    public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
    public static final Color VIOLET     = new Color(0.93333334f, 0.50980395f, 0.93333334f);
    
    public enum EDISPLAY_UNITS {
        DISPLAY_UNITS_MM, DISPLAY_UNITS_CM;
        public static final String getFormatString() {
            return Settings.DISPLAY_UNITS == DISPLAY_UNITS_MM ? java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString(""): java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("");} 
    };
    
    public static int MOUSE_DEFAULT_ACTION_LEFT   = Controller.MOUSE_ACTION_PAN;
    public static int MOUSE_DEFAULT_ACTION_MIDDLE = Controller.MOUSE_ACTION_ZOOM;
    public static int MOUSE_DEFAULT_ACTION_RIGHT  = Controller.MOUSE_ACTION_WINDOW;
    public static int MOUSE_DEFAULT_ACTION_WHEEL  = Controller.MOUSE_ACTION_LIST;    
        
    public static Fit DEFAULT_FIT = Fit.VISIBLE;
    public static boolean KEEP_WINDOW_AMONG_FRAMES = true;
    
    private Settings(){}
    
    private static final Settings iInstance = new Settings(); //do i have to place it in static initialization block 
    private final EventListenerList iList = new EventListenerList();
   
    
    public void addROIChangeListener(SettingsChangeListener aL) {       
        iList.add(SettingsChangeListener.class, aL);
    }  
    
    public void removeROIChangeListener(SettingsChangeListener aL) {       
        iList.remove(SettingsChangeListener.class, aL);
    }  
}

