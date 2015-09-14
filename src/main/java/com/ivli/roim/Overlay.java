/*
 * 
 */
package com.ivli.roim;

import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


/**
 *
 * @author likhachev
 */
public abstract class Overlay {    
    protected static final int SELECTABLE = 0x1;
    protected static final int MOVEABLE    = 0x2;
    protected static final int PERMANENT  = 0x4;
    protected static final int CLONEABLE  = 0x8;
    protected static final int CANFLIP    = 0x10;
    protected static final int CANROTATE  = 0x20;
    protected static final int RESIZABLE  = 0x40;
    protected static final int HASCUSTOMMNU  = 0x100;    
    
    protected Shape  iShape;
    protected String iName;
    protected int    iId;
    
    private static int sId = 0;
    
    protected Overlay(Shape aShape, String aName) {
        iShape = aShape; 
        if(null != aName) 
            iName = aName; 
        else
            iName = new String();
        patchName();
        
        iId = sId++;
    }
   
    public int getId() {
        return iId;
    }
    
    public String getName() {
        return iName;
    }
    
    public void setName(String aName) {
        iName = aName;
    }
    
    boolean isSelectable() {return 0 != (getCaps() & SELECTABLE);}
    boolean isMovable() {return 0 != (getCaps() & MOVEABLE);}
    boolean isPermanent() {return 0 != (getCaps() & PERMANENT);}
    boolean isCloneable() {return 0 != (getCaps() & CLONEABLE);}
    boolean canFlip() {return 0 != (getCaps() & CANFLIP);} 
    boolean canRotate() {return 0 != (getCaps() & CANROTATE);} 
    
    Shape getShape() {
        return iShape;
    }   
    
    private void patchName() { 
        if(iName.isEmpty()) {
            ///StringBuilder sb = new StringBuilder();
            iName = String.format("%d, %d", iShape.getBounds().x, iShape.getBounds().y); //NOI18N
        }
    }
    
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
}
