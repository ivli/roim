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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Range;
import com.ivli.roim.core.Window;

/**
 *
 * @author likhachev
 */
public class GridImageView extends ImageView {    
    private int iRows;
    private int iCols;
    private boolean iShowFrames = true;
    
    private static final String KCommandShowFrameNumbers = "MNU_CONTEXT_GRIDVIEW.SHOW_FRAME_NUMBERS"; // NOI18N
    private static final String KCommandOptimalLayout = "MNU_CONTEXT_GRIDVIEW.LAYOUT_OPTIMAL"; // NOI18N
    
    public static GridImageView create(IMultiframeImage aI, int aRows, int aCols) { 
        GridImageView ret = new GridImageView();
        ret.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        ret.setFit(ImageView.ZoomFit.VISIBLE);
        ret.setController(new GridViewController(ret));
        ret.setROIMgr(new ROIManager(aI));
        ret.setImage(aI);
        ret.setGrid(4, 4);
        return ret;
    }
        
    static class GridViewController extends Controller {      
        
        GridViewController(GridImageView aI) {
            super(aI);
        }
        
        @Override
        JPopupMenu buildContextPopupMenu() {
            GridImageView v = (GridImageView)iControlled;
            
            JPopupMenu mnu = new JPopupMenu("MNU_CONTEXT_GRIDVIEW"); 
            {                   
                {
                JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_CONTEXT_GRIDVIEW.LAYOUT_OPTIMAL"));
                mi.addActionListener(this);
                mi.setActionCommand(KCommandOptimalLayout); 
                mnu.add(mi);
                } 
                {
                JCheckBoxMenuItem mi = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_CONTEXT_GRIDVIEW.SHOW_FRAME_NUMBERS"));
                mi.addActionListener(this);
                mi.setActionCommand(KCommandShowFrameNumbers); 
                mi.setState(v.isShowFrames());
                mnu.add(mi);
                }                                         
            }
            return mnu;
        }

        @Override
        public void keyPressed(KeyEvent e) { 
            GridImageView v = (GridImageView)iControlled;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_EQUALS:    
                    v.setGrid(v.getRows(), v.getCols());                       
                    break;
                case KeyEvent.VK_MINUS: /*
                    if (iRows > 1 && iCols > 2) {                                                    
                        setGrid(--iRows, iCols);                        
                    } else if (1 == iRows && iCols > 1) {
                        setGrid(iRows, --iCols);
                    } */break;                        
                default: break;
            }
        }

        JPopupMenu buildObjectSpecificPopupMenu() {
            return buildContextPopupMenu();
        }

        protected boolean handleCustomCommand(ActionEvent aCommand) {
            GridImageView v = (GridImageView)iControlled;
            switch(aCommand.getActionCommand()) {
                case KCommandOptimalLayout:
                    return true;
                case KCommandShowFrameNumbers: {
                    v.setShowFrames(!v.isShowFrames());
                    //v.invalidateBuffer();
                    v.repaint();
                } break;
            }
            return false;
        }
    };   
    
    protected GridImageView(){//int aRows, int aCols) {            
        iRows = 1;
        iCols = 1;        
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
    public boolean loadFrame(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {        
            iCurrent = aN;
            
            Double min = Double.MAX_VALUE;
            Double max = Double.MIN_VALUE;
            
            for (int n = 0; (iCurrent + n) < iModel.getNumFrames() && n < iRows * iCols; ++n) {             
                min = Math.min(min, iModel.get(iCurrent + n).getRange().getMin()); 
                max = Math.max(max, iModel.get(iCurrent + n).getRange().getMax());
            } 
            
            iVLUT.setWindow(new Window(new Range(min, max))); // frameChanged();

            iROIMgr.update();   

            notifyFrameChanged();
            notifyWindowChanged();

            invalidateBuffer();
        }
        
        return true;
    }
    
    private final int GAPX = 2;
    private final int GAPY = 4;
        
    @Override
    protected int getVisualWidth() {
        return iCols*(iModel.getWidth() + GAPX) + GAPX;
    }
    
    @Override
    protected int getVisualHeight() {
        return iRows*(iModel.getHeight() + GAPY) + GAPY;
    }
    
    @Override
    protected void updateBufferedImage() {                  
        updateScale();
        
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
    }    
}
