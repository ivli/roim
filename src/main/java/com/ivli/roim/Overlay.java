/*
 * 
 */
package com.ivli.roim;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import javax.swing.event.EventListenerList;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;

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
    static final int HASCUSTOMMENU = HASMENU << 0x1;  
        
    transient protected ROIManager iMgr; 
    
    protected Shape  iShape;
    protected String iName;
    protected boolean iSelected = false;
    protected boolean iPinned = false;
    
    private final EventListenerList iListeners;    
    
    protected Overlay(String aName, Shape aShape, ROIManager aMgr) {
        iMgr   = aMgr;
        iShape = aShape;         
        iName = (null != aName)? aName : new String(aName);                      
        iListeners = new EventListenerList();   
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
    
    public void setPinned(boolean aPin) {
        iPinned = aPin;
    }
    
    public boolean isPinned() {
        return iPinned;
    }
    
    
    
    public void select(boolean aSelected) {
        iSelected = aSelected;
    }
    
    public boolean isSelected() {
        return iSelected;
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
        return getShape().intersects(aR);
    }   
        
    void move(double adX, double adY) {
        if (!isPinned()) {          
            Shape temp = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);        
            Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getWidth(), getManager().getHeight());

            if (bounds.contains(temp.getBounds())) {            
                iShape = temp;                    
                update();
                notifyROIChanged(ROIChangeEvent.CHG.Moved, new double[]{adX, adY});
            }
        }
    } 
    
    abstract int  getCaps();     
    abstract void paint(Graphics2D aGC, AffineTransform aTrans);    
    abstract void update();  
    
    interface IFlip {
        public void flip(boolean aVertical);
    } 
    
    interface IRotate {
        public void rotate(double anAngle);       
    }
    
    interface IIsoLevel {
        public void isolevel(int aTolerance);
    }
            
    public void addROIChangeListener(ROIChangeListener aL) {       
        iListeners.add(ROIChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {        
        iListeners.remove(ROIChangeListener.class, aL);
    }
    
    protected void notifyROIChanged(ROIChangeEvent.CHG aS, Object aEx) {        
        ROIChangeEvent evt = new ROIChangeEvent(this, aS, this, aEx);

        ROIChangeListener arr[] = iListeners.getListeners(ROIChangeListener.class);

        for (ROIChangeListener l : arr)
            l. ROIChanged(evt);
    }
}
