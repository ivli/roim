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

import com.ivli.roim.calc.AbstractFormatter;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.calc.Operand;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import com.ivli.roim.core.Uid;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends ScreenObject {
    private double iDistance;
    
    Ruler(Uid anUid, Shape aS, IImageView aV) {         
        super(anUid, aS, "RULER",  aV);            
    }
          
    @Override
    void paint(AbstractPainter aP) {   
        if (aP.getView() == getView())
            aP.paint(this);    
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU | PINNABLE;
    }
    
    @Override
    void update(OverlayManager aM) {  
        Point2D.Double a = null, b = null;
        
        PathIterator pi = iShape.getPathIterator(null);
        
        while(!pi.isDone()) {
            double [] temp = new double [2];
            switch(pi.currentSegment(temp)){
                case PathIterator.SEG_MOVETO:
                    a = new Point2D.Double(temp[0], temp[1]); break;
                case PathIterator.SEG_LINETO:
                    b = new Point2D.Double(temp[0], temp[1]); break;
                default:
                    return;
            }   
            pi.next();
        }
        
        if (null == a || null == b)
            return; 
        
        iDistance = a.distance(b) * aM.getImage().getPixelSpacing().getX();
        LOG.debug("distance is: {}", iDistance);           
    }       
    
    IOperation getOperation() {                
        return new IOperation() {
            Ruler iR = Ruler.this;
            @Override
            public IOperand value() {                
                return new Operand(new Scalar(Measurement.DISTANCE, iR.iDistance));
            }
            @Override
            public String format(AbstractFormatter aF) {
                return aF.format(value());
            }            
        };
    }
    
    public void showDialog(Object anO) {}
    
    @Override
    public JMenuItem [] makeCustomMenu(Object aVoidStar){return null;}    
    @Override
    public boolean handleCustomCommand(final String aCommand){return false;}
    
    private static final Logger LOG = LogManager.getLogger();
}
