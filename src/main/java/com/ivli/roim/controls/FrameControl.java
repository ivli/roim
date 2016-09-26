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

import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Window;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.view.ActionItem;
import com.ivli.roim.view.ImageView;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author likhachev
 */
public class FrameControl extends JComponent implements FrameChangeListener, ActionListener, 
                                                          MouseMotionListener, MouseListener, MouseWheelListener {
    
    private static final int MARKER_CURSOR = Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = Cursor.N_RESIZE_CURSOR;                           
    private static final int LEFT_GAP  = 2;  //reserve border at left & right
    private static final int RIGHT_GAP = 2; 
    private final int TOP_GAP;  //reserve a half of marker height at window's top & bottom 
    private final int BOTTOM_GAP;
    
    private final Marker iTop;
    private final Marker iBottom;
    private ActionItem iAction;
    private BufferedImage iBuf;
      
    private boolean iCanShowDialog;
    private ImageView  iView;       
        
    
    FrameControl() {
        iTop = new Marker("images/knob_bot.png", false);
        iBottom = new Marker("images/knob_bot.png", false);
        TOP_GAP = iTop.getMarkerHeight()/2;
        BOTTOM_GAP = iBottom.getMarkerHeight()/2; //to the case images of different height are used 
    }
    
     public static FrameControl create(ImageView aV) {
        FrameControl ret = new FrameControl();
       // ret.construct(aV);
        aV.addFrameChangeListener(ret);
       // aV.addWindowChangeListener(ret); 
        return ret;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        assert (null != iView);
    }
    
    @Override
    public void frameChanged(FrameChangeEvent anE) {                   
        makeBuffer();                 
    }   

    @Override
    public void mouseDragged(MouseEvent e) {
        if (null != iAction) 
            iAction.action(e.getX(), e.getY());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (null != iAction)
            iAction.wheel(e.getWheelRotation());
        else {
         
        }            
    } 
    
    @Override
    public void mouseMoved(MouseEvent e) {
        final int ypos = getHeight() - e.getPoint().y;

        if (iTop.contains(ypos) || iBottom.contains(ypos))
            setCursor(java.awt.Cursor.getPredefinedCursor(MARKER_CURSOR));
        else if (ypos < iTop.getPosition() && ypos > iBottom.getPosition())
            setCursor(java.awt.Cursor.getPredefinedCursor(WINDOW_CURSOR));
        else 
            setCursor(java.awt.Cursor.getDefaultCursor());                                     
    }
       
    @Override
    public void mousePressed(MouseEvent e) {        
        final int ypos = getHeight() - e.getPoint().y;

        if (iTop.contains(ypos) || iBottom.contains(ypos) || iTop.getPosition() + 4 > ypos && iBottom.getPosition() - 4 < ypos) {  
            iAction = new ActionItem(e.getX(), e.getY()) {

                boolean first = true;
                final boolean iMoveTop = iTop.contains(ypos);    

                final boolean iMoveBoth = !(iMoveTop || iBottom.contains(ypos)) && iTop.getPosition() > ypos && iBottom.getPosition() < ypos;  

                protected void DoAction(int aX, int aY) {
                    

                }   

                protected boolean DoWheel(int aX) {
                    final Window win = new Window(iView.getWindow());                     

                    return false;
                }
            };                
        } 
        else if (SwingUtilities.isLeftMouseButton(e)) {
            //iAction = NewAction(iLeftAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isMiddleMouseButton(e)) {
            //iAction = NewAction(iMiddleAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            //iRight.Activate(e.getX(), e.getY());
            //iAction = NewAction(iRightAction, e.getX(), e.getY());
        }                
    }

    @Override
    public void mouseReleased(MouseEvent e) {                    
        iAction = null;  
        if (!getBounds().contains(e.getPoint())){
           
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {        
        //iActive = true;
        if (null == iAction) {
           
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (null == iAction) {           
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {    
        /*
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());
        else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)                                            
            directChangeWindow(new Window(iRange));         
*/
    }

    private  ImageFrame iBackFrame;
    
    private void makeBuffer() {
        final int width  = getWidth()  - (LEFT_GAP + RIGHT_GAP);
        final int height = getHeight() - (TOP_GAP + BOTTOM_GAP);               
        
        if (width <=0 || height <=0) {
           iBackFrame = null;
           return;
        }
            
        iBackFrame = new ImageFrame(width, height);
        //final double ratio = iRange.range() / height;
        
        for (int i = 0; i < width; ++i) {                  
            for (int j = 0; j < height; ++j) {                                     
                iBackFrame.setPixel(i, j, 0);
            }                      
        }
    }
    
}
