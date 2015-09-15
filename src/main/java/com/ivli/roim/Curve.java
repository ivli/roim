/*
 * 
 */
package com.ivli.roim;


/**
 *
 * @author likhachev
 */

public class Curve extends java.util.ArrayList<Measure<Double>> implements java.io.Serializable {
    
    private static final long serialVersionUID = 42L;
    
    String iName;
    
    Curve(String aName) {
        iName = aName;
    }
    
    
    
    
}
