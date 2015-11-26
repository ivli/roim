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
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import javax.swing.event.EventListenerList;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.events.EStateChanged;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.FrameOffsetVector;


/**
 *
 * @author likhachev
 */
public class ROIManager implements java.io.Serializable {  
    private static final long serialVersionUID = 42L;
    
    private static final boolean ROI_HAS_ANNOTATIONS = !true;
          
    transient private final ImageView iView; 
    
    private HashSet<Overlay> iOverlays;      
    
    private final EventListenerList iList;
    
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
        notifyROIChanged(null, EStateChanged.Emptied);      
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
    }
            
    public void createRoiFromShape(Shape aS) {         
        final Shape r = iView.screenToVirtual().createTransformedShape(aS);
        
        final ROI newRoi = new ROI(r, this, null);       
  
        iOverlays.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS)
            iOverlays.add(new Annotation(newRoi));      
       
        newRoi.update();
        notifyROIChanged(newRoi, EStateChanged.Created);
    }
    
    public void cloneRoi(ROI aR) {
        ROI newRoi = new ROI(aR);
        newRoi.iName = aR.iName + "(2)"; // NOI18N
        iOverlays.add(newRoi); 
        
        if (ROI_HAS_ANNOTATIONS)
            iOverlays.add(new Annotation(newRoi));
        
        notifyROIChanged(newRoi, EStateChanged.Created);
    }
    
    public void moveRoi(Overlay aO, double adX, double adY) {        
        //AffineTransform trans = iView.virtualToScreen();
        //trans.concatenate(AffineTransform.getTranslateInstance(adX, adY));    
               
        //if (iView.getBounds().contains(trans.createTransformedShape(aO.getShape().getBounds()).getBounds())) {           
            aO.move((adX/iView.getZoom().getScaleX()), (adY/iView.getZoom().getScaleY()));  
            
            if (aO instanceof ROI)
                notifyROIChanged((ROI)aO, EStateChanged.Changed);
       // }       
    }
    
    public Overlay findOverlay(Point aP) {      
        final Rectangle temp = iView.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();
                
        for (Overlay r : iOverlays) {
           // logger.info("--! evaluate overlay " + r.getShape().toString());
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
        
        notifyROIChanged(aR, EStateChanged.Cleared);
        
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
         out.writeObject(iOverlays);
    }
    
    private void readObject(java.io.ObjectInputStream ois) throws IOException, ClassNotFoundException {
        
            iOverlays = (HashSet<Overlay>)(ois.readObject());  
            notifyROIChanged(null, EStateChanged.Emptied);
            
            for (Overlay r : iOverlays) {
                if (r instanceof ROIBase) {
                    ((ROIBase)r).iMgr = this;
                    notifyROIChanged(((ROI)r), EStateChanged.Created);
                    /*
                    if (null != ((ROI)r).iAnnos)
                        for (Overlay o : ((ROI)r).iAnnos)   
                            iOverlays.add(o);
                            */
                }
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
    
    void notifyROIChanged(ROI aR, EStateChanged aS) {
       ROIChangeEvent evt = new ROIChangeEvent(this, aS, aR);
       
       ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);
       
       for (ROIChangeListener l : arr)
           l. ROIChanged(evt);
    }
        
    private static final Logger logger = LogManager.getLogger(ROIManager.class);
}

