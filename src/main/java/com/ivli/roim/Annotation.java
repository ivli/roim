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

        /**
         * 
         */
        protected boolean iMultiline = true;

        /**
         * filters to display measurenment results
         */
        protected Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   

        /**
         * source ROI this annotation is for
         */
        protected final ROI iRoi;          
        /**
         * string annotations 
         */
        protected final java.util.ArrayList<String> iAnnotation = new java.util.ArrayList<>();           

        /**
         *
         * @param aRoi
         * @param aRM
         */
        public Static(ROI aRoi, ROIManager aRM) {
            super("ANNOTATION", null, null != aRM ? aRM : aRoi.getManager());  
            iRoi = aRoi;     

            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.get(iRoi)));        

            final Rectangle2D bnds = calcBounds();

            iShape = new Rectangle2D.Double(iRoi.getShape().getBounds2D().getX(),  
                                            iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX() , 
                                            bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                            bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());

            aRoi.addROIChangeListener(this);
        }

        /**
         *
         * @return
         */
        public ROI getRoi() {return iRoi;}

        /**
         *
         * @param aF
         */
        public void setFilters(Filter[] aF) {
            iFilters = aF;
            notifyROIChanged(ROIChangeEvent.CHG.Changed, null);
        }  

        /**
         *
         * @return
         */
        public Filter[] getFilters() {
            return iFilters;
        }

        /**
         *
         * @param aM
         */
        public void setMultiline(boolean aM) {
            iMultiline = aM;
            notifyROIChanged(ROIChangeEvent.CHG.Changed, null);
        }

        /**
         *
         * @return
         */
        public boolean isMultiline() {
            return iMultiline;
        }

        private Rectangle2D calcBounds() {

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
            return new Rectangle2D.Double(0, 0, width, height);
        }

        
        @Override
        public void update() {     
            iAnnotation.clear();

            for (Filter f : iFilters)
                iAnnotation.add(f.getMeasurement().format(f.get(iRoi)));     

            final Rectangle2D bnds = calcBounds();
            final double scaleX =  getManager().getView().screenToVirtual().getScaleX();       
            iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), getShape().getBounds2D().getY(),                                                                                        
                                            bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);                                                
        }

        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {               
            switch (anEvt.getChange()) {
                case Cleared: 
                    //commit suicide 
                    getManager().deleteOverlay(this);
                    break;
                case Moved: {//if not pinned move the same dX and dY
                    final double[] deltas = (double[])anEvt.getExtra();
                    ///logger.info(String.format("%f, %f",deltas[0], deltas[1]));
                    move(deltas[0], deltas[1]);
                    update();
                } break;
                case Changed:   
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
        private IOperation iOp;
    
        Active(IOperation aOp, ROIManager aRM) {
            super("ANNOTATION.ACTIVE", null, aRM);        
            iOp = aOp;     
            iAnnotation = iOp.getCompleteString();
            final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
            /**/

            iShape = new Rectangle2D.Double(0, 0, bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                            bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());   
        }   
        
        @Override
        public void ROIChanged(ROIChangeEvent anEvt) {              
            switch (anEvt.getChange()) {
                case Cleared: 
                   break;
                case Moved:                  
                case Changed:   
                default: //fall-through
                    update();                                
                    break;
            }        
        }

        @Override
        void paint(Graphics2D aGC, AffineTransform aTrans) {
            final Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();     
            aGC.setColor(iColor);

            aGC.drawString(iOp.getCompleteString(), (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));       
            aGC.draw(temp);                 
        }
        
        @Override
        public void update() {        
            iAnnotation = iOp.getCompleteString();

            final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
            /**/
            final double scaleX =  getManager().getView().screenToVirtual().getScaleX();       
            iShape = new Rectangle2D.Double(getShape().getBounds2D().getX(), getShape().getBounds2D().getY(),                                                                                        
                                            bnds.getWidth() * scaleX, bnds.getHeight() * scaleX);
        }
    }  
}
