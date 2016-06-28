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


import com.ivli.roim.events.OverlayChangeEvent;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;
import com.ivli.roim.events.OverlayChangeListener;

/**
 *
 * @author likhachev
 */
public abstract class Overlay implements OverlayChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;
    
    //static final int VISIBLE = 0x1;
    public static final int SELECTABLE = 0x1;      
    public static final int MOVEABLE   = SELECTABLE << 0x1;
    public static final int PERMANENT  = MOVEABLE  << 0x1;
    public static final int CLONEABLE  = PERMANENT << 0x1;
    public static final int CANFLIP    = CLONEABLE << 0x1;
    public static final int CANROTATE  = CANFLIP   << 0x1;
    public static final int RESIZABLE  = CANROTATE << 0x1;
    public static final int PINNABLE   = RESIZABLE << 0x1;
    public static final int HASMENU    = PINNABLE  << 0x1;  
    public static final int HASCUSTOMMENU = HASMENU << 0x1;  
        
    //transient private final ROIManager iMgr; 
    
    protected final int iUid;
    protected Shape   iShape;
    protected String  iName;
    protected boolean iEmphasized = false;
    protected boolean iPinned = false;
    protected boolean iVisible = true;
    
    private final EventListenerList iListeners;        
            
    protected Overlay(int anID) {
        this(anID, null, null);
    }
    
    protected Overlay(int anID, String aName, Shape aShape) {
        iUid   = anID;
        iShape = aShape;         
        iName  = (null != aName)? aName : String.format("OVERLAY%d", anID); //NOI18N                  
        iListeners = new EventListenerList();   
    }
               
    public int getID() {
        return iUid;
    }
       
    public String getName() {
        return iName;
    }
    
    public void setName(String aName) {
        String old = getName();
        iName  = aName;
        notify(OverlayChangeEvent.CODE.NAME, old);         
    }
    
    public void setPinned(boolean aPin) {
        iPinned = isPinnable() && aPin;
    }
    
    public boolean isPinned() {
        return isPinnable() && iPinned;
    }
  
    public void setEmphasized(boolean aE) {
        iEmphasized = aE;
    }
    
    public boolean isEmphasized() {
        return iEmphasized;
    }
    
    public boolean isVisible() {
        return iVisible;
    }
    
    public void setVisible(boolean aV) {
        iVisible = aV;
    }
    
    boolean isSelectable() {return 0 != (getCaps() & SELECTABLE);}
    boolean isMovable() {return 0 != (getCaps() & MOVEABLE);}
    boolean isPermanent() {return 0 != (getCaps() & PERMANENT);}
    boolean isCloneable() {return 0 != (getCaps() & CLONEABLE);}
    boolean isPinnable() {return 0 != (getCaps() & PINNABLE);}
    boolean hasMenu() {return 0 != (getCaps() & HASMENU);}
    boolean canFlip() {return 0 != (getCaps() & CANFLIP);} 
    boolean canRotate() {return 0 != (getCaps() & CANROTATE);} 
    
    Shape getShape() {
        return iShape;
    }       
    
    boolean intersects(Rectangle2D aR) {
        return null != getShape() && getShape().intersects(aR);
    }   
        
    void move(double adX, double adY) {       
        iShape = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);                            
        notify(OverlayChangeEvent.CODE.MOVED, new double[]{adX, adY});
    } 
    
    abstract void update(OverlayManager aRM);  
        
    abstract int  getCaps();         
    abstract void paint(AbstractPainter aP);  
            
    interface IFlip {
        public void flip(boolean aVertical);
    } 
    
    interface IRotate {
        public void rotate(double anAngle);       
    }
    
    interface IIsoLevel {
        public void isolevel(int aTolerance);
    }
            
    public void addChangeListener(OverlayChangeListener aL) {       
        iListeners.add(OverlayChangeListener.class, aL);
    }
    
    public void removeChangeListener(OverlayChangeListener aL) {        
        iListeners.remove(OverlayChangeListener.class, aL);
    }
    
    protected void notify(OverlayChangeEvent.CODE aS, Object aEx) {        
        final OverlayChangeEvent evt = new OverlayChangeEvent(this, aS, this, aEx);

        OverlayChangeListener arr[] = iListeners.getListeners(OverlayChangeListener.class);

        for (OverlayChangeListener l : arr)
            l.OverlayChanged(evt);
    }
       
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {}
}
