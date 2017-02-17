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

import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.ISeriesProvider;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import com.ivli.roim.events.OverlayChangeEvent;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends ScreenObject implements ISeriesProvider {     
    private double iSpacing = 1.;    
    private Handle h[]={null,null};
    
    private Shape makeShape(Point2D aB, Point2D aE) {
        Path2D ret = new Path2D.Double();
        ret.moveTo(aB.getX(), aB.getY());
        ret.lineTo(aE.getX(), aE.getY());
        return ret;
    }
    
    Ruler(IImageView aV, Handle aB, Handle aE) {         
        super(aV, aV.getFrameNumber());  
        iBegin = new Tick(aB.getPos());
        iEnd = new Tick(aE.getPos());        
        iShape = makeShape(aB.getPos(), aE.getPos());
        
        aB.addChangeListener(this);
        aE.addChangeListener(this);
        this.addChangeListener(aB);
        this.addChangeListener(aE);
        
        h[0] = aB;
        h[1] = aE;
    }
          
    @Override
    void paint(AbstractPainter aP) {   
        //if (aP.getView() == getView()) {
            aP.paint(this);    
            aP.paint(this.iBegin);
            aP.paint(this.iEnd);
        //}
    } 
      
    @Override
    public boolean contains(Point2D aP) {       
        double deltaX = aP.getX() * .01; 
        double deltaY = aP.getY() * .01;                
        return getShape().intersects(new Rectangle2D.Double(aP.getX() - deltaX, aP.getY() - deltaY, 2*deltaX, 2*deltaY));
    }
    
    @Override
    public void move(double adX, double adY) {    
        //iBegin.move(adX, adY);
        //iEnd.move(adX, adY);     
        iShape = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);                            
        notify(OverlayChangeEvent.CODE.MOVED, new double[]{adX, adY});
    } 
   
    @Override
    void update(OverlayManager aM) {          
        iSpacing = aM.getImage().getPixelSpacing().getX();      
    }       
   
    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {
        switch(anEvt.getCode()) {
            case MOVED: {        
                if(anEvt.getObject().equals(h[0])) {
                    iBegin.move(((double[])anEvt.getExtra())[0], ((double[])anEvt.getExtra())[1]);
                    iShape = makeShape(iBegin.getPos(), iEnd.getPos());
                } else if(anEvt.getObject().equals(h[1])) {
                    iEnd.move(((double[])anEvt.getExtra())[0], ((double[])anEvt.getExtra())[1]);
                    iShape = makeShape(iBegin.getPos(), iEnd.getPos());
                } else {

                } 
            } break;
            default: break;
        }
    }

    @Override
    public ISeries getSeries(Measurement anId) {
        return new Scalar(Measurement.DISTANCE, iBegin.iPos.distance(iEnd.iPos) * iSpacing);
    }
    
    private static final Measurement []LIST_OF_MEASUREMENTS = {Measurement.DISTANCE};
    
    @Override
    public Measurement[] getListOfMeasurements() {
        return LIST_OF_MEASUREMENTS;
    }

    @Override
    public Measurement[] getDefaults() {
        return LIST_OF_MEASUREMENTS;
    }
    
    class Tick {
        Point2D iPos;
        Tick (Point2D aP) {iPos=aP;}
        Point2D getPos() {return iPos;}
        
        void move(double adX, double adY) {
            iPos.setLocation(iPos.getX() + adX, iPos.getY() + adY);
        }
      
        void paint(AbstractPainter aP) {   
            //if (aP.getView() == getView())
            aP.paint(this);    
        }     
    }
       
    Tick iBegin;
    Tick iEnd;
    
    private static final Logger LOG = LogManager.getLogger();
}
