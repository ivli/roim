
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class Range {
    double iMax;
    double iMin;
    
    public Range(double aMax, double aMin) {
        iMax = aMax;
        iMin = aMin;
    }
    
    public Range(Window aW) {
        iMax = aW.getTop();
        iMin = aW.getBottom();
    }
    
    public Window Window() {
        return new Window(getRange()/2.0, getRange());
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
}
