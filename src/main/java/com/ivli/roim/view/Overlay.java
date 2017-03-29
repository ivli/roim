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


import com.ivli.roim.core.Uid;
import com.ivli.roim.events.OverlayChangeEvent;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;
import com.ivli.roim.events.OverlayChangeListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author likhachev
 */
public abstract class Overlay implements OverlayChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;   
    protected final transient Uid iUid;
    protected Shape   iShape;
    protected String  iName;
    protected boolean iSelected = false;
    protected boolean iPinned = false;
    protected boolean iShown = true;
    
    private final transient EventListenerList iListeners;        
            
    protected Overlay(Uid anID) {
        this(anID, null, null);
    }
    
    protected Overlay(Uid anID, Shape aShape, String aName) {
        iUid   = anID;
        iShape = aShape;         
        iName  = (null != aName) ? aName : "OVERLAY:" + anID.toString(); //NOI18N                  
        iListeners = new EventListenerList();   
    }
               
    public Uid getID() {
        return iUid;
    }    
    
    public void setName(String aName) {
        String old = getName();
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
    
    public final static int OVL_VISIBLE    = 0x1 << 1;
    public final static int OVL_MOVEABLE   = 0x1 << 2;
    public final static int OVL_SELECTABLE = 0x1 << 3;
    public final static int OVL_CLONEABLE  = 0x1 << 4;
    public final static int OVL_PINNABLE   = 0x1 << 5;
    public final static int OVL_PERMANENT  = 0x1 << 6;
    
    public final static int OVL_DEFAULT = OVL_VISIBLE|OVL_MOVEABLE|OVL_SELECTABLE|OVL_CLONEABLE|OVL_PINNABLE;
    
    ///protected final int iStyles;
    //public void setStyles(int aS) {iStyles = aS;}
    public int getStyles() {return OVL_DEFAULT;}
    
    ///public abstract boolean isPinnable();        
    ///public abstract boolean isSelectable();
    ///public abstract boolean isMovable();   
    ///public abstract boolean isPermanent(); 
    ///public abstract boolean isCloneable(); 
    ///public abstract boolean isVisible();  
    
   
    abstract void update(OverlayManager aM);          
    abstract void paint(AbstractPainter aP);  
    
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
           
    @Override
    public String toString() {
        return getClass().getName() + ":" + getName();
    }
    /**************************************/
    @FunctionalInterface
    interface ICanFlip {
        public void flip(boolean aVertical);
    } 
    
    @FunctionalInterface
    interface ICanRotate {
        public void rotate(double anAngle);       
    }
          
    @FunctionalInterface
    public interface IHaveConfigDlg {
        public boolean showDialog(Object aVoidStar);  
    } 
             
    public interface IHaveCustomMenu {   
        public ArrayList<JMenuItem> makeCustomMenu(Object aVoidStar);    
        public boolean handleCustomCommand(final String aCommand);
    }
    
   
}
