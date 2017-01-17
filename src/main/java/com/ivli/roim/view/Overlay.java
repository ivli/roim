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
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;
import com.ivli.roim.events.OverlayChangeListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author likhachev
 */
public abstract class Overlay implements OverlayChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;
    /*
    //static final int VISIBLE = 0x1;
    public static final int SELECTABLE = 0x1;      
    public static final int MOVEABLE   = 0x1 << 0x1;
    public static final int PERMANENT  = MOVEABLE  << 0x1;
    public static final int CLONEABLE  = PERMANENT << 0x1;
   // public static final int CANFLIP    = CLONEABLE << 0x1;
   // public static final int CANROTATE  = CANFLIP   << 0x1;
    public static final int RESIZABLE  = CLONEABLE << 0x1;
    public static final int PINNABLE   = RESIZABLE << 0x1;
   // public static final int HASMENU    = PINNABLE  << 0x1;  
    ///public static final int HASCUSTOMMENU = HASMENU << 0x1;  
    public static final int FRAMESCOPE = PINNABLE << 0x1;
    */
    
    protected final transient Uid iUid;
    protected Shape   iShape;
    protected String  iName;
    protected boolean iEmphasized = false;
    protected boolean iPinned = false;
    protected boolean iVisible = true;
    
    private final transient EventListenerList iListeners;        
            
    protected Overlay(Uid anID) {
        this(anID, null, null);
    }
    
    protected Overlay(Uid anID, Shape aShape, String aName) {
        iUid   = anID;
        iShape = aShape;         
        iName  = (null != aName)? aName : String.format("OVERLAY%d", anID); //NOI18N                  
        iListeners = new EventListenerList();   
    }
               
    public Uid getID() {
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
    
    public abstract boolean isSelectable();//{return 0 != (getCaps() & SELECTABLE);}
    public abstract boolean isMovable();   // {return 0 != (getCaps() & MOVEABLE);}
    public abstract boolean isPermanent(); // {return 0 != (getCaps() & PERMANENT);}
    public abstract boolean isCloneable(); // {return 0 != (getCaps() & CLONEABLE);}
    public abstract boolean isPinnable();  // {return 0 != (getCaps() & PINNABLE);}
    abstract void update(OverlayManager aRM);          
    abstract void paint(AbstractPainter aP);  
    
    public Shape getShape() {
        return iShape;
    }       
    
    boolean intersects(Rectangle2D aR) {
        return null != getShape() && getShape().intersects(aR);
    }   
        
    void move(double adX, double adY) {       
        iShape = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);                            
        notify(OverlayChangeEvent.CODE.MOVED, new double[]{adX, adY});
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
    
        
    @FunctionalInterface
    interface ICanFlip {
        public void flip(boolean aVertical);
    } 
    
    @FunctionalInterface
    interface ICanRotate {
        public void rotate(double anAngle);       
    }
    
    @FunctionalInterface
    interface ICanIso {
        public boolean convertToIso(int aTolerance);
    }
    
    @FunctionalInterface
    public interface IHaveConfigDlg {
        public boolean showDialog(Object aVoidStar);  
    } 
             
    public interface IHaveCustomMenu {   
        public ArrayList<JMenuItem> makeCustomMenu(Object aVoidStar);    
        public boolean handleCustomCommand(final String aCommand);
    }
    
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {}
           
    public String toString() {
        return getClass().getName() + ":" + getName();
    }
}
