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
package com.ivli.roim;


import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.FrameOffsetVector;
import com.ivli.roim.calc.BinaryOp;
import java.awt.geom.Path2D;

/**
 *
 * @author likhachev
 */
public class ROIManager implements ROIChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;    
    private static final boolean ROI_HAS_ANNOTATIONS  = true;
    private static final boolean CLONE_INHERIT_COLOUR = false;

    transient private final ImageView iView;     
    private HashSet<Overlay> iOverlays;          
    private EventListenerList iList;
    
    final class TUid {
        int iUid;
        public static final int UID_INVALID = -1;
        public TUid(int aStart) {iUid = aStart;}
        public String getNext() {            
            return String.format("ROI" + 
                                 "%03d", // NOI18N
                                 iUid++);
        }
    }
    
    private final TUid iUid = new TUid(0);
    
    ROIManager(ImageView aV) { 
        iView = aV;
        iOverlays = new HashSet(); 
        iList = new EventListenerList();
    }
    
    public int getWidth() {
        return iView.getModel().getWidth();
    }
    
    public int getHeight() {
        return iView.getModel().getHeight();
    }    
        
    public ImageView getView() {
        return iView;
    } 
            
    public IMultiframeImage getImage() {
        return iView.getModel();
    }
    
    FrameOffsetVector getOffsetVector() {
        if (iView instanceof OffsetImageView)
            return ((OffsetImageView)iView).iOff;
        return null;
    }
            
    public void clear() {
        iOverlays.clear();
        notifyROIChanged(null, ROIChangeEvent.CHG.Emptied, null);      
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

    public void createProfile(Shape aS) {               
        Rectangle r = iView.screenToVirtual().createTransformedShape(aS).getBounds();
        
        r.x = 0;
        r.width = getWidth();
        
        Profile newRoi = new Profile(r, this);     
        iOverlays.add(newRoi);   
        newRoi.addROIChangeListener(this);
    }
            
    public void createRuler(Path2D.Double aF) {
        //Rectangle2D s = new Rectangle2D.Double(aS.x, aS.y, aS.x + (aF.x - aS.x), Math.abs(aF.y-aS.y));        
        Shape r = iView.screenToVirtual().createTransformedShape(aF);     
        
        Ruler ruler = new Ruler(r, this);     
                
        iOverlays.add(ruler);   
        ruler.addROIChangeListener(this);
    }
    
    public void createAnnotation(BinaryOp anOp) {    
        iOverlays.add(new Annotation.Active(anOp, this));      
    }
    
    public void createAnnotation(ROI aROI) {    
        iOverlays.add(new Annotation.Static(aROI, this));      
    }
        
    protected void internalCreateROI(ROI aS) {
        ROI newRoi = new ROI(iUid.getNext(), aS.getShape(), this, aS.getColor());       
  
        iOverlays.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.CHG.Created, null);
    
    }
    
    public void createRoiFromShape(Shape aS) {                 
        final Shape shape = iView.screenToVirtual().createTransformedShape(aS);
        
        ROI newRoi = new ROI(iUid.getNext(), shape, this, null);       
  
        iOverlays.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.CHG.Created, null);
    }
    
    public ROI cloneRoi(ROI aR) {       
        ROI newRoi = new ROI(iUid.getNext(), aR.getShape(), this, CLONE_INHERIT_COLOUR ? aR.getColor() : null);
               
        iOverlays.add(newRoi); 
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);       
        
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.CHG.Created, aR); 
        return newRoi;
    }
    
    public void moveRoi(Overlay aO, double adX, double adY) {                         
        aO.move((adX/iView.getZoom().getScaleX()), (adY/iView.getZoom().getScaleY()));            
    }
    
    public Overlay findOverlay(Point aP) {      
        final Rectangle temp = iView.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();
                
        for (Overlay o : iOverlays) {           
            if (o.isSelectable() && o.intersects(temp)) 
                return o;                                   
        }
        return null;
    }
        
    boolean deleteRoi(ROI aR) {         
        final Iterator<Overlay> it = iOverlays.iterator();

        while (it.hasNext()) {  //clean annotations out - silly but workin'
            final Overlay o = it.next();
            if (o instanceof Annotation.Static && ((Annotation.Static)o).getRoi() == aR)               
                it.remove();
        } 
                
        notifyROIChanged(aR, ROIChangeEvent.CHG.Cleared, null);
        
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
           
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
         //out.writeObject(iList);
         out.writeObject(iOverlays);         
    }
    
    private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {                                            
        HashSet<Overlay> tmp = (HashSet<Overlay>)ois.readObject();  

        for (Overlay r : tmp) {               
            if (r instanceof ROI)
                internalCreateROI((ROI)r);
        }
    }
    
    void externalize(String aFileName) {        
        try(FileOutputStream fos = new FileOutputStream(aFileName)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            writeObject(oos);
            oos.close();
            fos.close();
        } catch (IOException ex){
           logger.error("Unable to externalize objects" + ex); 
        } 
    }
             
    void internalize(String aFileName) {
        try(FileInputStream fis = new FileInputStream(aFileName)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            readObject(ois);           
            ois.close();
            fis.close();            
        } catch (IOException|ClassNotFoundException ex) {
            logger.error("Unable to deserialize" + ex);
        }         
    }
    
    public void addROIChangeListener(ROIChangeListener aL) {
        ///iROIListeners.add(aL);
        iList.add(ROIChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {
        ///iROIListeners.add(aL);
        iList.remove(ROIChangeListener.class, aL);
    }
    
    void notifyROIChanged(Overlay aR, ROIChangeEvent.CHG aS, Object aEx) {
        final ROIChangeEvent evt = new ROIChangeEvent(this, aS, aR, aEx);

        ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);

        for (ROIChangeListener l : arr)
            l. ROIChanged(evt);
    }
        
    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {        
        notifyROIChanged(anEvt.getObject(), anEvt.getChange(), anEvt.getExtra());
    }
    
    private static final Logger logger = LogManager.getLogger(ROIManager.class);
}

