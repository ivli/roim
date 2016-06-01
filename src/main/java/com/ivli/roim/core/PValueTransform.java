
package com.ivli.roim.core;


/**
 *
 * @author likhachev
 */
public class PValueTransform implements java.io.Serializable {
    private static final long serialVersionUID = 42L;

    public static PValueTransform DEFAULT_TRANSFORM = new  PValueTransform(1.0, 0);
    private final double iSlope;
    private final double iIntercept;

    public String toString() {
        return String.format("%fx + %f", iSlope, iIntercept);
    }
    
    public PValueTransform(double aS, double aI) {
        iSlope = aS; 
        iIntercept = aI;
    }
       
    public final double transform(double aV) {
        return iSlope * aV + iIntercept;
    }

}
