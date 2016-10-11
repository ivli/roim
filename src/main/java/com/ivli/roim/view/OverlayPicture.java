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

import java.awt.Rectangle;
/**
 *
 * @author likhachev
 */
public class OverlayPicture extends ScreenObject {
    
    java.awt.Image iPicture;
    //Point iOrigin=new Point(0,0);
    
    OverlayPicture(IImageView aV, java.awt.Image aP, String aN) { 
        super(aV, aN, new Rectangle(aP.getWidth(null), aP.getHeight(null)));       
        iPicture = aP;
    }
    
    int getCaps(){return CANFLIP|CANROTATE|HASMENU|PERMANENT;}
    
    void paint(AbstractPainter aP) {
        aP.paint(this);
    }
    
    
    /*
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.KEY_INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(aTrans, hts);
                    
        //BufferedImage src = iWM.transform(true != SUMMED_FRAME_PANE ? iImg.getBufferedImage():iImg.makeCompositeFrame(0, -1), null);
        
        //java.awt.Image iBuf = z.filter(iPicture, null);   
        Rectangle r = getShape().getBounds();
        //aTrans.
        AffineTransform trans = AffineTransform.getTranslateInstance(aTrans.getTranslateX(), aTrans.getTranslateY());
        
        //aGC.drawImage(iBuf, 0, 0, r.width, r.height, null);
        aGC.drawImage(iPicture, trans, null);        
    } 
    */
    
    void update(OverlayManager aM){}
         
}
