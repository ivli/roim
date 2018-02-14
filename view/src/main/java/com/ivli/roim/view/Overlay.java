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


import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.JMenuItem;

import com.ivli.roim.events.*;


/**
 *
 * @author likhachev
 */
public abstract class Overlay implements OverlayChangeListener, Serializable {  
    private static final long serialVersionUID = 42L;   
    public final static int OVL_HIDDEN      = 0x0;
    public final static int OVL_VISIBLE     = 0x1 << 1;
    public final static int OVL_MOVEABLE    = 0x1 << 2;
    public final static int OVL_SELECTABLE  = 0x1 << 3;
    public final static int OVL_CLONEABLE   = 0x1 << 4;
    public final static int OVL_PINNABLE    = 0x1 << 5;
    public final static int OVL_PERMANENT   = 0x1 << 6;   
    public final static int OVL_HAVE_MENU   = 0x1 << 10;
    public final static int OVL_HAVE_CONFIG = 0x1 << 11;
    public final static int OVL_CAN_FLIP    = 0x1 << 12;
    public final static int OVL_CAN_ROTATE  = 0x1 << 13;
          
    protected Shape   iShape;
    protected String  iName;
    protected boolean iSelected = false;
    protected boolean iPinned = false;
    protected boolean iShown = true;    
    private final transient EventListenerList iListeners;        
     
    protected Overlay(Shape aShape, String aName) { 
        if (null == aName)
            throw(new IllegalArgumentException("Name cannot be null"));
        
        iShape = aShape;         
        iName  = (null != aName) ? aName : this.toString();                   
        iListeners = new EventListenerList();   
    }
       
    public void setName(String aName) {
        if (null == aName)
            throw(new IllegalArgumentException("Name cannot be null"));
        
        String old = iName;
        iName  = aName;
        notify(OverlayChangeEvent.CODE.NAME_CHANGED, old);         
    }
    
    public String getName() {
        return iName;
    }
    
    public void pin(boolean aPin) {
        if (0 != (getStyles() & OVL_PINNABLE))           
            notify(OverlayChangeEvent.CODE.PINNED, iPinned = aPin);        
    }
    
    public boolean isPinned() {
        return iPinned;
    }
  
    public void select(boolean aE) {
        if (0 != (getStyles() & OVL_SELECTABLE)) 
            notify(OverlayChangeEvent.CODE.SELECTED, iSelected = aE);
    }
    
    public boolean isSelected() {
        return iSelected;
    }
    
    public boolean isShown() {
        return iShown;
    }
    
    public void show(boolean aV) {
        iShown = aV;
    }
    
    public abstract int getStyles();// {return OVL_DEFAULT;}
    public abstract void update(OverlayManager aM);          
    public abstract void paint(IPainter aP);  
    
    public Shape getShape() {
        return iShape;
    } 
   
    public void setShape(Shape aS) {
        Shape old = iShape; 
        iShape = aS;
        notify(OverlayChangeEvent.CODE.RESHAPED, old);
    } 
       
    public boolean contains(Point2D aP) {
        return getShape().contains(aP);
    }
       
    protected void translate(double adX, double adY) {       
        iShape = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);                                    
    } 
    
    public void move(double adX, double adY) {       
        translate(adX, adY);
        notify(OverlayChangeEvent.CODE.MOVED, new double[]{adX, adY});
    } 
 
    public void addChangeListener(OverlayChangeListener aL) {       
        iListeners.add(OverlayChangeListener.class, aL);
    }
        
    public void removeChangeListener(OverlayChangeListener aL) {        
        iListeners.remove(OverlayChangeListener.class, aL);
    }
    
    protected void notify(OverlayChangeEvent.CODE aS, Object aEx) {        
        final OverlayChangeEvent evt = new OverlayChangeEvent(this, aS, aEx);

        OverlayChangeListener arr[] = iListeners.getListeners(OverlayChangeListener.class);

        for (OverlayChangeListener l : arr)
            l.OverlayChanged(evt);
    }
       
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {}
   
    
    /**************************************/
    private static final String CMD_SHOW_CONFIG  = "COMMAND_OVL_COMMAND_SHOW_CONFIG"; // NOI18N
    private static final String CMD_FLIP_HORZ  = "COMMAND_ROI_OPERATIONS_FLIP_V"; // NOI18N
    private static final String CMD_FLIP_VERT  = "COMMAND_ROI_OPERATIONS_FLIP_H"; // NOI18N
    private static final String CMD_ROT_90CW   = "COMMAND_ROI_OPERATIONS_ROTATE_90_CW"; // NOI18N
    private static final String CMD_ROT_90CCW  = "COMMAND_ROI_OPERATIONS_ROTATE_90_CCW"; // NOI18N
       
    /*
     * if OVL_CAN_FLIP is set this method will be called to flip an object
     * \param aVertical - true to flip vertical, false to flip horizontal
     * \return nothing
     */
    
    public void flip(boolean aV) {                        
        AffineTransform tx;
        
        if (aV) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -getShape().getBounds().getHeight());
        } else {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-getShape().getBounds().getWidth(), 0);       
        }        
        
        iShape = tx.createTransformedShape(iShape);                
    }
  
    /*
     * if OVL_CAN_ROTATE is set this method will be called to rotate an object
     * \param anAngle - rotation angle in degrees  
     * \return nothing
     */     
    public void rotate(double aV) {        
        final Rectangle rect = getShape().getBounds();
        AffineTransform tx = new AffineTransform();        
        tx.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);              
        iShape = tx.createTransformedShape(iShape);
    }  
    
    /*
     * if OVL_HAVE_CONFIG is set this method will be called to edit configuration of a particular instance
     * return true if anything changed otherwise false
     */
    public boolean showConfigDialog(Object aVoidStar) {
        return false;
    }      
                 
    /*
     * if OVL_HAVE_MENU is set in styles this mehod must return non null ArrayList of 0 or more commands
     * \param anActionListener - 
     * \return 
     */   
    public ArrayList<JMenuItem> makeCustomMenu(Object anActionListener) {
        ArrayList<JMenuItem> mnu = new ArrayList<>();
        if (0 != (getStyles() & OVL_CAN_FLIP)) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_HORZ"));           
            mi.setActionCommand(CMD_FLIP_VERT);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_VERT"));           
            mi.setActionCommand(CMD_FLIP_HORZ);
            mnu.add(mi);         
        }
        
        if (0 != (getStyles() & OVL_CAN_FLIP)) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CW"));            
            mi.setActionCommand(CMD_ROT_90CW);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CCW"));            
            mi.setActionCommand(CMD_ROT_90CCW);
            mnu.add(mi);
        }
       
        if (0 != (getStyles() & OVL_HAVE_CONFIG)) {
           JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CUST_MNU.ASTATIC.SETUP"));        
            mi.setActionCommand(CMD_SHOW_CONFIG);
            mnu.add(mi);
        }
        return mnu;
    }

    public boolean handleCustomCommand(String aCommand) {
        switch(aCommand) { 
            case CMD_SHOW_CONFIG:
                showConfigDialog(this);
                break;
            case CMD_FLIP_VERT:
                flip(false);                
                break;                
            case CMD_FLIP_HORZ:
                flip(true);                
                break;                
            case CMD_ROT_90CW:
                rotate(90);               
                break;                
            case CMD_ROT_90CCW:
                rotate(-90);               
                break; 
            default:
                return false;
        }
        return true;
    }
       
}
