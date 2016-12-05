/*
 * Copyright (C) 2016 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

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
       
    public void set(double aMin, double aMax) {    
        iWidth = aMax - aMin ;
        iLevel = (aMin + (aMax - aMin)) / 2.;                
    }
    
    public static Window fromRange(double aMin, double aMax) {    
        return new Window(aMin +(aMax - aMin)/2, aMax - aMin);                        
    }
    
    public boolean within(double aMin, double aMax) {    
        return  getTop() < aMax && getBottom() > aMin;                        
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
    
    public double percentsOf(double aValue) {      
        return aValue / iWidth * 100.;        
    }
    
    @Override
    public String toString(){return String.format("[%.1f, %.1f]", getLevel(), getWidth());} //NOI18N
}
