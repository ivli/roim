/*
 * Copyright (C) 2016 likhachev
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
package com.ivli.roim.view;


import com.ivli.roim.core.IImageView;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.OverlayChangeListener;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.event.EventListenerList;


/**
 *
 * @author likhachev
 */
public class OverlayManager implements OverlayChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;    
    
   /// transient protected IImageView iView; 
    private final IMultiframeImage iImage;
    private final HashSet<Overlay> iOverlays;        
    private final EventListenerList iList;
        
    protected final OverlayManager iParent;
            
    public OverlayManager(IMultiframeImage anImage, OverlayManager aP) {
        iImage = anImage;
        iParent = aP;        
        iOverlays = new HashSet();        
        iList = new EventListenerList();
    }
   
    public IMultiframeImage getImage() {
        return iImage;
    }
          
    public void clear() {
        iOverlays.clear();
        notifyROIChanged(null, ROIChangeEvent.CODE.ALLDELETED, null);      
    }
    
    public void update() {
        iOverlays.stream().forEach((o) -> {
            o.update(this);
        });
    }
    
    public void paint(AbstractPainter aP) {    
        if (null != iParent)
            iParent.paint(aP);
        
        iOverlays.stream().forEach((o) -> {
            if(o.isVisible()) 
                o.paint(aP);
        });
    }    
       
    protected void addObject(Overlay aO) {
        iOverlays.add(aO);
        aO.update(this);
        notifyROIChanged(aO, ROIChangeEvent.CODE.CREATED, this);  
    }
    
    public void moveObject(Overlay aO, double adX, double adY) {                         
        if (!aO.isPinned()) {          
            Shape temp = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(aO.getShape());        
            Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, iImage.getWidth(), iImage.getHeight());

            if (bounds.contains(temp.getBounds())) {            
                aO.move(adX, adY);   
                aO.update(this);
                notifyROIChanged(aO, ROIChangeEvent.CODE.MOVED, new double[]{adX, adY});
                
            }
        }
    }
    
    public Overlay findObject(Point aP, IImageView aV) {      
        final Rectangle temp = aV.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();                 
        
        for (Overlay o : iOverlays) {           
            if (o.isSelectable() && o.intersects(temp)) 
                return o;                                   
        }
                
        return null;
    }
               
    public boolean deleteObject(Overlay aO) {
        notifyROIChanged(aO, ROIChangeEvent.CODE.DELETED, this);        
        return iOverlays.remove(aO);   
    }
        
    public Iterator<Overlay> getObjects() {        
        return iOverlays.iterator();
    }     
               
    public void addROIChangeListener(ROIChangeListener aL) {        
        iList.add(ROIChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {        
        iList.remove(ROIChangeListener.class, aL);
    }
    
    void notifyROIChanged(Overlay aR, ROIChangeEvent.CODE aS, Object aEx) {
        final ROIChangeEvent evt = new ROIChangeEvent(this, aS, aR, aEx);

        ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);

        for (ROIChangeListener l : arr)
            l. ROIChanged(evt);
    }
        
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {  
        
        switch (anEvt.getCode()) {
            case NAME:
                notifyROIChanged(anEvt.getObject(), ROIChangeEvent.CODE.CHANGEDNAME, anEvt.getExtra());
                break;
            case COLOR:
                notifyROIChanged(anEvt.getObject(), ROIChangeEvent.CODE.CHANGEDCOLOR, anEvt.getExtra());
                break;
            default:
                break;
        }
        
        //notifyROIChanged(anEvt.getObject(), anEvt.getCode(), anEvt.getExtra());
    }
        
}

