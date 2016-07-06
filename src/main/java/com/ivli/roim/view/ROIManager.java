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
import com.ivli.roim.core.IImageView;
import com.ivli.roim.core.IMultiframeImage;
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
    
    public ROIManager(IMultiframeImage anImage) {
        super (anImage, null);
    }
     
    /*
     * creates profile curve for whole width of the image
     * aS - shape <b><i> in screen coordinates </i></b>  
    */
    public void createProfile(Shape aS, IImageView aV) {               
        Rectangle r = aV.screenToVirtual().createTransformedShape(aS).getBounds();        
        r.x = 0;
        r.width = getImage().getWidth();
        
        Profile newRoi = new Profile(r);     
        addObject(newRoi);   
        newRoi.addChangeListener(this);
    }
            
    public void createRuler(Point aFrom, Point aTo, IImageView aV) {                
        Path2D.Double r = new Path2D.Double();         
        r.moveTo(aFrom.x, aFrom.y);
        r.lineTo(aTo.x, aTo.y);                                
        Shape s = aV.screenToVirtual().createTransformedShape(r);     
        
        Ruler ruler = new Ruler(s);     
                
        addObject(ruler);   
        ruler.update(this);
        ruler.addChangeListener(this);
        
        addObject(new Annotation.Active(ruler.getOperation(), ruler));
        
    }
    
    public void createAnnotation(ROI aROI) {    
        Annotation.Static ret = new Annotation.Static(aROI);
        addObject(ret);
        ret.update(this);
        addROIChangeListener(ret);        
    }    
        
    public void createAnnotation(BinaryOp anOp) {    
        Annotation ret = new Annotation.Active(anOp, ((com.ivli.roim.calc.ConcreteOperand)anOp.getLhs()).getROI());
        addObject(ret);  
        createSurrogateROI(anOp);
        ret.update(this);
    }
    
    void createSurrogateROI(BinaryOp anOp) {               
        final class SO implements IOperand {
            Series iV; 
            SO(Series aV) {
                iV = aV;
            } 
            public Series value() {return iV;}
            public String getString() {return "";}
        } 
        
        ROI surrogate = new ROI(iUid.getNext(), null, null, Color.YELLOW) {
             ROI iLhs = ((ConcreteOperand)anOp.getLhs()).getROI();
             ROI iRhs = ((ConcreteOperand)anOp.getRhs()).getROI();
             
            {               
                iRhs.addChangeListener(this);
                iLhs.addChangeListener(this);
            }
            
            @Override
            protected void buildSeriesIfNeeded(OverlayManager aMgr) {               
                final Measurement f1 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();                
                final Measurement f2 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();
                
                final Series aLhs = ((ConcreteOperand)anOp.getLhs()).getROI().getSeries(aMgr, f1);
                final Series aRhs = ((ConcreteOperand)anOp.getRhs()).getROI().getSeries(aMgr, f2);
                    
                if (null != aLhs && null != aRhs) {
                 /*   Series density = new Series(f1);
 
                    for (int i = 0; i < aLhs.getNumFrames(); ++i) {                    
                        double r = anOp.getOp().product(new SO(aLhs), new SO(aRhs)).value();
                        density.add(r);
                    } 
                    */
                    iSeries = new SeriesCollection();
                    iSeries.addSeries(anOp.getOp().product(new SO(aLhs), new SO(aRhs)).value());
                }  
 
            }
            
            public void ROIChanged(ROIChangeEvent anEvt) {
                if (iLhs.equals(anEvt.getObject()) || iRhs.equals(anEvt.getObject())) {
                    switch (anEvt.getCode()) {
                        case MOVED: //cheat
                           // update((OverlayManager)anEvt.getExtra());
                           // notifyROIChanged(ROIChangeEvent.ROIMOVED, anEvt.getExtra()); break;
                        case ALLDELETED: //TODO;
                        default: break;
                    }
                }            
            }            
        };  
                        
        surrogate.setVisible(false);
        addObject(surrogate);
        surrogate.update(this);
        ////notifyROIChanged(surrogate, ROIChangeEvent.CODE.CREATED, this);  
        surrogate.addChangeListener(this);
    }
    
    public Overlay cloneObject(Overlay aR) {             
        Overlay ret;
        
        try {
            ret = aR.getClass().getConstructor(int.class).newInstance(iUid.getNext());             
            ret.iShape = aR.getShape();
            
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException ex) {
            LOG.debug("failed to clone object {}, due to {}", aR, ex);
            return null;
        }
        
        if (ret instanceof ROI)
            internalAddRoi((ROI)ret);
        else
            addObject(ret);
        
        return ret;        
    }
       
    private void internalAddRoi(ROI aR) {
        if (null != iParent)
            iParent.addObject(aR);
        else
            addObject(aR);
        
        if (ROI_HAS_ANNOTATIONS)    
            createAnnotation((ROI)aR);            
                      
        aR.addChangeListener(this);        
        aR.update(this);        
    }
    
    public void createRoi(Shape aS, IImageView aV) {                 
        final Shape shape = aV.screenToVirtual().createTransformedShape(aS);        
        ROI ret = new ROI(iUid.getNext(), null, shape, null);         
        internalAddRoi(ret);       
    }
    
    private final static Logger LOG = LogManager.getLogger();
}

