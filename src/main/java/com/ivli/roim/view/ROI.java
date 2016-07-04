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
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.SeriesCollection;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.ROIChangeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ROI extends Overlay implements Overlay.IFlip, Overlay.IRotate {             
    private Color iColor;          
    private int iAreaInPixels;   
        
    transient SeriesCollection iSeries;               
    
    @Override
    int getCaps() {return MOVEABLE|SELECTABLE|CANFLIP|CANROTATE|CLONEABLE|HASMENU|PINNABLE;}
    
    public ROI(int anID) {
        this(anID, null , null, Colorer.getNextColor(ROI.class));                 
    }
    
    public ROI(int anID, String aName, Shape aS, Color aC) {
        super(anID, null == aName ?  String.format("ROI%d", anID):aName, aS);         
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);          
        iAreaInPixels = -1;
        iSeries = null;
    }
         
    protected void buildSeriesIfNeeded(OverlayManager aMgr) {
        if (null == iSeries)
            iSeries = CurveExtractor.extract(aMgr.getImage(), this, null);
    }
       
    public int getAreaInPixels() {
        if(iAreaInPixels < 0)
            calculateAreaInPixels();
        return iAreaInPixels;
    }
    
    /*
    public double getDensity(OverlayManager aMgr) {      
        return getSeries(aMgr, Measurement.DENSITY).get(aMgr.getFrameNumber());
    }
    
    public double getMinPixel(OverlayManager aMgr) {       
        return getSeries(aMgr, Measurement.MINPIXEL).get(aMgr.getFrameNumber());
    }
    
    public double getMaxPixel(OverlayManager aMgr) {
        return getSeries(aMgr, Measurement.MAXPIXEL).get(aMgr.getFrameNumber());
    }
    */
    
    public Series getSeries(OverlayManager aMgr, Measurement anId) {  
        buildSeriesIfNeeded(aMgr);
        return iSeries.get(anId);
    }
    
    public Color getColor() {
        return iColor;
    }
    
    public void setColor(Color aC) {
        Color old = iColor;
        iColor = aC;
        notify(OverlayChangeEvent.CODE.COLOR, old);
    }  
    
    @Override
    void paint(AbstractPainter aD) {
        aD.paint(this);     
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
    void update(OverlayManager aM) {    
        iAreaInPixels = -1;
        iSeries = null;   
        buildSeriesIfNeeded(aM);
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
