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
package com.ivli.roim.view;

//import com.ivli.roim.core.Curve;
import com.ivli.roim.core.Window;
import com.ivli.roim.events.WindowChangeListener;

/**
 *
 * @author likhachev
 */
public interface IWindowTarget {    
    public void setWindow(Window aW);
    public Window getWindow();
    public double getMin();
    public double getMax();
        
    public void setInverted(boolean aI);
    public boolean isInverted(); 
    public void setLinear(boolean aI); 
    public boolean isLinear();     
    
    public void setLUT(String aLutName);
    //public Curve getWindowCurve();
    public ImageTransform getTransform();
       
    public void addWindowChangeListener(WindowChangeListener anEvt); 
    public void removeListenerFromAllLists(Object aListener);
}
