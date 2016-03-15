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
import com.ivli.roim.events.ROIChangeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ROI extends Overlay implements Overlay.IFlip, Overlay.IRotate {          
    private final static boolean DRAW_PROFILES = false;
    private final static int EMPHASIZED_STROKE = 20;
    private final static Color EMPHASIZED_COLOR = Color.RED;
    
    private Color iColor;          
    private int iAreaInPixels;   
    
    private transient SeriesCollection iSeries;               
    
    @Override
    int getCaps() {return MOVEABLE|SELECTABLE|CANFLIP|CANROTATE|CLONEABLE|HASMENU|PINNABLE;}
    
    ROI(String aName, Shape aS, ROIManager aMgr, Color aC) {
        super(aName, aS, aMgr); 
        
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);
          
        iAreaInPixels = -1;///calculateAreaInPixels();
        iSeries = null;//new SeriesCollection();//CurveExtractor.extract(getManager().getImage(), this, getManager().getOffsetVector());
    }
        
    private void boildSeriesIfNeeded() {
        if (null == iSeries)
            iSeries = CurveExtractor.extract(getManager().getImage(), this, getManager().getOffsetVector());
    }
    
    public int getAreaInPixels() {
        if(-1 == iAreaInPixels)///
            calculateAreaInPixels();
        return iAreaInPixels;
    }
    
    public double getDensity() {
        boildSeriesIfNeeded();
        return getSeries(Measurement.DENSITY).get(getManager().getView().getCurrent());
    }
    
    public double getMinPixel() {
        boildSeriesIfNeeded();
        return getSeries(Measurement.MINPIXEL).get(getManager().getView().getCurrent());
    }
    
    public double getMaxPixel() {
        boildSeriesIfNeeded();
        return getSeries(Measurement.MAXPIXEL).get(getManager().getView().getCurrent());
    }
    
    public Series getSeries(Measurement anId) {  
        boildSeriesIfNeeded();
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
   
    void drawProfiles(Graphics2D aGC, AffineTransform aTrans) {        
        final Rectangle bounds = iShape.getBounds();
        
        final int profileX[] = new int[bounds.width];;
        final int profileY[] = new int[bounds.height];;
        
        getManager().getView().getImage().extract(new com.ivli.roim.core.Extractor(){            
            public void apply(com.ivli.roim.core.ImageFrame aR){
            //double temp[] = new double [aR.getNumBands()];
            
            for (int i = 0 ; i < bounds.width; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.height; ++j)
                    if (iShape.contains(new Point(i + bounds.x, j))) 
                        profileX[i] += aR.getPixel(i + bounds.x, j);
            
            for (int i = 0; i < bounds.height; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.width; ++j)
                    if (iShape.contains(new Point(i + bounds.x, j ))) 
                        profileY[i] += aR.getPixel(i + bounds.x, j);
              
            }
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
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        if (iEmphasized) {
            aGC.setColor(EMPHASIZED_COLOR);
            aGC.setStroke(new BasicStroke(2));        
        } else {
            aGC.setColor(iColor);
        }
        
        aGC.draw(aTrans.createTransformedShape(getShape()));       
        
        if (DRAW_PROFILES)
            drawProfiles(aGC, aTrans);
    }
        
    private void calculateAreaInPixels() {
        final java.awt.Rectangle bnds = getShape().getBounds();
        int AreaInPixels = 0;

        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                if (getShape().contains(i, j)) 
                  ++AreaInPixels;
        
        iAreaInPixels = AreaInPixels;
    }                 
        
    @Override
    void update() {    
        iAreaInPixels = -1;
        iSeries = null;//new SeriesCollection();            
    }
    
    @Override
    public void flip(boolean aV) {                        
        Rectangle r = iShape.getBounds();
        AffineTransform tx;
        
        if (aV) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -getShape().getBounds().getHeight());
        } else {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-getShape().getBounds().getWidth(), 0);       
        }        
        
        iShape = tx.createTransformedShape(iShape);
        
        r = iShape.getBounds();
    }
    
    @Override
    public void rotate(double aV) {        
        final Rectangle rect = getShape().getBounds();
        AffineTransform tx = new AffineTransform();        
        tx.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);              
        iShape = tx.createTransformedShape(iShape);
    }      
    
    byte [] treshold(int aValue, int [] aData, int aW, int aH) {
        
        byte [] ret = new byte [aW*aH];
        
        for (int i = 0; i < aW*aH; ++i)            
            ret [i] = (byte) (aData[i] > aValue ? 1:0);
        
        for (int i = 1; i < aW-1; ++i)
            for (int j = 1; j < aH-1; ++j) {
                if (1 < ret[aW*i+j] && ret[aW*i+(j+1)] + ret[aW*i+(j-1)] + ret[aW*(i+1)+j] + ret[aW*(i-1)+j] >=3)
                    ret[i*aW+j] = 1;     
            }

        return ret;
    }
      /*  
    public void isolevel(int aTolerance) {
        Rectangle bounds = iShape.getBounds();
        
        Raster r = iSrc.getBufferedImage().getData();
        int [] data = new int [bounds.width*bounds.height];
        data = r.getPixels(bounds.x, bounds.y, bounds.width, bounds.height, data);
                
        byte [] tr = treshold((int)(iStats.iMax-iStats.iMin)/2, data, bounds.width, bounds.height);
        
        MarchingSquares ms = new MarchingSquares(bounds.width, bounds.height, tr);
        
        Path p = ms.identifyPerimeter();
        
        List<Direction> dir = p.getDirections();
       
        Point2D.Double pt = new Point2D.Double(p.getOriginX()+bounds.x, p.getOriginY()+bounds.y);
        
        Path2D.Double np = new Path2D.Double();
        
        np.moveTo(pt.x, pt.y);
        
        for (Iterator<Direction> it = dir.iterator(); it.hasNext();) {    
            switch(it.next()) {
                case N: pt.y-=1; break; 
                case S: pt.y+=1; break;
                case E: pt.x+=1; break;
                case W: pt.x-=1; break;
            }
            np.lineTo(pt.x, pt.y);
        }
        np.closePath();
        ///AffineTransform tr = AffineTransform.getTranslateInstance(-bounds.x, -bounds.y);
        iShape = np;
    }
    */
    private static final Logger logger = LogManager.getLogger(ROI.class);
}
