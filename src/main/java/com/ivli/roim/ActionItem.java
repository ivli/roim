/*
 * Copyright (C) 2015 likhachev
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
package com.ivli.roim;

import java.awt.Color;

/**
 *
 * @author likhachev
 */
public abstract class ActionItem {

    /**
     *
     */
    protected int iX;

    /**
     *
     */
    protected int iY;

    /**
     *
     * @param aX
     * @param aY
     */
    public ActionItem(int aX, int aY) {
        iX = aX; 
        iY = aY;
    }

    /**
     *
     * @param aX
     * @param aY
     * @return
     */
    public final boolean release(int aX, int aY) {
       return DoRelease(aX, aY);
    }

    /**
     *
     * @param aX
     * @param aY
     */
    public final void action(int aX, int aY) {
        DoAction(aX, aY);  
        iX = aX; 
        iY = aY;
    }

    /**
     * 
     * @param aX
     */
    public final void wheel(int aX) {
        DoWheel(aX);
    }     

    /**
     *
     * @param gc
     */
    public final void paint(java.awt.Graphics2D gc) {
        Color oc = gc.getColor();
        gc.setColor(Settings.get(Settings.KEY_ACTIVE_ROI_COLOR, Color.RED));
        DoPaint(gc);
        gc.setColor(oc);
    }

    /**
     * the main method must be implemented in descendants to handle action 
     * actual coordinates are provided
     * @param aX
     * @param aY
     */
    protected abstract void DoAction(int aX, int aY); 
    
    /**
     * 
     * @param aX
     * @return
     */
    protected boolean DoWheel(int aX) {
        return false;
    }
    
    /**
     *
     * @param aX
     * @param aY
     * @return it shall return true if action is not complete and ought to be continued
     */
    protected boolean DoRelease(int aX, int aY)  {
        return false;
    }
    
    /**
     *
     * @param aGC
     */
    protected void DoPaint(java.awt.Graphics2D aGC) {}   
}

