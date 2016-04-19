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


import com.ivli.roim.core.FrameOffset;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.FrameOffsetVector;
/**
 *
 * @author likhachev
 */
public class OffsetImageView extends ImageView {
    
    FrameOffsetVector iOff;
    
    public OffsetImageView() {
        super();
    }
    
    public void setImage(IMultiframeImage anImage) {
        super.setImage(anImage);
        iOff = new FrameOffsetVector(anImage.getNumFrames());        
    }
       
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;      
        iOff.put(getFrameNumber(), new FrameOffset(iOrigin.x, iOrigin.y));      
    }
     
    public boolean loadFrame(int aN) {
        if(super.loadFrame(aN)) {
            FrameOffset fof = iOff.get(aN);
            iOrigin.x = fof.getX();
            iOrigin.y = fof.getY();        
            return true;
        }
            
        return false;        
    }
        
    protected void updateBufferedImage() {                  
        updateScale();
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);
        BufferedImage src  = this.transform(iBufImage, null);
                
        iBuf = z.filter(src, null);                  
    }
    
    @Override
    public void paintComponent(Graphics g) {                   
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);        
        
        iROIMgr.paint((Graphics2D)g, iZoom);                         
        
        iController.paint((Graphics2D)g); //must reside last in the paint queue   
    }
}
