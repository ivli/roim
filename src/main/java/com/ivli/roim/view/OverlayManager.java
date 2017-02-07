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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.event.EventListenerList;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.OverlayChangeListener;
import java.awt.geom.Point2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class OverlayManager implements OverlayChangeListener, FrameChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;    
      
    protected final IMultiframeImage  iImage;
    protected final HashSet<Overlay>  iOverlays;        
    protected final EventListenerList iList;
  
    public OverlayManager(IMultiframeImage anImage) {
        iImage = anImage;   
        iOverlays = new HashSet();        
        iList = new EventListenerList();
    }
   
    public IMultiframeImage getImage() {
        return iImage;
    }
          
    public void clear() {
        iOverlays.stream().forEach((o) -> notifyROIChanged(o,OverlayChangeEvent.CODE.DELETED, null));        
        iOverlays.clear();              
    }
    
    public void update() {        
        iOverlays.stream().forEach((o) -> o.update(this) );
    }
    
    public void paint(AbstractPainter aP) {          
        iOverlays.stream().forEach((o) -> {
            if(o.isShown()) 
                o.paint(aP);
        });
    }    
       
    protected void addObject(Overlay aO) {
        iOverlays.add(aO);
        aO.update(this);
        aO.addChangeListener(this);
        notifyROIChanged(aO, OverlayChangeEvent.CODE.CREATED, this);  
    }
    
    public void moveObject(Overlay aO, double adX, double adY) {          
        if (!aO.isPinned()) {                  
            Shape temp = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(aO.getShape());        
            Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, iImage.getWidth(), iImage.getHeight());

            if (bounds.contains(temp.getBounds())) {            
                aO.move(adX, adY);   
                aO.update(this);
                notifyROIChanged(aO, OverlayChangeEvent.CODE.MOVED, new double[]{adX, adY});                
            }
        }
    }
    
    public Overlay findObject(Point2D aP) {          
        for (Overlay o : iOverlays) {           
            if (o.isSelectable() && o.contains(aP)) 
                return o;                                   
        }
        
        return null;
    }
               
    public boolean deleteObject(Overlay aO) {
        if (iOverlays.remove(aO)) {
            notifyROIChanged(aO, OverlayChangeEvent.CODE.DELETED, null);        
            return true;
        }
        return false;
    }
        
    public Iterator<Overlay> getObjects() {        
        return iOverlays.iterator();
    }     
               
    public void addROIChangeListener(OverlayChangeListener aL) {    
        iList.add(OverlayChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(OverlayChangeListener aL) {          
        iList.remove(OverlayChangeListener.class, aL);
    }
    
    void notifyROIChanged(Overlay aO, OverlayChangeEvent.CODE aS, Object aEx) {
        final OverlayChangeEvent evt = new OverlayChangeEvent(this, aS, aO, aEx);
        LOG.debug("<--" + evt);
        OverlayChangeListener arr[] = iList.getListeners(OverlayChangeListener.class);

        for (OverlayChangeListener l : arr)
            l.OverlayChanged(evt);
    }
        
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {         
        switch (anEvt.getCode()) {
            case NAME_CHANGED:
                notifyROIChanged(anEvt.getObject(), OverlayChangeEvent.CODE.NAME_CHANGED, anEvt.getExtra());
                break;
            case COLOR_CHANGED:
                notifyROIChanged(anEvt.getObject(), OverlayChangeEvent.CODE.COLOR_CHANGED, anEvt.getExtra());
                break;
            default:
                break;
        }      
    }

    @Override
    public void frameChanged(FrameChangeEvent anEvt) {       
        for (Overlay o : iOverlays) 
            if (o instanceof Profile)
                o.show(((Profile)o).getFrameNumber() == anEvt.getFrame());                   
    }   
    
    private final static Logger LOG = LogManager.getLogger();
}

