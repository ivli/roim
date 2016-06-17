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

import com.ivli.roim.core.Extractor;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.awt.Rectangle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class Profile extends ScreenObject {   
    public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
    public static final Color VIOLET     = new Color(0.93333334f, 0.50980395f, 0.93333334f);
    
    private boolean iShow = true;
    
    private boolean iNormalize = false;
    private double[]  iHist;
    
    public Profile(Rectangle2D aS, ROIManager aMgr) {
        super("PROFILE", aS, aMgr); //NOI18N 
        makeHistogram();
    }
    
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU;
    }
    
    /*    */
    @Override
    public void paint(AbstractPainter aP) {
        aP.paint(this);
    } 
 
    @Override
    public void update() {
        makeHistogram();        
    }            
    
    @Override
    public void move(double adX, double adY) {             
        final Rectangle2D r = iShape.getBounds2D();
        
        Shape temp = AffineTransform.getTranslateInstance(.0, adY).createTransformedShape(
                                 new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), Math.max(1.0, r.getHeight() + adX))
                              );  
        
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getImage().getWidth(), getManager().getImage().getHeight());
        
        if (bounds.contains(temp.getBounds())) {
            iShape = temp;
            update();
        }
    }  
    
    private void makeHistogram() {
        final Rectangle bounds = iShape.getBounds();

        getManager().getView().getFrame().extract(new Extractor() {
            
        public void apply(com.ivli.roim.core.ImageFrame aR) {
                
            iHist = new double[bounds.width];
            
            for (int i = 0; i < bounds.width; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.height; ++j)
                    iHist[i] += aR.getPixel(i, j);
        }});
    
    }
    
    private void drawHistogram(Graphics2D aGC, AffineTransform aTrans) {        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (double d : iHist) {
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
      
        final double range =  getManager().getView().getFrame().getRange().range();// maxV - minV; 
        
        Rectangle bounds = new Rectangle(0, 0, getManager().getImage().getWidth(), getManager().getImage().getHeight());
                       
        final double scale = Math.min(iShape.getBounds().getY()/(4*range), bounds.getHeight()/(4*range));                                              
        Path2D.Double s = new Path2D.Double();        
        //int n = 0;
        s.moveTo(0, iShape.getBounds().getY() - iHist[0] * scale);
        
        for (int n = 1; n < iHist.length; ++n) 
            s.lineTo(n, iShape.getBounds().getY() - iHist[n] * scale);
               
        aGC.setXORMode(Color.WHITE);             
        aGC.draw(aTrans.createTransformedShape(s));                
        aGC.setPaintMode(); //turn XOR mode off
    }
    
    public boolean normalize() {
        return iNormalize = !iNormalize; 
    }
   
    public boolean isShowHistogram() {
        return iShow; 
    }
    
    public void showHistogram(boolean aS) {
         iShow = aS; 
    }
    
    public double[] getHistogram() {
         return iHist;
    }
    
    
    private static final Logger LOG = LogManager.getLogger();
}
