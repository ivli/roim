
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public class Range implements java.io.Serializable {
    private final double iMax;
    private final double iMin;
    
    public Range(double aMin, double aMax) {
        iMax = aMax;
        iMin = aMin;
    }
    
    public Range(Window aW) {
        iMax = aW.getTop();
        iMin = aW.getBottom();
    }
    
    public Range(Range aR) {
        iMax = aR.iMax;
        iMin = aR.iMin;
    }
           
    public boolean contains(Range aR) {
        return iMax >=aR.iMax && iMin <= aR.iMin;
    }
    
    public boolean contains(Window aW) {
        return this.contains(new Range(aW));
    }
        
    public double getMax() {
        return iMax;
    }
    
    public double getMin() {
        return iMin;
    }
    
    public double range() {
        return iMax - iMin;
    }
            
    public double percentsOf(double aVal) {
        return aVal / range();
    }
    
    public boolean equals(Range aO) {
        return super.equals(aO) || (aO.iMin == iMin && aO.iMax == iMax);
    }
}
