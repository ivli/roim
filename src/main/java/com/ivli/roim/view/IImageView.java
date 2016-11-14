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

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Window;
import com.ivli.roim.view.ROIManager;

import java.awt.geom.AffineTransform;

/**
 *
 * @author likhachev
 */
public interface IImageView extends WindowTarget {    
    public void setImage(IMultiframeImage anImage);
    public IMultiframeImage getImage();    
    public ImageFrame getFrame();    
    public int getFrameNumber();    
    public boolean setFrameNumber(int aN);    
    public void    setROIMgr(ROIManager aR);
    public ROIManager getROIMgr();
       
    public enum ZoomFit {       
        NONE,    //no fit        
        VISIBLE, //fit entire image into view      
        WIDTH,   //width      
        HEIGHT,  //height      
        PIXELS;  //fit to display image pixel to pixel no matter how big it is
    }
   
    public void setFit(ZoomFit aW);    
    public void pan(int aX, int aY);
    public void zoom(double aStep);    
    public AffineTransform getZoom();
    public void setInterpolationMethod(Object aM);    
    public void reset();
    public AffineTransform virtualToScreen();
    public AffineTransform screenToVirtual();    
    public void repaint();
}

