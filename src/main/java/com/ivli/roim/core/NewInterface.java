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

import com.ivli.roim.view.ROIManager;
import java.awt.geom.AffineTransform;

/**
 *
 * @author likhachev
 */
public interface NewInterface {

    ImageFrame getFrame();

    int getFrameNumber();

    IMultiframeImage getImage();

    //abstract public void repaint();
    ROIManager getROIMgr();

    Window getWindow();

    AffineTransform getZoom();

    boolean loadFrame(int aN);

    void pan(int aX, int aY);

    void reset();

    AffineTransform screenToVirtual();

    void setImage(IMultiframeImage anImage);

    void setInterpolationMethod(Object aM);

    void setWindow(Window aW);

    AffineTransform virtualToScreen();

    void zoom(double aStep);
    
}
