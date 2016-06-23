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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Color;
import java.awt.geom.Path2D;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.calc.BinaryOp;
import com.ivli.roim.calc.ConcreteOperand;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author likhachev
 */
public class ROIManager extends OverlayManager {      
    private static final boolean ROI_HAS_ANNOTATIONS  = true;
    private static final boolean CLONE_INHERIT_COLOUR = false;
       
    private final static class TUid {
        int iUid;
        public static final int UID_INVALID = -1;
        
        public TUid() {iUid = 0;}
        
        public int getNext() {            
            return ++iUid;
        }
    }
    
    private final static TUid iUid = new TUid();
    
    public ROIManager() {
        super (null);
    }
     
    /*
     * creates profile curve for whole width of the image
     * aS - shape <b><i> in screen coordinates </i></b>  
    */
    public void createProfile(Shape aS) {               
        Rectangle r = iView.screenToVirtual().createTransformedShape(aS).getBounds();        
        r.x = 0;
        r.width = getImage().getWidth();
        
        Profile newRoi = new Profile(r);     
        addObject(newRoi);   
        newRoi.addROIChangeListener(this);
    }
            
    public void createRuler(Point aFrom, Point aTo) {                
        Path2D.Double r = new Path2D.Double();         
        r.moveTo(aFrom.x, aFrom.y);
        r.lineTo(aTo.x, aTo.y);                                
        Shape s = iView.screenToVirtual().createTransformedShape(r);     
        
        Ruler ruler = new Ruler(s);     
                
        addObject(ruler);   
        ruler.addROIChangeListener(this);
   
        addObject(new Annotation.Active(ruler.getOperation(), ruler));
    }
    
    public void createAnnotation(BinaryOp anOp) {    
        Annotation a = new Annotation.Active(anOp, ((com.ivli.roim.calc.ConcreteOperand)anOp.getLhs()).getROI());
        addObject(a);  
        createSurrogateROI(anOp);
    }
    
    public void createAnnotation(ROI aROI) {    
        Annotation.Static o = new Annotation.Static(aROI);
        addObject(o);
        o.update(this);
        aROI.addROIChangeListener(o);        
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
        
        ROI surrogate = new ROI(iUid.getNext(), null, ((ConcreteOperand)anOp.getLhs()).getROI().getShape(), Color.YELLOW) {
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
        addObject(surrogate);
        surrogate.update(this);
        notifyROIChanged(surrogate, ROIChangeEvent.ROICREATED, null);  
        surrogate.addROIChangeListener(this);
    }
    
    public Overlay cloneObject(Overlay aR) {             
        Overlay ret;
        
        try {
            ret = aR.getClass().getConstructor(int.class, ROIManager.class).newInstance(iUid.getNext(), this);             
            ret.iShape = aR.getShape();
            
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException ex) {
            LOG.debug("failed to clone object {}, due to {}", aR, ex);
            return null;
        }
        
        addObject(ret);
        
        if (aR instanceof ROI && ROI_HAS_ANNOTATIONS) {     
            createAnnotation((ROI)ret);            
        } 
        
        
        ret.addROIChangeListener(this);
        
        ret.update(this);
        notifyROIChanged(ret, ROIChangeEvent.ROICREATED, aR); 
        return ret;        
    }
    
    protected void internalCreateROI(ROI aS) {
        ROI newRoi = new ROI(iUid.getNext(), null, aS.getShape(), aS.getColor());       
  
        addObject(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update(this);
        notifyROIChanged(newRoi, ROIChangeEvent.ROICREATED, null);    
    }
    
    public void createRoi(Shape aS) {                 
        final Shape shape = iView.screenToVirtual().createTransformedShape(aS);
        
        ROI newRoi = new ROI(iUid.getNext(), null, shape, null);       
  
        addObject(newRoi);
        
        if (ROI_HAS_ANNOTATIONS) 
            createAnnotation(newRoi);      
       
        newRoi.addROIChangeListener(this);
        
        newRoi.update(this);
       // notifyROIChanged(newRoi, ROIChangeEvent.ROICREATED, null);
    }
   /*
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
    */
 
    private final static Logger LOG = LogManager.getLogger();
}

