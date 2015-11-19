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

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Range;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
/**
 *
 * @author likhachev
 */
public class GridImageView extends ImageView {
    
    private int iRows;
    private int iColumns;
    
    GridImageView(IMultiframeImage anImage, int aRows, int aColunmns) {
        super(anImage);
        iRows = aRows;
        iColumns = aColunmns;
    }
        
    public void setGrid(int aRows, int aColunmns) {
        iRows = aRows;
        iColumns = aColunmns;
    }
    
    public boolean loadFrame(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {        
            iCurrent = aN;
            
            Double min = Double.MAX_VALUE;
            Double max = Double.MIN_VALUE;
            
            for (int n = 0; n < iRows * iColumns; ++n) {             
                min = Math.min(min, iModel.getAt(iCurrent + n).getMin()); 
                max = Math.max(max, iModel.getAt(iCurrent + n).getMax());
            } 
            
            iLUTMgr.setRange(new Range(min, max)); // frameChanged();

            iROIMgr.update();   

            notifyFrameChanged();
            notifyWindowChanged();

            invalidateBuffer();
        }
        
        return true;
    }
    
    private final int GAPX = 2;
    private final int GAPY = 4;
    
    
    protected int getVisualWidth() {
        return iColumns*(getImage().getWidth() + GAPX) + GAPX;
    }
    
    protected int getVisualHeight() {
        return iRows*(getImage().getHeight() + GAPY) + GAPY;
    }
    
    protected void updateBufferedImage() {                  
        updateScale();
        
        RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, getInterpolationMethod());
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);
                
        final int width  = getImage().getWidth();
        final int height = getImage().getHeight();   
                       
        BufferedImage tmp = new BufferedImage(getVisualWidth(), 
                                                 getVisualHeight(), 
                                                    BufferedImage.TYPE_INT_RGB);//getImage().getAt(getCurrent()).getBufferedImage().getType());
        Graphics2D gc = tmp.createGraphics(); 
       
        if (getLUTMgr().isInverted()) { 
            final Color old = gc.getColor();
            gc.setColor(Color.WHITE);
            gc.fillRect(0, 0, getVisualWidth(), getVisualHeight());
            gc.setColor(old);
        }
       
        int j = 0;
        int posy = GAPY;
        do {   
            int posx = GAPX; 
            
            for (int i = 0; i < iColumns; ++i) {
                final int ndx = getCurrent() + j*iColumns + i;
                              
                if (getImage().hasAt(ndx)) {                    
                    BufferedImage img = getImage().getAt(ndx).getBufferedImage();
                    BufferedImage src = getLUTMgr().transform(img, null);

                    gc.drawImage(src, posx, posy, width, height, null);
                    gc.setColor(Color.RED);
                    gc.drawString(String.format("%d", ndx), posx + 2, posy + 12);
                
                } else {
                    if (getLUTMgr().isInverted())                          
                        gc.setColor(Color.WHITE);
                    else
                        gc.setColor(Color.BLACK);
                    gc.fillRect(posx, posy, width, height);                    
                }      
                
                
                posx += GAPX + width;
            }
            
            posy += GAPY + height;     
        } while (++j < iRows);
       
                    
        iBuf = z.filter(tmp, null);      
        gc.dispose();
    }
    
}
