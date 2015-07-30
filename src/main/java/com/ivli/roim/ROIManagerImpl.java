/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author likhachev
 */
class ROIManagerImpl extends ROIManager {
    private final LinkedList<Overlay> iOverlays = new LinkedList();  
    
    MEDImageComponentBase iComponent;
    
    
    public void attach(MEDImageComponentBase aMed){iComponent = aMed;}
    
    public MEDImageBase getImage() {return iComponent.getImage();}
    public void clear() {iOverlays.clear();}
    
    public void update() {
        for(Overlay o:iOverlays)
            o.update();
    }
    
    public void draw(Graphics2D aGC, AffineTransform aT) {
         for(Overlay o:iOverlays)
            o.draw(aGC, aT);
    }            
            
    public void createRoiFromShape(Shape aS) { 
        final Shape r = iComponent.screenToVirtual().createTransformedShape(aS);
        final ROI temp = new ROI(r, this, null);       
        iOverlays.add(temp);
        iOverlays.add(new Annotation(temp));      
        temp.update();
    }
    
    public void cloneRoi(ROI aR) {
        final ROI temp = new ROI(aR);
        temp.iName = aR.iName + "(2)";
        iOverlays.add(temp); 
        iOverlays.add(new Annotation(temp));
    }
    
    public void moveRoi(Overlay aO, double adX, double adY) {   
        Rectangle r = aO.getShape().getBounds();
        AffineTransform trans = iComponent.virtualToScreen();
        trans.concatenate(AffineTransform.getTranslateInstance(adX, adY));    
        Shape s = trans.createTransformedShape(r);
        Rectangle o = iComponent.getBounds();
        Rectangle i = s.getBounds();
        
        if (true == o.contains(i)) {
            ///logger.info ("--> move object" + o + i);
            aO.move((adX/iComponent.getZoom().getScaleX()), (adY/iComponent.getZoom().getScaleY()));  
        }
    }
   
    public Overlay findOverlay(Point aP) {      
        final Rectangle temp = iComponent.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();
        
        for (Overlay r:iOverlays) {
            if (r.isSelectable() && r.getShape().intersects(temp)) 
                return r;                                   
        }
        return null;
    }
        
    public boolean deleteRoi(ROI aR) {      
        final ListIterator<Overlay> it = iOverlays.listIterator();

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
        ListIterator<Overlay> it = iOverlays.listIterator();
        while (it.hasNext()) {
            Overlay o = it.next();
            it.remove();    
        }
    }  
    
    public ListIterator<Overlay> getOverlaysList() {        
        return iOverlays.listIterator();
    }     
}

