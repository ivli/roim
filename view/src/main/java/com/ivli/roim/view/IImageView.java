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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeListener;
/**
 *
 * @author likhachev
 */
public interface IImageView extends IWindowTarget {    
    public void setImage(IMultiframeImage anImage);
    public IMultiframeImage getImage();   
    
    //public ImageFrame getFrame();    
    public int     getFrameNumber();    
    public boolean setFrameNumber(int aN);    
    public void    setROIMgr(ROIManager aR);
    
    public ROIManager getROIMgr();
       
    public enum FITMODE {         
        FIT_VISIBLE, //fit to display entire image      
        FIT_WIDTH,   //fit to display full image width      
        FIT_HEIGHT,  //fit to display full image height 
        FIT_PIXELS;  //fit to display image in pixel to pixel mode no matter how big it is
    }
   
    public void fit(FITMODE aW);    
    public void pan(int aX, int aY);
    public void zoom(double aStep);    
    public double getScale();
    public void setInterpolationMethod(Object aM);    
    public void reset();
    public AffineTransform virtualToScreen();
    public AffineTransform screenToVirtual();       
    public Point2D virtualToScreen(Point2D aP);
    public Point2D screenToVirtual(Point2D aP);   
    public void repaint();
    
    public void addFrameChangeListener(FrameChangeListener anEvt);
    @Override
    public void addWindowChangeListener(WindowChangeListener anEvt); 
    @Override
    public void removeListenerFromAllLists(Object aListener);
}

