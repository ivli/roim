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

import static com.ivli.roim.view.Profile.BLUEVIOLET;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author likhachev
 */
public class ROIPainter extends AbstractPainter {
    
    public static final Color EMPHASIZED_COLOR = Color.RED;
    
    ROIPainter(Graphics2D aGC, AffineTransform aT, ImageView aV) {
        super(aGC, aT, aV);
    }
    
    public void paint(Overlay aO) {}
    
    public void paint(ROI aO) {
        if (aO.isEmphasized()) {
            iGC.setColor(EMPHASIZED_COLOR);
            iGC.setStroke(new BasicStroke(2));        
        } else {
            iGC.setColor(aO.getColor());
        }
        
        iGC.draw(iTrans.createTransformedShape(aO.getShape()));             
    }
    
    public void paint(Profile aO) { 
        final Rectangle bn = iTrans.createTransformedShape(aO.getShape()).getBounds();          
        final Color tmp = iGC.getColor();
        
        iGC.setColor(BLUEVIOLET);
        iGC.drawLine(bn.x, bn.y, bn.x+bn.width, bn.y);                           
        
        iGC.setColor(java.awt.Color.RED);
        iGC.drawLine(bn.x, bn.y+bn.height, bn.x+bn.width, bn.y+bn.height);
      
        iGC.setColor(tmp);
        /**/
        if (aO.isShowHistogram()) {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double [] h = aO.getHistogram();

            for (double d : h) {
                min = Math.min(min, d);
                max = Math.max(max, d);
            }

            final double range = iView.getFrame().getRange().range();// maxV - minV; 

            Rectangle bounds = new Rectangle(0, 0, iView.getImage().getWidth(), iView.getImage().getHeight());

            final double scale = Math.min(aO.getShape().getBounds().getY()/(4*range), bounds.getHeight()/(4*range));                                              
            Path2D.Double s = new Path2D.Double();        
            //int n = 0;
            s.moveTo(0, aO.getShape().getBounds().getY() - h[0] * scale);

            for (int n = 1; n < h.length; ++n) 
                s.lineTo(n, aO.getShape().getBounds().getY() - h[n] * scale);

            iGC.setXORMode(Color.WHITE);             
            iGC.draw(iTrans.createTransformedShape(s));                
            iGC.setPaintMode(); //turn XOR mode off    
        }
    }
  
    public void paint(Ruler aO) {
        final Shape rect = iTrans.createTransformedShape(aO.getShape());                           
        iGC.setColor(Color.YELLOW);       
        iGC.draw(rect);       
    }   
    
    public void paint(Annotation aO) {        
       //void paint(Graphics2D aGC, AffineTransform aTrans) {
            final Rectangle2D temp = iTrans.createTransformedShape(aO.getShape()).getBounds();     
            iGC.setColor(aO.getColor());
            
            if (!aO.isMultiline()) {
                String str = new String();
                str = aO.getText().stream().map((s) -> s).reduce(str, String::concat);            
                iGC.drawString(str, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));                   
            } else {            
                final double stepY = temp.getHeight() / aO.getText().size();
                double posY = temp.getY() + stepY - 4.;
                for(String str : aO.getText()) {                               
                    iGC.drawString(str, (int)temp.getX(), (int)posY);
                    posY += stepY;
                }
            }
            iGC.draw(temp);                 
        }
    /*
    public void paint(Annotation.Active aO) {           
        Rectangle2D tmp = iTrans.createTransformedShape(aO.getShape()).getBounds2D();            
        iGC.setColor(Color.RED);

        iGC.drawString(aO.getText(), (int)tmp.getMinX(), (int)tmp.getMaxY());       
        //aGC.draw(tmp);        
    }    
    */
}
