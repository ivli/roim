
package com.ivli.roim;

//import com.ivli.roim.events.EStateChanged;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;
/**
 *
 * 
 */
public abstract class ROIBase extends Overlay {
    transient ROIManager iMgr; 
     
    private EventListenerList iList;
    
    ROIBase(java.awt.Shape aS, ROIManager aM, String aN) {
        super(aS, aN);
        iMgr = aM;
        iList = new EventListenerList();
    }
    
    @Override
    int getCaps() {return SELECTABLE|MOVEABLE|HASMENU;}
    
    public ROIManager getManager() {
        return iMgr;
    }    
    
    public boolean canMove(double adX, double adY) {           
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getWidth(), getManager().getHeight());
        
        return bounds.contains(AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(getShape()).getBounds());
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
