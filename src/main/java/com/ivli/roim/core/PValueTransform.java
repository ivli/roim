
package com.ivli.roim.core;


/**
 *
 * @author likhachev
 */
public class PValueTransform implements java.io.Serializable {
    private static final long serialVersionUID = 42L;

    public static final PValueTransform DEFAULT = new  PValueTransform(1.0, .0);
    
    private final double iSlope;
    private final double iIntercept;
  
    public PValueTransform(double aS, double aI) {
        iSlope = aS; 
        iIntercept = aI;
    }
       
    public final double transform(double aV) {
        return iSlope * aV + iIntercept;
    }

    public final double[] transform(double[] aSrc, double[] aDst) {        
        if (aDst == null)
            aDst = new double[aSrc.length];
        
        for (int i=0; i<aDst.length; ++i)
            aDst[i] = iSlope * aSrc[i] + iIntercept;
        
        return aDst;
    }
    
    @Override
    public String toString() {
        return String.format("%f*x%+f", iSlope, iIntercept);
    }    
}
