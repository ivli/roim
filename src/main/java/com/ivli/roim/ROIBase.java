
package com.ivli.roim;

import com.ivli.roim.events.EStateChanged;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
/**
 *
 * 
 */
public abstract class ROIBase extends Overlay {
    transient ROIManager iMgr; 
     
    ROIBase(java.awt.Shape aS, ROIManager aM, String aN) {
        super(aS, aN);
        iMgr = aM;
    }
    
    int getCaps() {return SELECTABLE|MOVEABLE|HASMENU;}
    
    public ROIManager getManager() {
        return iMgr;
    }    
    
    public boolean canMove(double adX, double adY) {           
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getWidth(), getManager().getHeight());
        
        return bounds.contains(AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(getShape()).getBounds());
    }
}
