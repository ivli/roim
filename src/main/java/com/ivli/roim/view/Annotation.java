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

import com.ivli.roim.calc.BaseFormatter;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import java.awt.Color;
import java.util.ArrayList;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.core.Filter;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.ROIChangeListener;

/**
 *
 * @author likhachev
 */
public abstract class Annotation extends ScreenObject implements ROIChangeListener {              
    protected boolean iMultiline = true; 
    protected final ArrayList<String> iAnnotation;    
    
    Annotation(int aUid, String aName, Shape aShape) {
        super(aUid, aName, aShape);   
        iAnnotation = new ArrayList<>();
    }
    
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE|HASCUSTOMMENU;}    
   
    public void setMultiline(boolean aM) {
        iMultiline = aM;
        notify(OverlayChangeEvent.CODE.PRESENTATION, null);
    }

    public boolean isMultiline() {
        return iMultiline;
    }
        
    public abstract Color getColor();
    
    protected abstract void computeShape(AbstractPainter aP);
    
    @Override
    void paint(AbstractPainter aP) {   
        computeShape(aP);
        aP.paint(this);    
    } 
        
    public ArrayList<String> getText() {
        return iAnnotation;
    }    
    /**
     * 
     */
    public static class Static extends Annotation {              
        protected final ROI iRoi;              
                
        protected Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   
        
        public Static(ROI aRoi) {
            super(-1, 
                  "ANNOTATION::STATIC", // NOI18N
                  aRoi.getShape() );  
            iRoi = aRoi;             
        }
  
        @Override
        public Color getColor() {return getRoi().getColor();}
        
        public void setFilters(Filter[] aF) {
            iFilters = aF;
            notify(OverlayChangeEvent.CODE.PRESENTATION, null);
        }  
                      
        public ROI getRoi() {return iRoi;}

        public Filter[] getFilters() {
            return iFilters;
        }
                
        @Override
        protected void computeShape(AbstractPainter aP) {            
            double width  = 0;
            double height = 0;

            ImageView w = aP.getView();
            
            final java.awt.FontMetrics fm = w.getFontMetrics(w.getFont());

            for (String s : iAnnotation) {
                Rectangle2D b = fm.getStringBounds(s, w.getGraphics());        

                if (iMultiline) {
                    width = Math.max(width, b.getWidth());
                    height += b.getHeight();
                } else {
                    width += b.getWidth();
                    height = Math.max(height, b.getHeight());
                }
            }
            
            final Rectangle2D bnds = new Rectangle2D.Double(0, 0, width, height);            
            final double scaleX = w.screenToVirtual().getScaleX();      
            
            if (null == iShape) 
                iShape = new Rectangle2D.Double(iRoi.getShape().getBounds2D().getX(),  
                                                iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * scaleX, 
                                                bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);                                                
            else
                iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), 
                                                getShape().getBounds2D().getY(),                                                                                        
                                                bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);                                             
        }
                
        @Override
        public void update(OverlayManager aM) {     
            iAnnotation.clear();
            
            for (Filter f : iFilters) {
                f.filter().eval(iRoi, aM);
                
                
                iAnnotation.add(f.getMeasurement().format(2.3));     
            }
        }
        
        public void OverlayChanged(OverlayChangeEvent anEvt) {
            switch (anEvt.getCode()) {                
                case MOVED: {//if not pinned move the same dX and dY                    
                    //final double[] deltas = (double[])anEvt.getExtra(); 
                    ///OverlayManager mgr = (OverlayManager)anEvt.getSource();
                    //mgr.moveObject(this, deltas[0], deltas[1]);                    
                } break;                 
            }
        }
         
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {   
            if (!anEvt.getObject().equals(iRoi))
                return; //not interested in 
            switch (anEvt.getCode()) {
                case DELETED: 
                    //commit suicide 
                    ((OverlayManager)anEvt.getSource()).deleteObject(this);
                    break;
                case MOVED: {//if it is not pinned down then move it the same dX and dY                    
                    final double[] deltas = (double[])anEvt.getExtra(); 
                    OverlayManager mgr = (OverlayManager)anEvt.getSource();
                    mgr.moveObject(this, deltas[0], deltas[1]); 
                    update(((OverlayManager)anEvt.getSource()));                   
                } ///fall through break;
                case CHANGED:                      
                default: //fall-through
                    //update(); break;
            }        
        }
        
        public String []getCustomMenu() {
            java.util.ArrayList<String> ret = new java.util.ArrayList<>();

            for (Filter f : iFilters)
                ret.add(f.getMeasurement().getName());

            return (String[])ret.toArray();
        }
    }
          
    /**
     *
     */
    public static class Active extends Annotation {        
        private final IOperation iOp;
        private final Overlay iR;
        
        Active(IOperation anOp, Overlay aR) {
            super(-1, "ANNOTATION.ACTIVE", null);                    
            iOp = anOp;       
            iR = aR;
        }   
        
        @Override
        public Color getColor() {
            return Color.RED;
        }
        
        @Override
        protected void computeShape(AbstractPainter aP) {
            final ImageView w = aP.getView();
            
            iAnnotation.clear();            
            iAnnotation.add(iOp.format(new BaseFormatter(w.getFrameNumber())));
            
            final Rectangle2D bnds = w.getFontMetrics(w.getFont()).getStringBounds(iAnnotation.get(0), w.getGraphics());        

            double posX = .0, posY = .0;

            if (null != iR) {                 
                Rectangle2D r = w.virtualToScreen().createTransformedShape(iR.getShape()).getBounds2D();
                posX = r.getX();
                posY = r.getY();

                if (!(iR instanceof Ruler))                
                    posY += bnds.getHeight();               
            }

            iShape = w.screenToVirtual().createTransformedShape(new Rectangle2D.Double(posX, posY, bnds.getWidth(), bnds.getHeight()));  
        }
       
        @Override
        public void update(OverlayManager aM) {              
           // iAnnotation.clear();            
           // iAnnotation.add(iOp.getCompleteString());
        }
        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {              
            switch (anEvt.getCode()) {
                case DELETED: 
                    break;
                case MOVED: {  
                    final double[] deltas = (double[])anEvt.getExtra();                    
                    ((OverlayManager)anEvt.getSource()).moveObject(this, deltas[0], deltas[1]);
                    //update();
                } break;            
                case CHANGED:   
                default: //fall-through
                    update((OverlayManager)anEvt.getSource());                                
                    break;
            }        
        }
    }          
}
