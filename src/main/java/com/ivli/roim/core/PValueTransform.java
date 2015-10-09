
package com.ivli.roim.core;


/**
 *
 * @author likhachev
 */
public class PValueTransform implements java.io.Serializable {
    private static final long serialVersionUID = 42L;

    private final double iSlope;
    private final double iIntercept;

    public PValueTransform(double aS, double aI) {
        iSlope = aS; 
        iIntercept = aI;
    }
    
    public PValueTransform() {
        iSlope = 1.0; 
        iIntercept = .0;
    }

    public final double transform(double aV) {
        return iSlope * aV + iIntercept;
    }

}
