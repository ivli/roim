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

import java.awt.geom.Rectangle2D;
import java.awt.Shape;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.core.Filter;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author likhachev
 */
public abstract class Annotation extends ScreenObject implements ROIChangeListener {              
    protected boolean iMultiline = true; 
   
    Annotation(String aName, Shape aShape, ROIManager aRM) {
        super(aName, aShape, aRM);    
    }
    
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE|HASCUSTOMMENU;}    
   
    public void setMultiline(boolean aM) {
        iMultiline = aM;
        notifyROIChanged(ROIChangeEvent.ROICHANGED, null);
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
    
    abstract public ArrayList<String> getText();
        
    /**
     *
     */
    public static class Static extends Annotation {              
        protected final ROI iRoi;              
        protected final ArrayList<String> iAnnotation;           
      
        protected Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   
        
        public Static(ROI aRoi, ROIManager aRM) {
            super("ANNOTATION::STATIC", // NOI18N
                    null, null != aRM ? aRM : aRoi.getManager());  
            iRoi = aRoi;     
            iAnnotation = new ArrayList<>();
            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.filter(aRoi)));        

            aRoi.addROIChangeListener(this);
        }
              
        public ArrayList<String> getText() {return iAnnotation;}
        public Color getColor() {return getRoi().getColor();}
        
        public void setFilters(Filter[] aF) {
            iFilters = aF;
            notifyROIChanged(ROIChangeEvent.ROICHANGED, null);
        }  
                      
        public ROI getRoi() {return iRoi;}

        public Filter[] getFilters() {
            return iFilters;
        }
                
        protected void computeShape(AbstractPainter aP) {            
            double width  = 0;
            double height = 0;

            final java.awt.FontMetrics fm = getManager().getView().getFontMetrics(getManager().getView().getFont());

            for (String s : iAnnotation) {
                Rectangle2D b = fm.getStringBounds(s, getManager().getView().getGraphics());        

                if (iMultiline) {
                    width = Math.max(width, b.getWidth());
                    height += b.getHeight();
                } else {
                    width += b.getWidth();
                    height = Math.max(height, b.getHeight());
                }
            }
            
            final Rectangle2D bnds = new Rectangle2D.Double(0, 0, width, height);            
            final double scaleX = getManager().getView().screenToVirtual().getScaleX();      
            
            if (null == iShape) 
                iShape = new Rectangle2D.Double(iRoi.getShape().getBounds2D().getX(),  
                                                iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX() , 
                                                bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                                bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());
            else
                iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), 
                                                getShape().getBounds2D().getY(),                                                                                        
                                                bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);                                             
        }
                
        @Override
        public void update() {     
            iAnnotation.clear();
           
            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.filter(iRoi)));     
         
            ///computeShape();   
        }
        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {               
            switch (anEvt.getChange()) {
                case ROIChangeEvent.ROIDELETED: 
                    //commit suicide 
                    getManager().deleteObject(this);
                    break;
                case ROIChangeEvent.ROIMOVED: {//if not pinned move the same dX and dY
                    final double[] deltas = (double[])anEvt.getExtra();
                    ///logger.info(String.format("%f, %f",deltas[0], deltas[1]));
                    getManager().moveObject(this, deltas[0], deltas[1]);
                    ///update();
                } break;
                case ROIChangeEvent.ROICHANGED:   
                default: //fall-through
                    update(); break;
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
        private final Overlay  iOver;
        
        Active(IOperation aOp, Overlay anO, ROIManager aRM) {
            super("ANNOTATION.ACTIVE", null, aRM);                    
            iOp = aOp;       
            iOver = anO;
        }   
        
        public Color getColor() {return Color.RED;}
        
        protected void computeShape(AbstractPainter aP) {
            final ImageView w =aP.getView();
            final Rectangle2D bnds = w.getFontMetrics(w.getFont()).getStringBounds(iOp.getCompleteString(), w.getGraphics());        

            double posX = .0, posY = .0;

            if (null != iOver) {                 
                Rectangle2D r = w.virtualToScreen().createTransformedShape(iOver.getShape()).getBounds2D();
                posX = r.getX();
                posY = r.getY();

                if (!(iOver instanceof Ruler))                
                    posY += bnds.getHeight();               

                iOver.addROIChangeListener(this);
            }

            iShape = w.screenToVirtual().createTransformedShape(new Rectangle2D.Double(posX, posY, bnds.getWidth(), bnds.getHeight()));  
        }
               
        public ArrayList<String> getText() {
            ArrayList<String> ret = new ArrayList<>();            
            ret.add(iOp.getCompleteString());
            return ret;
        }
               
        @Override
        public void update() {    }
        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {              
            switch (anEvt.getChange()) {
                case ROIChangeEvent.ROIDELETED: 
                    break;
                case ROIChangeEvent.ROIMOVED: {  
                    final double[] deltas = (double[])anEvt.getExtra();                    
                    move(deltas[0], deltas[1]);
                    update();
                } break;            
                case ROIChangeEvent.ROICHANGED:   
                default: //fall-through
                    update();                                
                    break;
            }        
        }
    }          
}
