/*
 * 
 */
package com.ivli.roim;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;


/**
 *
 * @author likhachev
 */
public abstract class Overlay implements java.io.Serializable {  
    private static final long serialVersionUID = 42L;
    
    static final int SELECTABLE = 0x1;
    static final int MOVEABLE   = SELECTABLE << 0x1;
    static final int PERMANENT  = MOVEABLE  << 0x1;
    static final int CLONEABLE  = PERMANENT << 0x1;
    static final int CANFLIP    = CLONEABLE << 0x1;
    static final int CANROTATE  = CANFLIP   << 0x1;
    static final int RESIZABLE  = CANROTATE << 0x1;
    static final int PINNABLE   = RESIZABLE << 0x1;
    static final int HASMENU    = PINNABLE  << 0x1;   
    
    
    transient protected final ROIManager iMgr; 
    
    protected Shape  iShape;
    protected String iName;
    protected boolean iPinned = false;
    
    private EventListenerList iList;    
    
    protected Overlay(String aName, Shape aShape, ROIManager aMgr) {
        iMgr   = aMgr;
        iShape = aShape; 
        
        if(null != aName) 
            iName = aName; 
        else
            iName = new String();
              
        iList = new EventListenerList();   
    }
    
    public final ROIManager getManager() {
        return iMgr;
    }    
    
    public boolean canMove(double adX, double adY) {           
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getWidth(), getManager().getHeight());
        
        return bounds.contains(AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(getShape()).getBounds());
    }
    
    public String getName() {
        return iName;
    }
    
    public void setName(String aName) {
        iName = aName;
    }
    
    public void pin(boolean aPin) {
        iPinned = aPin;
    }
    
    public boolean isPinned() {
        return iPinned;
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
  /*
    public final void move(double adX, double adY) {
        if (!isPinned())
            doMove(adX, adY);
    }
  */  
    abstract int  getCaps(); 
    
    abstract void paint(Graphics2D aGC, AffineTransform aTrans); 
   
    abstract void update();    
    
    
    abstract void move(double adX, double adY);     
    
    interface IFlip {
        public void flip(boolean aVertical);
    } 
    
    interface IRotate {
        public void rotate(int anAngle);
    }
    
    interface IIsoLevel {
        public void isolevel(int aTolerance);
    }
    
    
     ///todo: following it might make sense to keep the list of observers here
    public void addROIChangeListener(ROIChangeListener aL) {
        //iMgr.addROIChangeListener(aL);
        iList.add(ROIChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {
        //iMgr.removeROIChangeListener(aL);
        iList.remove(ROIChangeListener.class, aL);
    }
    
    protected void notifyROIChanged(ROIChangeEvent.CHG aS, Object aEx) {
        //iMgr.notifyROIChanged((ROI)this, aS, aEx);
        ROIChangeEvent evt = new ROIChangeEvent(this, aS, (ROI)this, aEx);

        ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);

        for (ROIChangeListener l : arr)
            l. ROIChanged(evt);
    }
}
