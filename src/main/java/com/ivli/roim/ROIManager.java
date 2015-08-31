
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author likhachev
 */
class ROIManager {    
    private final HashSet<Overlay> iOverlays;      
    private final ImageView        iView;        
    
    ROIManager(ImageView aV) {
        iView = aV;
        iOverlays = new HashSet();          
    }
        
    public IMultiframeImage getImage() {
        return iView.getImage();
    }
        
    public void clear() {
        iOverlays.clear();
    }
    
    public void update() {
        for(Overlay o : iOverlays)
            o.update();
    }
    
    public void draw(Graphics2D aGC, AffineTransform aT) {
         for(Overlay o : iOverlays)
            o.draw(aGC, aT);
    }            
            
    public void createRoiFromShape(Shape aS) { 
        final Shape r = iView.screenToVirtual().createTransformedShape(aS);
        final ROI newRoi = new ROI(r, this, null);       
  
        iOverlays.add(newRoi);
        iOverlays.add(new Annotation(newRoi));      
        newRoi.update();
        newRoi.makeCurve();        
    }
    
    public void cloneRoi(ROI aR) {
        final ROI temp = new ROI(aR);
        temp.iName = aR.iName + "(2)"; // NOI18N
        iOverlays.add(temp); 
        iOverlays.add(new Annotation(temp));
    }
    
    public void moveRoi(Overlay aO, double adX, double adY) {           
        AffineTransform trans = iView.virtualToScreen();
        trans.concatenate(AffineTransform.getTranslateInstance(adX, adY));    
               
        if (iView.getBounds().contains(trans.createTransformedShape(aO.getShape().getBounds()).getBounds()))            
            aO.move((adX/iView.getZoom().getScaleX()), (adY/iView.getZoom().getScaleY()));  
       
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
       
}

