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


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.BasicStroke;

import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.SeriesCollection;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.events.ROIChangeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ROI extends OverlayBase implements OverlayBase.IFlip, OverlayBase.IRotate {          
    private final static boolean DRAW_PROFILES = false;
    private final static int EMPHASIZED_STROKE = 20;
    private final static Color EMPHASIZED_COLOR = Color.RED;
    
    private Color iColor;          
    private int iAreaInPixels;   
    
    transient SeriesCollection iSeries;               
    
    @Override
    int getCaps() {return MOVEABLE|SELECTABLE|CANFLIP|CANROTATE|CLONEABLE|HASMENU|PINNABLE;}
    
    ROI(String aName, Shape aS, ROIManager aMgr, Color aC) {
        super(aName, aS, aMgr);         
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);          
        iAreaInPixels = -1;
        iSeries = null;
    }
        
    void buildSeriesIfNeeded() {
        if (null == iSeries)
            iSeries = CurveExtractor.extract(getManager().getImage(), this, getManager().getOffsetVector());
    }
    
    public int getAreaInPixels() {
        if(iAreaInPixels < 0)///
            calculateAreaInPixels();
        return iAreaInPixels;
    }
    
    public double getDensity() {
        buildSeriesIfNeeded();
        return getSeries(Measurement.DENSITY).get(getManager().getView().getFrameNumber());
    }
    
    public double getMinPixel() {
        buildSeriesIfNeeded();
        return getSeries(Measurement.MINPIXEL).get(getManager().getView().getFrameNumber());
    }
    
    public double getMaxPixel() {
        buildSeriesIfNeeded();
        return getSeries(Measurement.MAXPIXEL).get(getManager().getView().getFrameNumber());
    }
    
    public Series getSeries(Measurement anId) {  
        buildSeriesIfNeeded();
        return iSeries.get(anId);
    }
    
    public Color getColor() {
        return iColor;
    }
    
    public void setColor(Color aC) {
        Color old = iColor;
        iColor = aC;
        notifyROIChanged(ROIChangeEvent.ROICHANGEDCOLOR, old);
    }
    
    @Override
    public void setName(String aName) {
        String old = getName();
        super.setName(aName);
        notifyROIChanged(ROIChangeEvent.ROICHANGEDNAME, old);         
    }
    
   /*
    void drawProfiles(Graphics2D aGC, AffineTransform aTrans) {        
        final Rectangle bounds = iShape.getBounds();
        
        final int profileX[] = new int[bounds.width];;
        final int profileY[] = new int[bounds.height];;
        
        getManager().getView().getFrame().extract((ImageFrame aF) -> {                        
            for (int i = 0 ; i < bounds.width; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.height; ++j)
                    if (iShape.contains(new Point(i + bounds.x, j))) 
                        profileX[i] += aF.getPixel(i + bounds.x, j);
            
            for (int i = 0; i < bounds.height; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.width; ++j)
                    if (iShape.contains(new Point(i + bounds.x, j ))) 
                        profileY[i] += aF.getPixel(i + bounds.x, j);
                          
        });
            
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double d : profileX) {
            min = Math.min(min, d);
            max = Math.max(max, d);
        }

        double range = max - min;
        double scale = Math.min(iShape.getBounds().getY() / (range), getManager().getHeight() / (4*range));

        Path2D.Double xpath = new Path2D.Double();

        xpath.moveTo(iShape.getBounds().getX(), iShape.getBounds().getY() - profileX[0] * scale);

        for (int n = 1; n < profileX.length; ++n) 
            xpath.lineTo(iShape.getBounds().getX() + n, iShape.getBounds().getY() - profileX[n] * scale);

        Path2D.Double ypath = new Path2D.Double();                   

        for (double d : profileY) {
            min = Math.min(min, d);
            max = Math.max(max, d);
        }

        range = max - min;

        scale = Math.min(iShape.getBounds().getX() / (range), 
                                  getManager().getWidth() / (4*range) );

        ypath.moveTo(iShape.getBounds().getX() + iShape.getBounds().getWidth() + profileY[0] * scale , iShape.getBounds().getY());

        for (int n = 1; n < profileY.length; ++n) 
            ypath.lineTo(iShape.getBounds().getX() + iShape.getBounds().getWidth() + profileY[n] * scale , iShape.getBounds().getY() + n);


        aGC.setXORMode(Color.WHITE);   
        aGC.draw(aTrans.createTransformedShape(xpath));
        aGC.draw(aTrans.createTransformedShape(ypath));
        aGC.setPaintMode(); //turn XOR mode off

    }
   */
    
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        if (iEmphasized) {
            aGC.setColor(EMPHASIZED_COLOR);
            aGC.setStroke(new BasicStroke(2));        
        } else {
            aGC.setColor(iColor);
        }
        
        aGC.draw(aTrans.createTransformedShape(getShape()));       
        /*
        if (DRAW_PROFILES)
            drawProfiles(aGC, aTrans);
        */
    }
        
    private void calculateAreaInPixels() {
        final Rectangle bnds = getShape().getBounds();
        
        iAreaInPixels = 0;
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (getShape().contains(i, j)) 
                  ++iAreaInPixels;  
    }                 
        
    @Override
    void update() {    
        iAreaInPixels = -1;
        iSeries = null;            
    }
    
    @Override
    public void flip(boolean aV) {                        
        AffineTransform tx;
        
        if (aV) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -getShape().getBounds().getHeight());
        } else {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-getShape().getBounds().getWidth(), 0);       
        }        
        
        iShape = tx.createTransformedShape(iShape);                
    }
    
    @Override
    public void rotate(double aV) {        
        final Rectangle rect = getShape().getBounds();
        AffineTransform tx = new AffineTransform();        
        tx.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);              
        iShape = tx.createTransformedShape(iShape);
    }      
       
    private static final Logger LOG = LogManager.getLogger();
}
