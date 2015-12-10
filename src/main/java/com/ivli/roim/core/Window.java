
package com.ivli.roim.core;


/**
 *
 * @author likhachev
 * 
 */
public class Window implements java.io.Serializable {      
    private static final long serialVersionUID = 42L;
    private static final double MINIMUM_WINDOW_WIDTH = 1.;
    
    protected double iLevel;
    protected double iWidth;

    public Window() {
        iLevel = .5;
        iWidth = MINIMUM_WINDOW_WIDTH;
    }
    
    public Window(Window aW) {
        iLevel = aW.iLevel; 
        iWidth = aW.iWidth;
    }
    
    public Window(Range aR) {
        iLevel = aR.range() / 2.0; 
        iWidth = aR.range();
    }
    
    public Window(double aL, double aW) {
        iLevel = aL; 
        iWidth = Math.max(MINIMUM_WINDOW_WIDTH, aW);   
    }

    public double getLevel() {
        return iLevel;
    }
    
    public double getWidth() {
        return iWidth;
    }

    public void setLevel(double aC) {
        iLevel = aC;
    }
    
    public void setWidth(double aW) {
        iWidth = Math.max(aW, MINIMUM_WINDOW_WIDTH);
    }   

    public double getTop() {
        return iLevel + iWidth / 2.;
    }
    
    public double getBottom() {
        return iLevel - iWidth / 2.;
    }

    public void setTop(double aT) { 
        final double oldTop = getTop();
        final double oldBottom = getBottom();
        
        iWidth = aT - oldBottom;
        iLevel = oldBottom + iWidth / 2.0;        
    }
    
    public void setBottom(double aB) { 
        final double oldTop = getTop();
        final double oldBottom = getBottom();
        
        iWidth = oldTop - aB;
        iLevel = oldTop - iWidth / 2.0;       
    }
    
    public void setWindow(Window aW) {
        setLevel(aW.iLevel);
        setWidth(aW.iWidth);
    }
    
    public void setWindow(double aL, double aW) {
        setLevel(aL);
        setWidth(aW);
    }

    public boolean inside(double aV) {return aV > getBottom() && aV < getTop();}

    public boolean compare(Window aW) {
        return  aW.iLevel == this.iLevel && aW.iWidth == this.iWidth;
    }
    
    public boolean contains(Window aW) {
        return  aW.getTop() <= this.getTop() && aW.getBottom() >= this.getBottom();
    }

    @Override
    public String toString(){return String.format("[%.1f, %.1f]", getLevel(), getWidth());} //NOI18N
}
