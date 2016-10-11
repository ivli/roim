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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author likhachev
 */
public abstract class AbstractPainter {    
    ImageView        iView;
    Graphics2D         iGC;
    AffineTransform iTrans;    
    
    public AbstractPainter(Graphics2D aGC, AffineTransform aT, ImageView aV) {
        iView = aV;
        iGC = aGC;
        iTrans = aT;
    }
    
    public ImageView getView() {return iView;}
    
    public abstract void paint(Overlay aO);
    public abstract void paint(ROI aO);
    public abstract void paint(Annotation aO);    
    public abstract void paint(Ruler aO);
    public abstract void paint(Profile aO);
}
