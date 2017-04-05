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
import com.ivli.roim.calc.BinaryOp;
import com.ivli.roim.calc.ConcreteOperand;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Uid;
import com.ivli.roim.events.OverlayChangeEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class ROIManager extends OverlayManager {                            
    private static final boolean CREATE_ANNOTATIONS_FOR_ROIS = true;
    private static final boolean CREATE_ANNOTATIONS_FOR_RULER = false;
           
    public ROIManager(IMultiframeImage anImage) {
        super (anImage);    
    }
          
    /*
     * creates profile curve for whole width of the image
     * aS - shape <b><i> in screen coordinates </i></b>  
    */
    public Overlay createProfile(Shape aS, IImageView aV) {               
        Rectangle r = aV.screenToVirtual().createTransformedShape(aS).getBounds();        
        r.x = 0;
        r.width = getImage().getWidth();
        
        Profile ret = new Profile(r, aV);     
        addObject(ret);   
        ret.addChangeListener(this);
        return ret; 
    }
  
    public Overlay createAnnotation(ROI aROI, IImageView aV) {    
        Annotation.Static ret = new Annotation.Static(aV, aROI, aROI, aROI.getColor());
        addObject(ret);        
        addChangeListener(ret, null);   //TODO:?????????   
        return ret;
    }    
        
    public Overlay createAnnotation(BinaryOp anOp, IImageView aV) {    
        Annotation ret = new Annotation.Active(anOp, ((com.ivli.roim.calc.ConcreteOperand)anOp.getLhs()).getROI(), aV);
        addObject(ret);  
        createSurrogateRoi(anOp);
        ret.update(this);
        return ret;
    }
    
    public Overlay createSurrogateRoi(BinaryOp anOp) {    
        ROI surrogate = new ROI(new Rectangle(), 
                                ((ConcreteOperand)anOp.getLhs()).getROI().getName() + anOp.getOp().getOperationChar() + ((ConcreteOperand)anOp.getRhs()).getROI().getName(), 
                                null) {
            ROI iLhs = ((ConcreteOperand)anOp.getLhs()).getROI();
            ROI iRhs = ((ConcreteOperand)anOp.getRhs()).getROI();            
            {              
                iRhs.addChangeListener(this);
                iLhs.addChangeListener(this);
            }
            
            @Override
            public int getStyles() {
                return OVL_HIDDEN;
            }
            
            @Override
            public boolean contains(Point2D aNotUsed){return false;}
            @Override
            public void update(OverlayManager aMgr) {       
                 final class SO implements IOperand {
                    ISeries iV; 
                    SO(ISeries aV) {
                        iV = aV;
                    } 
                    public ISeries value() {return iV;}
                    public String getString() {return "";}
                } 
                 
                final Measurement f1 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();                
                final Measurement f2 = ((ConcreteOperand)anOp.getLhs()).getFilter().getMeasurement();
                
                final ISeries aLhs = ((ConcreteOperand)anOp.getLhs()).getROI().getSeries(f1);
                final ISeries aRhs = ((ConcreteOperand)anOp.getRhs()).getROI().getSeries(f2);
                    
                if (null != aLhs && null != aRhs) {                
                    iSeries = new SeriesCollection();
                    iSeries.addSeries(anOp.getOp().product(new SO(aLhs), new SO(aRhs)).value());
                }  
            }
            
            public void OverlayChanged(OverlayChangeEvent anEvt) {
                if (iLhs.equals(anEvt.getObject()) || iRhs.equals(anEvt.getObject())) {
                    switch (anEvt.getCode()) {
                        case MOVED:  //cheat
                        //case MOVING: //fall through and cheat as well
                            update(ROIManager.this);
                            notifyROIChanged(this, OverlayChangeEvent.CODE.MOVED, null); break;                       
                        default: break;
                    }
                }            
            }            
        };  
                        
        surrogate.show(false);
        addObject(surrogate);
        surrogate.update(this);
        ////notifyROIChanged(surrogate, ROIChangeEvent.CODE.CREATED, this);  
        surrogate.addChangeListener(this);
        return surrogate;
    }
    
    public Overlay cloneObject(Overlay aR, IImageView aV) {                 
        if (0 != (aR.getStyles() & Overlay.OVL_CLONEABLE) ) {
            if (aR instanceof ROI)                         
                return internalCreateROI(aR.getShape(), aV);        
            else if (aR instanceof Overlay) {
                //TODO:
            }
        }
        return null;
    }
     
    public Overlay createROI(Shape aS, IImageView aV) {
        final Shape shape = aV.screenToVirtual().createTransformedShape(aS); 
        return internalCreateROI(shape, aV);
    }
    
    private Overlay internalCreateROI(Shape aS, IImageView aV) {                 

        String name = "ROI" + Uid.getNext(ROI.class);  
        Color clr = Colorer.getNextColor(ROI.class);
               
        String decorator="";
        do {
            name += decorator;
            decorator += ".1";
        } while(null != findObject(name));         
            
        
        ROI ret = new ROI(aS, name, clr);         
        addObject(ret);
        
        if (CREATE_ANNOTATIONS_FOR_ROIS) { 
            Annotation.Static ano = new Annotation.Static(aV, ret, ret, ret.getColor());
            addObject(ano);        
            addChangeListener(ano, ret); //let ROIManager forward events to, so it can smoothly addjust position when needed    
        }
        return ret;
    }
    
     public Overlay createRuler(Point aFrom, Point aTo, IImageView aV) {                        
        Handle beg = new Handle(aV.screenToVirtual(aFrom));
        Handle end = new Handle(aV.screenToVirtual(aTo));
        Ruler ruler = new Ruler(aV, beg, end);                     
           
        addObject(beg);
        addObject(end);
        addObject(ruler);
        
        ruler.addChangeListener(this); 
        
        if (CREATE_ANNOTATIONS_FOR_RULER) { 
            Annotation.Static ano = new Annotation.Static(aV, ruler, ruler, Color.RED);
            addObject(ano);        
            addChangeListener(ano, ruler);   
        }
        return ruler;
    }
    
    public void externalize(ObjectOutputStream ois) {
    
    }
    
    public void internalize(ObjectInputStream ois) 
            throws IOException, ClassNotFoundException {
        
        Collection<Overlay> sel = (Collection<Overlay>)ois.readObject();

        for (Overlay o:sel) { 
            if( new Rectangle(getImage().getWidth(), getImage().getHeight()).contains(o.getShape().getBounds()))
                cloneObject(o, null); 
            else 
                throw new IOException("File has wrong data");
        }
    
    }
    
    private final static Logger LOG = LogManager.getLogger();  
}

