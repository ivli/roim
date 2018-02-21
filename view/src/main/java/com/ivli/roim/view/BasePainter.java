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

import com.ivli.roim.core.Filter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import com.ivli.roim.core.Histogram;
import com.ivli.roim.core.Measurement;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
/**
 *
 * @author likhachev
 */
public class BasePainter implements IPainter {    
    protected static final Color EMPHASIZED_COLOR = Color.RED;
    protected static final Color DEFAULT_LINK_COLOR = Color.RED;
    
    protected Graphics2D iGC;
    protected IImageView iView;    
    
   
    public BasePainter(Graphics2D aGC, IImageView aV) {
        iGC = aGC;
        iView = aV;
    }
    
    @Override
    public void paint(Overlay aO) {}
    
    @Override
    public void paint(ROI aO) {
        if (aO.isSelected()) {
            iGC.setColor(EMPHASIZED_COLOR);
            iGC.setStroke(new BasicStroke(2));        
        } else {
            iGC.setColor(aO.getColor());
        }
        
        iGC.draw(iView.virtualToScreen().createTransformedShape(aO.getShape()));             
    }
    
    @Override
    public void paint(Profile aO) { 
        final Rectangle bn = iView.virtualToScreen().createTransformedShape(aO.getShape()).getBounds();          
        final Color tmp = iGC.getColor();
        
        iGC.setColor(java.awt.Color.CYAN);
        iGC.drawLine(bn.x, bn.y, bn.x+bn.width, bn.y);                           
        
        iGC.setColor(java.awt.Color.RED);
        iGC.drawLine(bn.x, bn.y+bn.height, bn.x+bn.width, bn.y+bn.height);
      
        iGC.setColor(tmp);
        /**/
        if (aO.isShowHistogram()) {            
            Histogram h = aO.getHistogram();
            double min = h.min(); //Double.MAX_VALUE;
            double max = h.max(); //Double.MIN_VALUE;
            
            //final double range = getView().getMax() - getView().getMin();// maxV - minV; 

            Rectangle bounds = new Rectangle(0, 0, aO.getView().getImage().getWidth(), aO.getView().getImage().getHeight());

            final double scale = Math.min(aO.getShape().getBounds().getY(), bounds.getHeight());                                              
            Path2D.Double s = new Path2D.Double();        
            //int n = 0;
            //Integer set[]=h.values();
            s.moveTo(0, aO.getShape().getBounds().getY() - h.get(0) * scale);

            for (int n = 1; n < h.getNoOfBins(); ++n) 
                s.lineTo(n, aO.getShape().getBounds().getY() - h.get(n) * scale);

            iGC.setXORMode(Color.WHITE);             
            iGC.draw(iView.virtualToScreen().createTransformedShape(s));                
            iGC.setPaintMode(); //turn XOR mode off    
        }
    }
  
    static void drawRotate(Graphics2D g2d, double x, double y, double angleRadians, String text){         
        g2d.translate((float)x,(float)y);
        g2d.rotate(angleRadians);
        g2d.drawString(text,0,0);
        g2d.rotate(-angleRadians);
        g2d.translate(-(float)x,-(float)y);
    } 
    
    Point2D translate(Point2D aP) {
        Rectangle2D r = iView.virtualToScreen().createTransformedShape(new Rectangle2D.Double(aP.getX(), aP.getY(), 1, 1)).getBounds2D();
        return new Point2D.Double(r.getX(), r.getY());
    }
    
    @Override
    public void paint(Ruler aO) {
        final Shape line = iView.virtualToScreen().createTransformedShape(aO.getShape());                           
        iGC.setColor(Color.YELLOW);       
        iGC.draw(line); 
        
       // Rectangle2D rd = line.getBounds2D();
        Point2D p1 = translate(aO.iBegin.iPos);
        Point2D p2 = translate(aO.iEnd.iPos);
        
        Point2D beg; 
        Point2D end;
        
        if(p1.getX() < p2.getX()) {
            beg = p1; end = p2;
        } else {
            beg = p2; end = p1;
        }
        
        Measurement[] ms = aO.getDefaults();
        String text="";     
        
        for (Measurement m : ms)
            text += m.format(Filter.getFilter(m.getName()).filter().eval(aO).get(0), false);
                
        final double angle = Math.atan(-(beg.getY() - end.getY())/(end.getX() - beg.getX()));                 
        final double len = iGC.getFont().getStringBounds(text, iGC.getFontRenderContext()).getWidth();        
        final double x = beg.getX() + ((end.getX() - beg.getX()) / 2.0 - (len / 2.0) * Math.cos(angle));               
        final double y  = -((beg.getY() - end.getY()) * x + (beg.getX() * end.getY() - end.getX() * beg.getY())) / (end.getX() - beg.getX()); 
                
        drawRotate(iGC, x, y, angle, text);
    }   
    
    private static final boolean ROUND_TICKS = true;
    private static final boolean SOLID_TICKS = false;    
    private static final int     TICK_SIZE   = 8;    
    private static final Color   TICK_COLOR  = Color.YELLOW;

    @Override
    public void paint(Tick aO) {                        
        Rectangle2D temp = iView.virtualToScreen().createTransformedShape(new Rectangle2D.Double(aO.getPos().getX(), aO.getPos().getY(), 1, 1)).getBounds2D(); 
        
        Shape tick;
        if (!ROUND_TICKS)
            tick = new Rectangle2D.Double(temp.getX() - TICK_SIZE/2, temp.getY() - TICK_SIZE/2, TICK_SIZE, TICK_SIZE);
        else
            tick = new Ellipse2D.Double(temp.getX() - TICK_SIZE/2, temp.getY() - TICK_SIZE/2, TICK_SIZE, TICK_SIZE);
                                                               
        iGC.setColor(TICK_COLOR); 
        
        if(SOLID_TICKS)
            iGC.fill(tick);
        else   
            iGC.draw(tick); 
    }
        
    final Rectangle2D updateAnnotationShape(Annotation aO) {            
        double width  = 0;
        double height = 0;

        final java.awt.FontMetrics fm = iGC.getFontMetrics();

        for (String s : aO.getText()) {
            Rectangle2D b = fm.getStringBounds(s, iGC);        

            if (aO.isMultiline()) {
                width = Math.max(width, b.getWidth());
                height += b.getHeight();
            } else {
                width += b.getWidth();
                height = Math.max(height, b.getHeight());
            }
        }
                
        Rectangle2D rect = iView.virtualToScreen().createTransformedShape(aO.getShape()).getBounds2D();                              
        rect.setRect(rect.getMinX(), rect.getMinY(), width + 4, height + 4);
        aO.setShape(iView.screenToVirtual().createTransformedShape(rect));       
        return rect;
    }               
    
    @Override
    public void paint(Annotation aO) {          
        LOG.debug("annotation update");
        final Rectangle2D temp = updateAnnotationShape(aO);    
        iGC.setColor(aO.getColor());

        if (!aO.isMultiline()) {
            String str = new String();
            str = aO.getText().stream().map((s) -> s).reduce(str, String::concat);            
            iGC.drawString(str, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));                   
        } else {            
            final double stepY = temp.getHeight() / aO.getText().size();
            double posY = temp.getY() + stepY - 4.;
            for(String str : aO.getText()) {                               
                iGC.drawString(str, (int)temp.getX(), (int)posY);
                posY += stepY;
            }
        }
        iGC.draw(temp);                 
    }   
        
    @Override
    public void paint(Handle aO) {        
        Rectangle2D rect = iView.virtualToScreen().createTransformedShape(aO.getShape()).getBounds2D();                           
        iGC.setColor(Color.RED);       
        iGC.draw(rect); 
    }

    @Override
    public void paint(ActionItem aO) {
        
    }    
   
    Line2D computeLinkLine(Shape aF, Shape aT) {
        Shape s1 = iView.virtualToScreen().createTransformedShape(aF);
        Shape s2 = iView.virtualToScreen().createTransformedShape(aT);      
        Rectangle2D r1 = s1.getBounds2D();
        Rectangle2D r2 = s2.getBounds2D();
        Line2D temp = new Line2D.Double(r1.getCenterX(), r1.getCenterY(), r2.getCenterX(), r2.getCenterY());
        
        Point2D p1 = IntersectFinder.intersect(s1, temp);
        
        if (null != p1) {
            Point2D p2 = IntersectFinder.intersect(s2, temp);
            if (null != p2)
                return new Line2D.Double(p1, p2);
        }
        return null;
    }
    
    @Override
    public void paint(Link aO) {  
        Line2D line = computeLinkLine(aO.iFrom.getShape(), aO.iTo.getShape());

        if (null != line) {            
            iGC.setColor(aO.iFrom instanceof ROI ? ((ROI)aO.iFrom).getColor() : aO.iTo instanceof ROI ? ((ROI)aO.iFrom).getColor() : DEFAULT_LINK_COLOR );
            iGC.draw(line);
        }     
    }
    org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
