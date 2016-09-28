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

import com.ivli.roim.core.Instant;
import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.view.ActionItem;
import com.ivli.roim.view.Colorer;
import com.ivli.roim.view.ImageView;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 *
 * @author likhachev
 */
public class FrameControl extends JComponent implements FrameChangeListener, ActionListener, 
                                                          MouseMotionListener, MouseListener, MouseWheelListener {
    
    private static final int MARKER_CURSOR = Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = Cursor.N_RESIZE_CURSOR;                           
    private final int LEFT_GAP;  //reserve border at left & right
    private final int RIGHT_GAP; 
    private final int TOP_GAP  = 2;  //reserve a half of marker height at window's top & bottom 
    private final int BOTTOM_GAP  = 2;
    
    private final Marker iMarker;    
    private ActionItem   iAction;
    private BufferedImage iBuf;
          
    private ImageView  iView;       
    
     //TODO: rewrite to be configured at instantiation
    private static final boolean DRAW_PHASES_LEGEND = true;
   
    private static final boolean ANNOTATE_MARKER = false; 
    private static final boolean DRAW_FRAME_MINIATURES = true;
       
    private boolean iTimeWiseScale = false; 
     private double conversion = 1; // holds a value used to conversion pixels to time or pixels to frame number and vice versa
     
    FrameControl() {
        iMarker = new Marker("images/knob_cone_vert.png", false);        
        LEFT_GAP  = iMarker.getMarkerSize()/2;
        RIGHT_GAP = iMarker.getMarkerSize()/2; //to the case images of different height are used 
    }
    
    public static FrameControl create(ImageView aV) {
        FrameControl ret = new FrameControl();
        ret.construct(aV);
        aV.addFrameChangeListener(ret);       
        return ret;
    }
        
    private void setTimeWise(boolean aTimeWise) {
        iTimeWiseScale = aTimeWise;
        TimeSliceVector tsv = iView.getImage().getTimeSliceVector();                
                conversion = iTimeWiseScale ? (double)tsv.duration() / (double)getActiveBarWidth() :
                                              (double)tsv.getNumFrames() / (double)getActiveBarWidth();
                
        LOG.debug("duration=" + tsv.duration() + ", width=" + getActiveBarWidth() + ", millis per pixel=" + conversion);
                
    }
    
    private void construct(ImageView aW) {    
        iView = aW;    
     
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {   
                setTimeWise(iTimeWiseScale);
                
                makeBuffer();
                repaint();
            }                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}                    
        });
              
        addMouseMotionListener(this);
        addMouseListener(this);  
        addMouseWheelListener(this);   
    }
  
    
    @Override
    public void frameChanged(FrameChangeEvent anE) {       
        if (iTimeWiseScale)
            iMarker.setPosition((int)(iView.getImage().getTimeSliceVector().frameStarts(anE.getFrame()) / conversion));
        else
            iMarker.setPosition((int)(anE.getFrame() / conversion));
        repaint();
    }   
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(256, 32);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Short.MAX_VALUE, 32);
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Short.MAX_VALUE, 32);
    }

    @Override
    public void mouseDragged(MouseEvent e) {       
        if (null != iAction) {
            iAction.action(e.getX(), e.getY());
            repaint();
        }
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
        final int xpos = e.getPoint().x;

        if (iMarker.contains(xpos))
            setCursor(java.awt.Cursor.getPredefinedCursor(MARKER_CURSOR));        
        else 
            setCursor(java.awt.Cursor.getDefaultCursor());                                     
    }
    
    final void setMarkerPosition(int aPos) {
        if (null == iView)
            iMarker.setPosition(aPos);
        else {
            if (iTimeWiseScale)
                iView.setFrameNumber(iView.getImage().getTimeSliceVector().frameNumber(new Instant((long)(conversion*(double)aPos))));     
            else
               iView.setFrameNumber((int)(conversion*(double)aPos));             
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {               
        if (SwingUtilities.isLeftMouseButton(e)) {
            final int xpos = e.getPoint().x;
            //hit marker icon then get ready to drag
            if (iMarker.contains(xpos)) {  

                iAction = new ActionItem(e.getX(), e.getY()) {
                    protected void DoAction(int aX, int aY) { 
                        if (aX >= LEFT_GAP && aX < getActiveBarWidth() + RIGHT_GAP) {
                            setMarkerPosition(aX - LEFT_GAP);
                            //repaint();
                        }
                    }   

                    protected boolean DoWheel(int aX) {                    
                        return false;
                    }
                };    
            //don't hit but still within active rectangle - just move marker            
            } else if (xpos >= LEFT_GAP && xpos < getActiveBarWidth() + RIGHT_GAP) {
                setMarkerPosition(xpos - LEFT_GAP);
                //repaint();
            }              
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
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());        
    }
   
   
    private void makeBuffer() {             
        if (null == iBuf)
            iBuf = new BufferedImage(getActiveBarWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        
        Graphics g = iBuf.getGraphics();
               
        g.setColor(Color.LIGHT_GRAY);              
        g.fillRect(0, 0, iBuf.getWidth(), iBuf.getHeight());
        
        if (DRAW_PHASES_LEGEND && null != iView && iView.getImage().getTimeSliceVector().getNumPhases() > 1) {
            final TimeSliceVector tsv = iView.getImage().getTimeSliceVector();
            int xstart = 0;
            int width = 0;
            for(int i =0; i < tsv.getNumPhases(); ++i) {
                width = iTimeWiseScale ? (int) (tsv.getPhaseDuration(i) / conversion) - 1 :
                                         (int) (tsv.getPhaseFrames(i) / conversion) - 1;
                g.setColor(Colorer.getColor(i));//RAINBOW[i%RAINBOW.length]);
                g.drawRect(xstart, 0, width, getHeight() - TOP_GAP);
                xstart += width + 1;
            }           
        } 
    }
    
    int getActiveBarWidth() {
        return getWidth() - (iReserveSpaceRight ? 2*(RIGHT_GAP + LEFT_GAP) : (RIGHT_GAP + LEFT_GAP));
    }
    
    private final boolean iReserveSpaceRight = true; 
    
    @Override
    public void paintComponent(Graphics g) {                          
        final Color old = g.getColor();
               
        g.setColor(Color.LIGHT_GRAY);  
                  
        
        if (iReserveSpaceRight)
            g.draw3DRect(getWidth() - 32, 0, getWidth(), getHeight(), true);       
        
        //g.fillRect(0, 0, getActiveBarWidth(), getHeight());
        g.drawImage(iBuf, LEFT_GAP, TOP_GAP, getActiveBarWidth(), getHeight() - (TOP_GAP + BOTTOM_GAP), null);    
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);    
        
        if (null != iMarker) 
            iMarker.draw(g, getBounds());        
        
        
        g.setColor(old);
    }
     
    private static final String KCOMMAND_TIMEWISE_SCALE="KCOMMAND_TIMEWISE_SCALE"; 
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu("FC_CONTEXT_MENU_TITLE"); //NOI18N    
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem("FC_MENU.TIMEWISE_SCALE");
        mi11.addActionListener(this);
        mi11.setState(iTimeWiseScale);
        mi11.setActionCommand(KCOMMAND_TIMEWISE_SCALE);        
        mnu.add(mi11);
        mnu.show(this, aX, aY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {        
        switch (e.getActionCommand()) {    
            case KCOMMAND_TIMEWISE_SCALE:
                setTimeWise(!iTimeWiseScale);
                makeBuffer();
                repaint();
                break;
            default: break;    
        }
    }
    
    
   private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();    
}
