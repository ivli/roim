
package com.ivli.roim;


import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.Events.EStateChanged;
/**
 *
 * @author likhachev
 */
public class ROIManager implements java.io.Serializable {  
    private static final long serialVersionUID = 42L;
    
    private static final boolean ROI_HAS_ANNOTATIONS = false;
    
    transient private final ImageView  iView; 
    
    private HashSet<Overlay> iOverlays;      
           
    
    ROIManager(ImageView aV) { 
        iView = aV;
        iOverlays = new HashSet();          
    }
        
    public IMultiframeImage getImage() {
        return iView.getImage();
    }
        
    public void clear() {
        iOverlays.clear();
        iView.notifyROIChanged(null, EStateChanged.Emptied);
    }
    
    public void update() {
        iOverlays.stream().forEach((o) -> {
            o.update();
        });
    }
    
    public void paint(Graphics2D aGC, AffineTransform aT) {
        iOverlays.stream().forEach((o) -> {
            o.paint(aGC, aT);
        });
    }            
            
    public void createRoiFromShape(Shape aS) { 
        final Shape r = iView.screenToVirtual().createTransformedShape(aS);
        final ROI newRoi = new ROI(r, this, null);       
  
        iOverlays.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS)
            iOverlays.add(new Annotation(newRoi));      
       
        newRoi.update();
        iView.notifyROIChanged(newRoi, EStateChanged.Created);
    }
    
    public void cloneRoi(ROI aR) {
        final ROI newRoi = new ROI(aR);
        newRoi.iName = aR.iName + "(2)"; // NOI18N
        iOverlays.add(newRoi); 
        iOverlays.add(new Annotation(newRoi));
        iView.notifyROIChanged(newRoi, EStateChanged.Created);
    }
    
    public void moveRoi(Overlay aO, double adX, double adY) {           
        AffineTransform trans = iView.virtualToScreen();
        trans.concatenate(AffineTransform.getTranslateInstance(adX, adY));    
               
        if (iView.getBounds().contains(trans.createTransformedShape(aO.getShape().getBounds()).getBounds())) {           
            aO.move((adX/iView.getZoom().getScaleX()), (adY/iView.getZoom().getScaleY()));  
            if (aO instanceof ROI)
                iView.notifyROIChanged((ROI)aO, EStateChanged.Changed);
        }       
    }
    
    public Overlay findOverlay(Point aP) {      
        final Rectangle temp = iView.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();
        
        for (Overlay r : iOverlays) {
            if (r.isSelectable() && r.getShape().intersects(temp)) 
                return r;                                   
        }
        return null;
    }
        
    boolean deleteRoi(ROI aR) {      
        final Iterator<Overlay> it = iOverlays.iterator();

        while (it.hasNext()) {  //clean annotations out - silly but workin'
            final Overlay o = it.next();
            if (o instanceof Annotation && aR.remove(o))               
                it.remove();
        } 
        
        iView.notifyROIChanged(aR, EStateChanged.Cleared);
        
        return iOverlays.remove(aR);   
    }  
    
    public boolean deleteOverlay(Overlay aO) {  
        if (aO instanceof ROI)
            return deleteRoi((ROI)aO);
        else
            return iOverlays.remove(aO);   
    }
    
    void deleteAllOverlays() {      
        iOverlays.clear();
    }  
    
    public Iterator<Overlay> getOverlaysList() {        
        return iOverlays.iterator();// listIterator();
    }     
       
    void externalize(String aFileName) {        
        try(java.io.FileOutputStream fos = new java.io.FileOutputStream(aFileName)) {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fos);
            oos.writeObject(iOverlays);
            oos.close();
        } catch (IOException ex){
           logger.error("Unable to externalize objects" + ex); 
        } 
    }
     
    void internalize(String aFileName) {
        try(java.io.FileInputStream fis = new java.io.FileInputStream(aFileName)) {
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
            iOverlays = (HashSet<Overlay>)(ois.readObject());            
            ois.close();
             
            iView.notifyROIChanged(null, EStateChanged.Emptied);
            
            for (Overlay r : iOverlays) {
                if (r instanceof ROI) {
                    ((ROI)r).iMgr = this;
                    iView.notifyROIChanged(((ROI)r), EStateChanged.Created);
                    
                    if (null != ((ROI)r).iAnnos)
                        for (Overlay o : ((ROI)r).iAnnos)   
                            iOverlays.add(o);
                }
            }
        } catch (IOException|ClassNotFoundException ex) {
            logger.error("Unable to deserialize" + ex);
        } 
    }
    
    private static final Logger logger = LogManager.getLogger(ROIManager.class);
}

