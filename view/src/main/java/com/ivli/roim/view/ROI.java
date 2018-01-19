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

import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.ISeriesProvider;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Rectangle;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import com.ivli.roim.core.SeriesCollection;
import com.ivli.roim.events.OverlayChangeEvent;

public class ROI extends Overlay implements ISeriesProvider {             
    private Color iColor;          
    private int iAreaInPixels;   
        
    transient SeriesCollection iSeries;               
   
    public ROI(Shape aS, String aName, Color aC) {
        super(aS, aName);
        iColor = aC;          
        iAreaInPixels = -1;
        iSeries = null;
    }
     
    public int getStyles() {
        return OVL_VISIBLE|OVL_MOVEABLE|OVL_SELECTABLE|OVL_CLONEABLE|OVL_PINNABLE|OVL_HAVE_MENU|OVL_CAN_FLIP|OVL_CAN_ROTATE;      
    }
    
    public int getAreaInPixels() {        
        return iAreaInPixels;
    }      
    
    @Override
    public ISeries getSeries(Measurement anId) {         
        if (anId == Measurement.AREAINPIXELS)  //special case this actually is a scalar
            return new Scalar(Measurement.AREAINPIXELS, getAreaInPixels());
        else if (anId == Measurement.AREAINLOCALUNITS) // TODO:
            return new Scalar(Measurement.AREAINPIXELS, getAreaInPixels());            
        else
            return iSeries.get(anId);     
    }
    
    private static final Measurement [] LIST_OF_MEASUREMENTS = {Measurement.DENSITY,            
                                                                Measurement.AREAINPIXELS,
                                                                Measurement.MINPIXEL, 
                                                                Measurement.MAXPIXEL
                                                               };
    private static final Measurement [] DEFAULTS = {Measurement.DENSITY,            
                                                    Measurement.AREAINPIXELS,
                                                    };
    @Override
    public Measurement[] getListOfMeasurements() {            
        return LIST_OF_MEASUREMENTS;
    }
    
    @Override
    public Measurement[] getDefaults() {
        return DEFAULTS;                                                               
    }
    
    public Color getColor() {
        return iColor;
    }
    
    public void setColor(Color aC) {
        Color old = iColor;
        iColor = aC;
        notify(OverlayChangeEvent.CODE.COLOR_CHANGED, old);
    }  
    
    @Override
    public void paint(AbstractPainter aD) {
        aD.paint(this);     
    }
  
    @Override
    public void update(OverlayManager aM) {    
        final Rectangle bnds = getShape().getBounds();
        
        iAreaInPixels = 0;
        
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (getShape().contains(i, j)) 
                  ++iAreaInPixels;          
        iSeries = CurveExtractor.extract(aM.getImage(), this, null);
    } 
}
