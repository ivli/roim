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
package com.ivli.roim.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.ivli.roim.core.Histogram;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Uid;
/**
 *
 * @author likhachev
 */
public class Profile extends ScreenObject {      
    private boolean    iShow = true;    
    private boolean    iNormalize = false;
    private Histogram  iHist;
    private ImageFrame iFrame;
    private int iFrameNumber;
    
    
    public Profile(Rectangle2D aS, IImageView aF) {
        super(Uid.getNext(), "PROFILE", (Shape)aS); //NOI18N 
        iFrameNumber = aF.getFrameNumber();
        iFrame = aF.getFrame();        
        iHist = iFrame.processor().histogram(iShape.getBounds()); 
    }
    
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU | FRAMESCOPE;
    }
        
    @Override
    public void paint(AbstractPainter aP) {
        aP.paint(this);
    } 
 
    @Override
    public void update(OverlayManager aM) {        
        iHist = iFrame.processor().histogram(iShape.getBounds());      
    }            
    
    public int getFrameNumber() {
        return iFrameNumber;
    }
    
    public boolean normalize() {
        return iNormalize = !iNormalize; 
    }
   
    public boolean isShowHistogram() {
        return iShow; 
    }
    
    public void showHistogram(boolean aS) {
         iShow = aS; 
    }
    
    public Histogram getHistogram() {
         return iHist;
    }        
}
