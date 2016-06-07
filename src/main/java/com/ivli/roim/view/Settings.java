
package com.ivli.roim.view;

import java.awt.Color;

/**
 *
 * @author likhachev
 */
public class Settings {//java.io.Serializable { 
    public static final String KEY_ACTIVE_ROI_COLOR = "ACTIVE_ROI_COLOR";///NOI18N  
    public static final String KEY_ZOOM_STEP_FACTOR = "ZOOM_STEP_FACTOR";//NOI18N   
    public static final String KEY_DEFAULT_PRESENTATION_LUT = "DEFAULT_PRESENTATION_LUT";//NOI18N
    public static final String KEY_INTERPOLATION_METHOD = "INTERPOLATION_METHOD";//NOI18N
    
    public static final String KEY_FILE_SUFFIX_LUT     = "FILE_SUFFIX_LUT";  //NOI18N    //  
    public static final String DEFAULT_FILE_SUFFIX_LUT = "lut";
    public static final String KEY_LASTFILE_LUT        = ""; //NOI18N
    public static final String DEFAULT_FILE_SUFFIX_DICOM = "dcm";
    
    public static final String KEY_FILE_SUFFIX_ROILIST = "FILE_SUFFIX_ROILIST"; //NOI18N    = "*"; 
    
    public static final String KEY_DEFAULT_FOLDER_LUT     = "DEFAULT_FOLDER_LUT"; // NOI18N
    public static final String KEY_DEFAULT_FOLDER_DICOM   = "DEFAULT_FOLDER_DICOM";
    public static final String KEY_DEFAULT_FOLDER_ROILIST = "DEFAULT_FOLDER_ROILIST";//= ".\\"; //NOI18N        
    
    public static final String KEY_MOUSE_DEFAULT_ACTION_LEFT = "MOUSE_DEFAULT_ACTION_LEFT";//= Controller.MOUSE_ACTION_ZOOM;
    public static final String KEY_MOUSE_DEFAULT_ACTION_MIDDLE = "MOUSE_DEFAULT_ACTION_MIDDLE";//= Controller.MOUSE_ACTION_PAN;
    public static final String KEY_MOUSE_DEFAULT_ACTION_RIGHT = "MOUSE_DEFAULT_ACTION_RIGHT";//= Controller.MOUSE_ACTION_WINDOW;
    public static final String KEY_MOUSE_DEFAULT_ACTION_WHEEL = "MOUSE_DEFAULT_ACTION_WHEEL";//= Controller.MOUSE_ACTION_LIST;    
        
    public static final String KEY_DEFAULT_IMAGE_SCALE = "DEFAULT_IMAGE_SCALE";//Fit.ONE_TO_ONE;
    public static final String KEY_PRESERVE_WINDOW = "PRESERVE_WINDOW";//true;
       
    
    static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(Settings.class);
    
    public static int get(String aKey, int aDefaultValue) {    
        return prefs.getInt(aKey, aDefaultValue);
    }
    
    public static String get(String aKey, String aDefaultValue) {    
        return prefs.get(aKey, aDefaultValue);
    }
    
    public static boolean get(String aKey, boolean aDefaultValue) {    
        return prefs.getBoolean(aKey, aDefaultValue);
    }
    
    public static double get(String aKey, double aDefaultValue) {    
        return prefs.getDouble(aKey, aDefaultValue);
    }
    
    public static Color get(String aKey, Color aDefaultValue) {    
        int val = prefs.getInt(aKey, aDefaultValue.getRGB());
        return new Color(val);
    }
       
    public static void set(String aKey, int aDefaultValue) {    
        prefs.putInt(aKey, aDefaultValue);
    }
    
    public static void set(String aKey, String aDefaultValue) {    
        prefs.put(aKey, aDefaultValue);
    }
    
    public static void set(String aKey, boolean aDefaultValue) {    
         prefs.putBoolean(aKey, aDefaultValue);
    }
    
    public static void set(String aKey, double aDefaultValue) {    
        prefs.putDouble(aKey, aDefaultValue);
    }
    
    public static void set(String aKey, Color aDefaultValue) {    
        prefs.putInt(aKey, aDefaultValue.getRGB());
    }
     /*
    
    public static final Double ZOOM_STEP_FACTOR  = 10.;    
    public static final Integer DEFAULT_PRESENTATION_LUT = 1;//LutLoader.BUILTIN_LUTS[1]; //GRAYS
    public static final Object INTERPOLATION_METHOD     = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;//VALUE_INTERPOLATION_BILINEAR;//
    public static final String DEFAULT_FOLDER_LUT       = "D:\\temp\\Lookup_Tables\\"; //NOI18N
    public static final String FILE_SUFFIX_LUT          = "*.lut"; //NOI18N    
    public static final String DEFAULT_FOLDER_ROILIST   = ".\\"; //NOI18N
    public static final String FILE_SUFFIX_ROILIST      = "*"; //NOI18N
    
    public static int MOUSE_DEFAULT_ACTION_LEFT   = Controller.MOUSE_ACTION_ZOOM;
    public static int MOUSE_DEFAULT_ACTION_MIDDLE = Controller.MOUSE_ACTION_PAN;
    public static int MOUSE_DEFAULT_ACTION_RIGHT  = Controller.MOUSE_ACTION_WINDOW;
    public static int MOUSE_DEFAULT_ACTION_WHEEL  = Controller.MOUSE_ACTION_LIST;    
        
    public static Fit DEFAULT_FIT = Fit.ONE_TO_ONE;
    public static boolean KEEP_WINDOW_AMONG_FRAMES = true;
    
    private static final Settings iInstance = new Settings(); //do i have to place it in static initialization block 
    
    private static final EventListenerList iList = new EventListenerList();
   
    public void addROIChangeListener(SettingsChangeListener aL) {       
        iList.add(SettingsChangeListener.class, aL);
    }  
    
    public void removeROIChangeListener(SettingsChangeListener aL) {       
        iList.remove(SettingsChangeListener.class, aL);
    }  
*/
}

