
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public class Measurement implements java.io.Serializable {
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
