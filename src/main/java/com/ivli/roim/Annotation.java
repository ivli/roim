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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import java.awt.Shape;

import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.core.Filter;
import java.awt.Rectangle;

/**
 *
 * @author likhachev
 */
public abstract class Annotation extends Overlay implements ROIChangeListener {              
    Annotation(String aName, Shape aShape, ROIManager aRM) {
        super(aName, aShape, aRM);    
    }
    
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE|HASCUSTOMMENU;}    
   
    /**
     *
     */
    public static class Static extends Annotation {
        protected boolean iMultiline = true;       
        protected final ROI iRoi;                 
        protected final java.util.ArrayList<String> iAnnotation = new java.util.ArrayList<>();           
      
        protected Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   
        
        public Static(ROI aRoi, ROIManager aRM) {
            super("ANNOTATION::STATIC", // NOI18N
                    null, null != aRM ? aRM : aRoi.getManager());  
            iRoi = aRoi;     
            /* */
            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.filter(aRoi)));        

            computeShape(true);

            aRoi.addROIChangeListener(this);
        }
                
        public void setFilters(Filter[] aF) {
            iFilters = aF;
            notifyROIChanged(ROIChangeEvent.ROICHANGED, null);
        }  
                      
        public Overlay getRoi() {return iRoi;}

        public Filter[] getFilters() {
            return iFilters;
        }

        public void setMultiline(boolean aM) {
            iMultiline = aM;
            notifyROIChanged(ROIChangeEvent.ROICHANGED, null);
        }

        public boolean isMultiline() {
            return iMultiline;
        }
               
        private void computeShape(boolean aInitial) {            
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
            
            if (aInitial) 
                iShape = new Rectangle2D.Double(iRoi.getShape().getBounds2D().getX(),  
                                                iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX() , 
                                                bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                                bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());
            else
                iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), getShape().getBounds2D().getY(),                                                                                        
                                                bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);                                             
        }
                
        @Override
        public void update() {     
            iAnnotation.clear();
            
            //if (iRoi instanceof ROI) {
            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.filter(iRoi)));     
            //}
            computeShape(false);   
        }

        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {               
            switch (anEvt.getChange()) {
                case ROIChangeEvent.ROIDELETED: 
                    //commit suicide 
                    getManager().deleteOverlay(this);
                    break;
                case ROIChangeEvent.ROIMOVED: {//if not pinned move the same dX and dY
                    final double[] deltas = (double[])anEvt.getExtra();
                    ///logger.info(String.format("%f, %f",deltas[0], deltas[1]));
                    move(deltas[0], deltas[1]);
                    update();
                } break;
                case ROIChangeEvent.ROICHANGED:   
                default: //fall-through
                    update(); break;
            }        
        }

        @Override
        void paint(Graphics2D aGC, AffineTransform aTrans) {
            final Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();     
            aGC.setColor(iRoi.getColor());
            if (!iMultiline) {
                String str = new String();
                str = iAnnotation.stream().map((s) -> s).reduce(str, String::concat);            
                aGC.drawString(str, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));                   
            } else {            
                final double stepY = temp.getHeight() / iAnnotation.size();
                double posY = temp.getY() + stepY - 4.;
                for(String str : iAnnotation) {                               
                    aGC.drawString(str, (int)temp.getX(), (int)posY);
                    posY += stepY;
                }
            }
            aGC.draw(temp);                 
        }               

        /**
         * returns a list of menu strings
         * @return
         */
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
        private Color iColor = Color.RED;
        private String iAnnotation;        
        private Overlay iOverlay;
        private IOperation iOp;
        
        Active(IOperation aOp, Overlay anO, ROIManager aRM) {
            super("ANNOTATION.ACTIVE", null, aRM);        
            iOverlay = anO;
            iOp = aOp;     
            
            iAnnotation = iOp.getCompleteString();
            
            final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
            
           /// final double scaleX = getManager().getView().screenToVirtual().getScaleX();
            double posX = .0, posY = .0;
           
            if (null != iOverlay) {
                Rectangle r = anO.getShape().getBounds();
                posX = r.x;
                posY = r.y + bnds.getHeight();
            }
            
            iShape = getManager().getView().screenToVirtual().createTransformedShape(new Rectangle2D.Double(posX, posY, bnds.getWidth(), bnds.getHeight()));            
        }   
        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {              
            switch (anEvt.getChange()) {
                case ROIChangeEvent.ROIDELETED: 
                   break;
                case ROIChangeEvent.ROIMOVED: {
                
                
                } break;            
                case ROIChangeEvent.ROICHANGED:   
                default: //fall-through
                    update();                                
                    break;
            }        
        }

        @Override
        void paint(Graphics2D aGC, AffineTransform aTrans) {
            
            final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
            
           /// final double scaleX = getManager().getView().screenToVirtual().getScaleX();
           /* double posX = .0, posY = .0;
           
            if (null != iOverlay) {
                Rectangle r = iOverlay.getShape().getBounds();
                posX = r.x;
                posY = r.y + bnds.getHeight();
            }
            */
           
                   
          
            final Rectangle2D tmp = iShape.getBounds();     
           // iShape = new Rectangle2D.Double(tmp.getX(), tmp.getY(), bnds.getWidth(), bnds.getHeight());     
            
            aGC.setColor(iColor);

            aGC.drawString(iOp.getCompleteString(), (int)tmp.getX(), (int)(tmp.getY() + bnds.getHeight() - 4));       
            aGC.draw(bnds);        
        }
        
        @Override
        public void update() {      
            iAnnotation = iOp.getCompleteString();
/*
            final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
            
            //final double scaleX = getManager().getView().screenToVirtual().getScaleX();    
            Rectangle2D temp = getShape().getBounds2D();
            temp.setFrame(temp.getX(), temp.getY(), bnds.getWidth(), bnds.getHeight());
            iShape = getManager().getView().screenToVirtual().createTransformedShape(temp);                   
*/
        }
    }  
        
}
