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

import java.awt.Color;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import com.ivli.roim.core.Extractor;
import com.ivli.roim.core.ImageFrame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class Profile extends ScreenObject {   
    public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
    public static final Color VIOLET     = new Color(0.93333334f, 0.50980395f, 0.93333334f);
    
    private boolean  iShow = true;    
    private boolean  iNormalize = false;
    private double[] iHist;
    
    public Profile(Rectangle2D aS, ROIManager aMgr) {
        super("PROFILE", aS, aMgr); //NOI18N 
        iHist = getManager().getFrame().processor().histogram(iShape.getBounds()); 
    }
    
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU;
    }
    
    /*    */
    @Override
    public void paint(AbstractPainter aP) {
        aP.paint(this);
    } 
 
    @Override
    public void update() {
        iHist = getManager().getFrame().processor().histogram(iShape.getBounds());      
    }            
    
    @Override
    public void move(double adX, double adY) {             
        final Rectangle2D r = iShape.getBounds2D();
        
        Shape temp = AffineTransform.getTranslateInstance(.0, adY).createTransformedShape(
                                 new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), Math.max(1.0, r.getHeight() + adX))
                              );  
        
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getImage().getWidth(), getManager().getImage().getHeight());
        
        if (bounds.contains(temp.getBounds())) {
            iShape = temp;
           /// update();
        }
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
    
    public double[] getHistogram() {
         return iHist;
    }
    
        private static final Logger LOG = LogManager.getLogger();
}
