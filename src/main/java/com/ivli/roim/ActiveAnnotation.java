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


import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.calc.IOperation;


/**
 *
 * @author likhachev
 */
public class ActiveAnnotation extends Overlay implements ROIChangeListener {
    
    Color    iColor = Color.RED;
    String   iAnnotation;
    IOperation       iOp;
    
    ActiveAnnotation(ROIManager aRM, IOperation aOp) {
        super("ANNOTATION.ACTIVE", null, aRM);        
        iOp = aOp;     
        iAnnotation = iOp.getCompleteString();
        final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
        /**/
       
        iShape = new Rectangle2D.Double(0, 0, bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());   
    }

    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {              
        switch (anEvt.getChange()) {
            case Cleared: 
               break;
            case Moved:                  
            case Changed:   
            default: //fall-through
                update();                                
                break;
        }        
    }
    
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        final Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();     
        aGC.setColor(iColor);

        aGC.drawString(iOp.getCompleteString(), (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));       
        aGC.draw(temp);                 
    }
           
    public void update() {        
        iAnnotation = iOp.getCompleteString();
        
        final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
        /**/
        final double scaleX =  getManager().getView().screenToVirtual().getScaleX();       
        iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), getShape().getBounds2D().getY(),                                                                                        
                                        bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);
    }
    
    @Override
    int getCaps() {
        return HASMENU|MOVEABLE|SELECTABLE|PINNABLE;
    }
}
