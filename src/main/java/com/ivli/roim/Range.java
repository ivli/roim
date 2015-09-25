
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class Range {
    double iMax;
    double iMin;
    
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
    
    public double getRange() {
        return iMax - iMin;
    }
    
    public double getWidth() {
        return getRange();
    }
    
}
