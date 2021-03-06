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
import java.awt.Point;
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
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

/**
 *
 * @author likhachev
 */
public class FrameControl extends JComponent implements FrameChangeListener, ActionListener, 
                                                        MouseMotionListener, MouseListener, MouseWheelListener {
    
    private static final int MARKER_CURSOR = Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = Cursor.N_RESIZE_CURSOR;                              
    private static final int DEFAULT_HORIZONTAL_GAP = 2;  
    private static final int DEFAULT_VERTICAL_GAP = 16;

    
    private final int iLeftGap;// = DEFAULT_VERTICAL_GAP;  //reserve border at left & right
    private final int iRightGap;// = DEFAULT_VERTICAL_GAP; 
    private final int iTopGap = DEFAULT_HORIZONTAL_GAP;  //reserve a half of marker height at window's top & bottom 
    private final int iBottomGap = DEFAULT_HORIZONTAL_GAP;
    
    private final Marker  iMarker;    
    private ActionItem    iAction;
    private BufferedImage iBuf;
          
    private ImageView iView;       
    private JToolTip iTip;
      
    private static final boolean DRAW_FRAME_MINIATURES = true;
       
    private boolean iAnnotateMarker = true;    
    private boolean iDisplayImageUnits = false;
    private boolean iTimeSincePhase = false;
    private boolean iDrawPhases = true;
        
    private double iConv = 1.; // holds a value used to convert pixels to image units (time or space) or frame number and vice versa
    
    boolean iImageUnitsTime = true;
    
    FrameControl() {        
        iMarker = new Marker("images/knob_cone_vert.png", Marker.Orientation.HORIZONTAL);    //NOI18N    
        iLeftGap  = iMarker.getMarkerSize()/2;
        iRightGap = iMarker.getMarkerSize()/2; //to the case images of different height are used 
        
        if(iAnnotateMarker) {
            iTip = new JToolTip();
            iTip.setComponent(this);            
        }
    }
    
    public static FrameControl create(ImageView aV) {
        FrameControl ret = new FrameControl();
        ret.construct(aV);
        aV.addFrameChangeListener(ret);       
        return ret;
    }
               
    private void setImageUnits(boolean aTimeWise) {
        iDisplayImageUnits = aTimeWise;
                
        if (!iDisplayImageUnits)
            iConv = (double)iView.getImage().getNumFrames() / (double)getActiveBarWidth();  
        else if(iImageUnitsTime) {                   
                TimeSliceVector tsv = iView.getImage().getTimeSliceVector(); 
                iConv = (double)tsv.duration() / (double)getActiveBarWidth();
        } else {
        
        } 
   
        updateMarker(iView.getFrameNumber());                      
    }
    
    private void construct(ImageView aW) {    
        iView = aW;    
        
        /*
        switch(aW.getImage().getImageType()) {
            case DYNAMIC: //TODO: check logic about gated and the rest of image types
                iImageUnitsTime = true; break;
            default:
                iImageUnitsTime = false;
        }
        */
        
        //aW.getImage().getTimeSliceVector()
        
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {   
                iBuf = null; 
                setImageUnits(iDisplayImageUnits);                
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
    
    boolean iActive = false;
   
    private void updateMarkerAnnotation() {
        if (null != popup) {
            popup.hide();
            popup = null;
        }
        
        if (iAnnotateMarker && iActive) {            
            PopupFactory popupFactory = PopupFactory.getSharedInstance();           
            Point pt = new Point(getLocationOnScreen()); 
            //SwingUtilities.convertPointToScreen(pt, this);
            int x = pt.x + iMarker.getPosition();// .e.getXOnScreen();
            int y = pt.y - 8;//e.getYOnScreen();
            popup = popupFactory.getPopup(this, iTip, x, y);
            popup.show();
        }
    }
    
    protected void updateMarker(int aFrameNumber) {        
        if (iDisplayImageUnits) {
            if (iImageUnitsTime) {
                long moment = iView.getImage().getTimeSliceVector().frameStarts(aFrameNumber);
                iMarker.setPosition((int)(moment / iConv));
                if (iAnnotateMarker) {
                   Instant t =  new Instant(!iTimeSincePhase ? moment: iView.getImage().getTimeSliceVector().sincePhase(moment)); 
                   iTip.setTipText(t.format());
                }
            } else { //TODO: recalculate in  
                iMarker.setPosition((int)(aFrameNumber / iConv));
                if (iAnnotateMarker)
                   iTip.setTipText(String.format("%d", aFrameNumber)); //NOI18N
            }            
        } else {
                iMarker.setPosition((int)(aFrameNumber / iConv));
                if (iAnnotateMarker)
                   iTip.setTipText(String.format("%d", aFrameNumber)); //NOI18N
        }
        
        updateMarkerAnnotation();        
    } 
    
    @Override
    public void frameChanged(FrameChangeEvent anE) {       
        updateMarker(anE.getFrame());
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
            if (null != iView)
                iView.setFrameNumber(iView.getFrameNumber() + e.getWheelRotation());
            else
                iMarker.setPosition(iMarker.getPosition() + e.getWheelRotation());
        }            
    } 
    
    Popup popup = null;
    @Override
    public void mouseMoved(MouseEvent e) {
        final int xpos = e.getPoint().x;

        if (iMarker.contains(xpos))
            setCursor(java.awt.Cursor.getPredefinedCursor(MARKER_CURSOR));        
        else 
            setCursor(java.awt.Cursor.getDefaultCursor());       
 
    }
    
    final void setFrameByPosition(int aPos) {
        LOG.debug("set frame position = " + aPos );
        boolean repaint = false;
        if (null == iView)
            iMarker.setPosition(aPos);
        else {
            if (!iDisplayImageUnits)
                iView.setFrameNumber((int)(iConv*(double)aPos)); 
            else {
                if (iImageUnitsTime) 
                    iView.setFrameNumber(iView.getImage().getTimeSliceVector().frameNumber((long)(iConv*(double)aPos)));                    
                else
                    iView.setFrameNumber((int)(iConv*(double)aPos)); //TODO: 
            }
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
                        if (aX >= iLeftGap && aX < getActiveBarWidth() + iRightGap) {
                            setFrameByPosition(aX - iLeftGap);
                            //repaint();
                        }
                    }   

                    protected boolean DoWheel(int aX) {   
                        ////setFrameByPosition(aX);
                        return false;
                    }
                };    
            //don't hit but still within active rectangle - just move marker            
            } else if (xpos >= iLeftGap && xpos < getActiveBarWidth() + iRightGap) {
                setFrameByPosition(xpos - iLeftGap);
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
        iActive = true;
        updateMarkerAnnotation();
    }

    @Override
    public void mouseExited(MouseEvent e) {        
        iActive = false;
        if (null != popup){
            popup.hide();
            popup = null;
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
        
        if (iDrawPhases && null != iView && iView.getImage().getTimeSliceVector().getNumPhases() > 1) {
            final TimeSliceVector tsv = iView.getImage().getTimeSliceVector();
            int xstart = 0;
            int width = 0;
            for(int i = 0; i < tsv.getNumPhases(); ++i) {
                width = iDisplayImageUnits ? (int) ((double)tsv.getPhaseDuration(i) / iConv) - 1 :
                                         (int) ((double)tsv.getPhaseFrames(i) / iConv) - 1;
                g.setColor(Colorer.getColor(i));
                g.drawRect(xstart, 0, width, getHeight() - iTopGap);
                xstart += width + 1;
            }           
        } 
    }
    
    int getActiveBarWidth() {
        return getWidth() - (iReserveSpaceRight ? 2*(iRightGap + iLeftGap) : (iRightGap + iLeftGap));
    }
    
    private final boolean iReserveSpaceRight = true; 
    
    @Override
    public void paintComponent(Graphics g) {                          
        final Color old = g.getColor();
               
        g.setColor(Color.LIGHT_GRAY);                   
        
        if (iReserveSpaceRight)
            g.draw3DRect(getWidth() - 32, 0, getWidth(), getHeight(), true);       
        
        //g.fillRect(0, 0, getActiveBarWidth(), getHeight());
        g.drawImage(iBuf, iLeftGap, iTopGap, getActiveBarWidth(), getHeight() - (iTopGap + iBottomGap), null);    
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);    
        
        if (null != iMarker) 
            iMarker.draw(g, getBounds());        
        
        
        g.setColor(old);
    }
     
    private static final String KCOMMAND_TIMEWISE_SCALE = "KCOMMAND_TIMEWISE_SCALE";  //NOI18N
    private static final String KCOMMAND_ANNOTATE_MARKER = "KCOMMAND_ANNOTATE_MARKER"; //NOI18N 
    private static final String KCOMMAND_SINCE_PHASE = "KCOMMAND_SINCE_PHASE"; //NOI18N 
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu("FC_CONTEXT_MENU_TITLE"); //NOI18N  
        
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FC_MENU.KCOMMAND_TIMEWISE_SCALE"));
        mi11.addActionListener(this);
        mi11.setState(iDisplayImageUnits);
        mi11.setActionCommand(KCOMMAND_TIMEWISE_SCALE);        
        mnu.add(mi11);
        
        JCheckBoxMenuItem mi12 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FC_MENU.KCOMMAND_ANNOTATE_MARKER"));
        mi12.addActionListener(this);
        mi12.setState(iAnnotateMarker);
        mi12.setActionCommand(KCOMMAND_ANNOTATE_MARKER);        
        mnu.add(mi12);
        
        JCheckBoxMenuItem mi13 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FC_MENU.KCOMMAND_SINCE_PHASE"));
        mi13.addActionListener(this);
        mi13.setState(iTimeSincePhase);
        mi13.setActionCommand(KCOMMAND_SINCE_PHASE);        
        mnu.add(mi13);
        
        mnu.show(this, aX, aY);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {        
        switch (e.getActionCommand()) {    
            case KCOMMAND_TIMEWISE_SCALE:
                setImageUnits(!iDisplayImageUnits);
                updateMarker(iView.getFrameNumber());
                makeBuffer();
                repaint();
                break;
            case KCOMMAND_ANNOTATE_MARKER:
                iAnnotateMarker = !iAnnotateMarker;
                updateMarkerAnnotation();               
                break;
             case KCOMMAND_SINCE_PHASE:
                iTimeSincePhase = !iTimeSincePhase;
                updateMarkerAnnotation();               
                break;                    
            default: break;    
        }
    }
        
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();    
}
