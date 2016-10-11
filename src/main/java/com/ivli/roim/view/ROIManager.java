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
import com.ivli.roim.calc.BinaryOp;
import com.ivli.roim.calc.ConcreteOperand;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Uid;
import com.ivli.roim.events.OverlayChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class ROIManager extends OverlayManager {                        
    private final Uid iUid;
    private boolean iAnnotationsAutoCreate;
    
    public ROIManager(IMultiframeImage anImage, Uid aUid, boolean aAnnotationsAutoCreate) {
        super (anImage);
        iUid = aUid;
        iAnnotationsAutoCreate = true;//aAnnotationsAutoCreate;        
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
            
    public Overlay createRuler(Point aFrom, Point aTo, IImageView aV) {                
        Path2D.Double r = new Path2D.Double();         
        r.moveTo(aFrom.x, aFrom.y);
        r.lineTo(aTo.x, aTo.y);                                
        Shape s = aV.screenToVirtual().createTransformedShape(r);     
        
        Ruler ret = new Ruler(s, aV);     
                
        addObject(ret);   
        //ruler.update(this);
        ret.addChangeListener(this);
        
        addObject(new Annotation.Active(ret.getOperation(), ret, aV));  
        return ret;
    }
    
    public Overlay createAnnotation(ROI aROI, IImageView aV) {    
        Annotation.Static ret = new Annotation.Static(aROI, aV);
        addObject(ret);        
        addROIChangeListener(ret);   //TODO:?????????   
        return ret;
    }    
        
    public Overlay createAnnotation(BinaryOp anOp, IImageView aV) {    
        Annotation ret = new Annotation.Active(anOp, ((com.ivli.roim.calc.ConcreteOperand)anOp.getLhs()).getROI(), aV);
        addObject(ret);  
        createSurrogateROI(anOp);
        ret.update(this);
        return ret;
    }
    
    void createSurrogateROI(BinaryOp anOp) {               
        final class SO implements IOperand {
            ISeries iV; 
            SO(ISeries aV) {
                iV = aV;
            } 
            public ISeries value() {return iV;}
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
            public void update(OverlayManager aMgr) {               
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
                        case MOVED: //cheat
                           // update((OverlayManager)anEvt.getExtra());
                           // notifyROIChanged(ROIChangeEvent.ROIMOVED, anEvt.getExtra()); break;
                        //TODO;
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
    
    public Overlay cloneObject(Overlay aR, IImageView aV) {             
        Overlay ret = null;
        
        if (aR instanceof ROI) {
            ret = new ROI(Uid.getNext(), null !=aV ? null : aR.getName(), aR.getShape(), null != aV ? null : ((ROI) aR).getColor());         
            addObject(ret);

            if (iAnnotationsAutoCreate)    
                createAnnotation((ROI)ret, aV); 
        }
        
        return ret;
        /*
        try {
            ret = aR.getClass().getConstructor(Uid.class).newInstance(iUid.getNext());             
            ret.iShape = aR.getShape();            
        } catch (InstantiationException|IllegalAccessException|NoSuchMethodException|InvocationTargetException ex) {
            LOG.catching(ex); //NOI18N            
        }
        
        addObject(ret);
        
        if (ret instanceof ROI){
            java.util.List <Overlay> l = iOverlays.stream()
                     .filter((o) -> o instanceof Annotation.Static)
                     .filter((o) -> ((Annotation.Static)(o)).getRoi() == aR)
                     .collect(Collectors.toList());
                     //.forEach((o) -> cloneObject(o));
            
            for (Overlay o:l) {
                
            }
        
            ///internalAddRoi((ROI)ret, aR);            
        }
        */
         
    }
     
    /*
    private void internalAddRoi(ROI aR, IImageView aV) {    
            addObject(aR);
        
        if (iAnnotationsAutoCreate)    
            createAnnotation(aR, aV);            
                      
        aR.addChangeListener(this);                   
    }
    */
    
    public Overlay createROI(Shape aS, IImageView aV) {                 
        final Shape shape = aV.screenToVirtual().createTransformedShape(aS);        
        ROI ret = new ROI(iUid.getNext(), null, shape, null);         
        addObject(ret);
        
        if (iAnnotationsAutoCreate)    
            createAnnotation(ret, aV); 
        
        return ret;
    }
    
    private final static Logger LOG = LogManager.getLogger();  
}

