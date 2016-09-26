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
package com.ivli.roim.controls;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;

/**
 *
 * @author likhachev
 */
class Marker {           
    protected boolean iVert;
    protected int     iPos; 
    protected Image   iImage;

    Marker(final String anImageFileName, boolean aVertical) {    
        iVert = aVertical;
        iPos = 0;                              
        try {                       
            iImage = javax.imageio.ImageIO.read(ClassLoader.getSystemResource(anImageFileName));//"images/knob_bot.png")); //NOI18N                                   
         } catch (IOException ex) {              
             //LOG.error("FATAL!!!", ex); //NOI18N               
         }         
    }

    int getMarkerHeight() {
        return iImage.getHeight(null);
    }

    void setPosition(int aPos) {           
        iPos = aPos;
    } 

    int getPosition() {
        return iPos;
    }             

    boolean contains(int aVal) {
        final int ypos = iPos;//getHeight() - (TOP_GAP + BOTTOM_GAP) - iPos;
        final int height = (null != iImage) ? iImage.getHeight(null) : 4;
        return aVal < ypos + height && aVal > ypos  /*- half_height*/;
    }

    void draw(Graphics aGC, Rectangle aRect) {                                                   
        final int ypos = aRect.height - getMarkerHeight() - iPos;// + ((iName == "top") ? TOP_GAP : BOTTOM_GAP);
        aGC.drawImage(iImage, 0, ypos, null);                
/*
        if (MARKERS_DISPLAY_WL_VALUES) {
            final double val = MARKERS_DISPLAY_PERCENT ? screenToImage(iPos) * 100.0 / iRange.range() : screenToImage(iPos);
            final String out = String.format("%.0f", Math.abs(val)); //NOI18N
            final Rectangle2D sb = aGC.getFontMetrics().getStringBounds(out, aGC);    
            final int height = (null != iImage) ? iImage.getHeight(null) : 4;

            aGC.setColor(Color.BLACK);    
            aGC.drawString(out, (int)(getWidth()/2 - sb.getWidth()/2), 
                          (int)(getHeight() - iPos - height / 2 + sb.getHeight() / 2 )
                          );
        }      
*/
    }         
}        
       
