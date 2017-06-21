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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Uid;
import com.ivli.roim.core.Window;

/**
 *
 * @author likhachev
 */
public class GridImageView extends ImageView {    
    private int iRows;
    private int iCols;
    private boolean iShowFrames = true;
   
    public static GridImageView create(IMultiframeImage aI, int aRows, int aCols, ROIManager aMgr) { 
        GridImageView ret = new GridImageView();
        ret.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        ret.setFit(ImageView.ZoomFit.VISIBLE);
        ret.setController(new GridViewController(ret));
        ret.setROIMgr(null != aMgr ? aMgr : ROIManager.create(aI));
        ret.setImage(aI);
        ret.setGrid(aRows, aCols);
        return ret;
    }
   
    private void checkGrid() {
        final int n = iModel.getNumFrames();
        if (n < 2) {
            iRows = iCols = 1;
        } else if (n < iRows * iCols) {
            
        }  
    }
    
    public void setImage(IMultiframeImage anImage) {
        super.setImage(anImage);
        checkGrid();   
    }
    
    public void setShowFrames(boolean aS) {
        iShowFrames = aS; 
        invalidateBuffer();
    }   
    
    public boolean isShowFrames() {
        return iShowFrames;         
    }   
    
    public int getRows() {return iRows;}
    public int getCols() {return iCols;}
                    
    public void setGrid(int aRows, int aColunmns) {
        iRows = aRows;
        iCols = aColunmns;
        checkGrid(); 
        invalidateBuffer();
       // repaint();
    }
    
    @Override
    public boolean setFrameNumber(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {        
            iCurrent = aN;
            
            Double min = Double.MAX_VALUE;
            Double max = Double.MIN_VALUE;
            
            /* 
            for (int n = 0; n < iModel.getNumFrames(); ++n) {     
                Range r = iModel.get(n).getMax() - iModel.get(n).getMin();
                min = Math.min(min, r.getMin()); 
                max = Math.max(max, r.getMax());
            } 
            */
            iVLUT.setWindow(Window.fromRange(min, max)); // frameChanged();

            iMgr.update();   

            notifyFrameChanged();
            notifyWindowChanged();

            invalidateBuffer();
        }
        
        return true;
    }
    
    private final int GAPX = 2;
    private final int GAPY = 4;
        
    @Override
    public int getVisualWidth() {
        return iCols*(iModel.getWidth() + GAPX) + GAPX;
    }
    
    @Override
    public int getVisualHeight() {
        return iRows*(iModel.getHeight() + GAPY) + GAPY;
    }
    
    @Override
    protected void updateBufferedImage() {                  
        updateScale();
        /*
        RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, getInterpolationMethod());
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);
                
        final int width  = iModel.getWidth();
        final int height = iModel.getHeight();   
                       
        BufferedImage tmp = new BufferedImage(getVisualWidth(), 
                                              getVisualHeight(), 
                                              BufferedImage.TYPE_INT_RGB);//getImage().get(getFrameNumber()).getBufferedImage().getType());
        Graphics2D gc = tmp.createGraphics(); 
       
        if (isInverted()) { 
            final Color old = gc.getColor();
            gc.setColor(Color.WHITE);
            gc.fillRect(0, 0, getVisualWidth(), getVisualHeight());
            gc.setColor(old);
        }
       
        int j = 0;
        int posy = GAPY;
        
        do {   
            int posx = GAPX; 
            
            for (int i = 0; i < iCols; ++i) {
                final int ndx = getFrameNumber() + j*iCols + i;
                              
                if (iModel.hasAt(ndx)) {                    
                    ///BufferedImage img = createBufferedImage(iModel.get(ndx));//.getBufferedImage();
                    BufferedImage src = iVLUT.transform(iModel.get(ndx), null);

                    gc.drawImage(src, posx, posy, width, height, null);
                    
                    if (iShowFrames) {
                        gc.setColor(Color.RED);
                        gc.drawString(String.format("%d", ndx), posx + 2, posy + 12); // NOI18N
                    }                
                } else {
                    if (this.isInverted())                          
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
*/
    }    
}
