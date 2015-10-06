
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class Measurement {
    public static final int MINIMUM = 2;
    public static final int MAXIMUM = 4; 
    public static final int DENSITY = 8;
    
    public static final int PRIMARY = 0;
    public static final int DERIVED = 1;
    
    
    private final int iId; 
    
    public Measurement(int anId) {
        iId = anId;
    }
    
    public int getId() {return iId;}
}
