
package com.ivli.roim.core;


/**
 * VOI window set by pair of values window width and level 
 * 
 * @author likhachev
 */
public class Window implements java.io.Serializable {      
    private static final long serialVersionUID = 42L;
    private static final double MINIMAL_WINDOW_WIDTH = 1.;
    public static final Window DEFAULT_WINDOW = new Window(.5, MINIMAL_WINDOW_WIDTH);
    /**
     * 
     */
    protected double iLevel;
    
    /**
     *
     */
    protected double iWidth;
   
    /**
     * 
     * @param aW
     */
    public Window(Window aW) {
        iLevel = aW.iLevel; 
        iWidth = aW.iWidth;
    }       
            
    /**
     *
     * @param aL
     * @param aW
     */
    public Window(double aL, double aW) {
        iLevel = aL; 
        iWidth = Math.max(MINIMAL_WINDOW_WIDTH, aW);   
    }
       
    public Window(Range aR) {    
        iWidth = aR.range() ;
        iLevel = aR.getMin() + iWidth / 2.0;                
    }
    
    /**
     *
     * @return
     */
    public double getLevel() {
        return iLevel;
    }
    
    /**
     *
     * @return
     */
    public double getWidth() {
        return iWidth;
    }

    /**
     *
     * @param aC
     */
    public void setLevel(double aC) {
        iLevel = aC;
    }
    
    /**
     *
     * @param aW
     */
    public void setWidth(double aW) {
        iWidth = Math.max(aW, MINIMAL_WINDOW_WIDTH);
    }   

    /**
     *
     * @return
     */
    public double getTop() {
        return iLevel + iWidth / 2.;
    }
    
    /**
     *
     * @return
     */
    public double getBottom() {
        return iLevel - iWidth / 2.;
    }

    /**
     *
     * @param aT
     */
    public void setTop(double aT) { 
        final double oldTop = getTop();
        final double oldBottom = getBottom();
        
        iWidth = aT - oldBottom;
        iLevel = oldBottom + iWidth / 2.0;        
    }
    
    /**
     *
     * @param aB
     */
    public void setBottom(double aB) { 
        final double oldTop = getTop();
        final double oldBottom = getBottom();
        
        iWidth = oldTop - aB;
        iLevel = oldTop - iWidth / 2.0;       
    }
    
    /**
     *
     * @param aW
     */
    public void setWindow(Window aW) {
        setLevel(aW.iLevel);
        setWidth(aW.iWidth);
    }
    
    /**
     *
     * @param aL
     * @param aW
     */
    public void setWindow(double aL, double aW) {
        setLevel(aL);
        setWidth(aW);
    }

    /**
     *
     * @param aV
     * @return
     */
    public boolean inside(double aV) {return aV > getBottom() && aV < getTop();}

    /**
     *
     * @param aW
     * @return
     */
    public boolean equals(Window aW) {
        return  (this == aW) || (aW.iLevel == this.iLevel && aW.iWidth == this.iWidth);
    }
    
    /**
     *
     * @param aW
     * @return
     */
    public boolean contains(Window aW) {
        return  aW.getTop() <= this.getTop() && aW.getBottom() >= this.getBottom();
    }

    public void scale(double aFactor) {
        //double bot = getBottom();
        iWidth *= aFactor;
        iLevel *= aFactor;
    }
    
    @Override
    public String toString(){return String.format("[%.1f, %.1f]", getLevel(), getWidth());} //NOI18N
}
