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
package com.ivli.roim.view;

import com.ivli.roim.core.SeriesCollection;
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
import java.awt.Color;
import java.awt.geom.Path2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.calc.BinaryOp;
import com.ivli.roim.calc.ConcreteOperand;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author likhachev
 */
public class ROIManager implements ROIChangeListener, java.io.Serializable {  
    private static final long serialVersionUID = 42L;    
    private static final boolean ROI_HAS_ANNOTATIONS  = true;
    private static final boolean CLONE_INHERIT_COLOUR = false;

    transient private ImageView iView; /*TODO: shall be interface - IImageView Annotations do not let make this transition since they do use methods of JComponent for drawing
                                        * hold Annotations, Ruler and perhaps all the rest classes in another container in view leave here only ROI
                                        * SIC: what 'bout the plan to display curves of Active annotations???    
                                        */
    
    private final HashSet<Overlay> iOverlays; 
    private final HashSet<Overlay> iRois = new HashSet(); 
    
    private final EventListenerList iList;
    
    private final static class TUid {
        int iUid;
        public static final int UID_INVALID = -1;
        public TUid(int aStart) {iUid = aStart;}
        public String getNext() {            
            return String.format("ROI" + 
                                 "%03d", // NOI18N
                                 iUid++);
        }
    }
    
    private final static TUid iUid = new TUid(0);
    
    public ROIManager() {        
        iOverlays = new HashSet();        
        iList = new EventListenerList();
    }
    
    public void setView(ImageView aV) {
         iView = aV;
    }
      
    public ImageView getView() {
        return iView;
    } 
    
    public IMultiframeImage getImage() {
        return iView.getImage();
    }
      
    public void clear() {
        iOverlays.clear();
        notifyROIChanged(null, ROIChangeEvent.ROIALLDELETED, null);      
    }
    
    public void update() {
        iOverlays.stream().forEach((o) -> {
            o.update();
        });
    }
    
    public void paint(AbstractPainter aP) {       
        iRois.stream().forEach((r) -> {
            if (r.isVisible()) 
                r.paint(aP);
        });
        
        iOverlays.stream().forEach((o) -> {
            if(o.isVisible()) 
                o.paint(aP);
        });
    }    
    
    /*
     * creates profile curve for whole width of the image
     * aS - shape <b><i> in screen coordinates </i></b>  
    */
    public void createProfile(Shape aS) {               
        Rectangle r = iView.screenToVirtual().createTransformedShape(aS).getBounds();        
        r.x = 0;
        r.width = getImage().getWidth();
        
        Profile newRoi = new Profile(r, this);     
        iOverlays.add(newRoi);   
        newRoi.addROIChangeListener(this);
    }
            
    public void createRuler(Point aFrom, Point aTo) {
        //Rectangle2D s = new Rectangle2D.Double(aS.x, aS.y, aS.x + (aF.x - aS.x), Math.abs(aF.y-aS.y));        
        Path2D.Double r = new Path2D.Double();         
        r.moveTo(aFrom.x, aFrom.y);
        r.lineTo(aTo.x, aTo.y);                                
        Shape s = iView.screenToVirtual().createTransformedShape(r);     
        
        Ruler ruler = new Ruler(s, this);     
                
        iOverlays.add(ruler);   
        ruler.addROIChangeListener(this);
   
        iOverlays.add(new Annotation.Active(ruler.getOperation(), ruler, this));
    }
    
    public void createAnnotation(BinaryOp anOp) {    
        Annotation a = new Annotation.Active(anOp, ((com.ivli.roim.calc.ConcreteOperand)anOp.getLhs()).getROI(), this);
        iOverlays.add(a);  
        createSurrogateROI(anOp);
    }
    
    public void createAnnotation(ROI aROI) {    
        iOverlays.add(new Annotation.Static(aROI, this));      
    }    
    
    void createSurrogateROI(BinaryOp anOp) {           
        final String ln = ((ConcreteOperand)anOp.getLhs()).getROI().getName();
        final String rn = ((ConcreteOperand)anOp.getRhs()).getROI().getName();
        final String on = anOp.getOp().getOperationChar();
        
        final class SO implements IOperand {
            double iV; 

            SO(double aV) {
                iV = aV;
            } 
            public double value() { return iV;}
            public String getString() { return "";}
        } 
        
        ROI surrogate = new ROI(ln + on + rn, ((ConcreteOperand)anOp.getLhs()).getROI().getShape(), this, Color.YELLOW) {
            void buildSeriesIfNeeded() {               
                final Measurement f1 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();                
                final Measurement f2 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();
                
                final Series aLhs = ((ConcreteOperand)anOp.getLhs()).getROI().getSeries(f1);
                final Series aRhs = ((ConcreteOperand)anOp.getRhs()).getROI().getSeries(f2);
                     
                if (null != aLhs && null != aRhs) {
                    Series density = new Series(f1);

                    for (int i = 0; i < aLhs.getNumFrames(); ++i) {                    
                        double r = anOp.getOp().product(new SO(aLhs.get(i)), new SO(aRhs.get(i))).value();
                        density.add(r);
                    } 

                    iSeries = new SeriesCollection();
                    iSeries.addSeries(density);
                }  
            }
        };  
                        
        surrogate.setVisible(false);
        iRois.add(surrogate);
        surrogate.update();
        notifyROIChanged(surrogate, ROIChangeEvent.ROICREATED, null);  
        surrogate.addROIChangeListener(this);
    }
    
    protected void internalCreateROI(ROI aS) {
        ROI newRoi = new ROI(iUid.getNext(), aS.getShape(), this, aS.getColor());       
  
        iRois.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.ROICREATED, null);    
    }
    
    public void createRoi(Shape aS) {                 
        final Shape shape = iView.screenToVirtual().createTransformedShape(aS);
        
        ROI newRoi = new ROI(iUid.getNext(), shape, this, null);       
  
        iRois.add(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.ROICREATED, null);
    }
    
    public ROI cloneRoi(ROI aR) {       
        ROI newRoi = new ROI(iUid.getNext(), aR.getShape(), this, CLONE_INHERIT_COLOUR ? aR.getColor() : null);               
        iRois.add(newRoi); 
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);       
        
        newRoi.addROIChangeListener(this);
        
        newRoi.update();
        notifyROIChanged(newRoi, ROIChangeEvent.ROICREATED, aR); 
        return newRoi;
    }
    
    public void moveObject(Overlay aO, double adX, double adY) {                         
        if (!aO.isPinned()) {          
            Shape temp = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(aO.getShape());        
            Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getImage().getWidth(), getImage().getHeight());

            if (bounds.contains(temp.getBounds()))            
                aO.move(adX, adY);   
        }
    }
    
    public Overlay findObject(Point aP) {      
        final Rectangle temp = iView.screenToVirtual().createTransformedShape(new Rectangle(aP.x, aP.y, 3, 1)).getBounds();
                
        for (Overlay o : iOverlays) {           
            if (o.isSelectable() && o.intersects(temp)) 
                return o;                                   
        }
        
        for (Overlay r: iRois)
            if (r.isSelectable() && r.intersects(temp))
                return r;
        
        return null;
    }
        
    boolean deleteRoi(ROI aR) {         
        final Iterator<Overlay> it = iOverlays.iterator();

        while (it.hasNext()) {  //clean annotations out - silly but workin'
            final Overlay o = it.next();
            if (o instanceof Annotation.Static && ((Annotation.Static)o).getRoi() == aR)               
                it.remove();
        } 
                
        notifyROIChanged(aR, ROIChangeEvent.ROIDELETED, null);
        
        return iRois.remove(aR);   
    }  
    
    public boolean deleteObject(Overlay aO) {      
        if (aO instanceof ROI)
            return deleteRoi((ROI)aO);
        else
            return iOverlays.remove(aO);   
    }
        
    public Iterator<Overlay> getObjects() {        
        return iRois.iterator();
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
           LOG.error("Unable to externalize object {}", ex); 
        } 
    }
             
    void internalize(String aFileName) {
        try(FileInputStream fis = new FileInputStream(aFileName)) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            readObject(ois);           
            ois.close();
            fis.close();            
        } catch (IOException|ClassNotFoundException ex) {
            LOG.error("Unable to deserialize {}", ex);
        }         
    }
    
    public void addROIChangeListener(ROIChangeListener aL) {        
        iList.add(ROIChangeListener.class, aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {        
        iList.remove(ROIChangeListener.class, aL);
    }
    
    void notifyROIChanged(Overlay aR, int aS, Object aEx) {
        final ROIChangeEvent evt = new ROIChangeEvent(this, aS, aR, aEx);

        ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);

        for (ROIChangeListener l : arr)
            l. ROIChanged(evt);
    }
        
    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {        
        notifyROIChanged(anEvt.getObject(), anEvt.getChange(), anEvt.getExtra());
    }
    
    private static final Logger LOG = LogManager.getLogger();
}

