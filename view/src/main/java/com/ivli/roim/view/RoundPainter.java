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
import java.awt.geom.Rectangle2D;
/**
 *
 * @author likhachev
 */
public class RoundPainter extends BasePainter {       
    private static final double ARC_WIDTH  = .2;
    private static final double ARC_HEIGHT = .2;   
   
    public RoundPainter(Graphics2D aGC, IImageView aV) {       
        super(aGC, aV);
    }
     
    public void paint(Annotation aO) {                  
        final Rectangle2D temp = updateAnnotationShape(aO);    
        iGC.setColor(aO.getColor());

        if (!aO.isMultiline()) {
            String str = new String();
            str = aO.getText().stream().map((s) -> s).reduce(str, String::concat);            
            iGC.drawString(str, (int)temp.getX() + 2, (int)(temp.getY() + temp.getHeight() - 4));                   
        } else {            
            final double stepY = temp.getHeight() / aO.getText().size();
            double posY = temp.getY() + stepY - 4.;
            for(String str : aO.getText()) {                               
                iGC.drawString(str, (int)temp.getX() + 2, (int)posY);
                posY += stepY;
            }
        }
                
        final int arc= (int)Math.min(temp.getWidth()*ARC_WIDTH, temp.getHeight()*ARC_HEIGHT);
        
        iGC.drawRoundRect((int)temp.getX(), (int)temp.getY(), (int)temp.getWidth(), (int)temp.getHeight(), 
                          arc, arc);                            
    }     
}
