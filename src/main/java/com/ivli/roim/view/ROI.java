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
import java.awt.geom.AffineTransform;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import com.ivli.roim.core.SeriesCollection;
import com.ivli.roim.core.Uid;
import com.ivli.roim.events.OverlayChangeEvent;
import java.util.ArrayList;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

public class ROI extends Overlay implements ISeriesProvider, Overlay.ICanFlip, Overlay.ICanRotate, Overlay.IHaveCustomMenu {             
    private Color iColor;          
    private int iAreaInPixels;   
        
    transient SeriesCollection iSeries;               
       
    public ROI(Uid anID, Shape aS, String aName,  Color aC) {
        super(anID, aS, null != aName ? aName : "ROI" + anID.toString());         
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);          
        iAreaInPixels = -1;
        iSeries = null;
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
    void paint(AbstractPainter aD) {
        aD.paint(this);     
    }
  
    @Override
    void update(OverlayManager aM) {    
        final Rectangle bnds = getShape().getBounds();
        
        iAreaInPixels = 0;
        
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (getShape().contains(i, j)) 
                  ++iAreaInPixels;          
        iSeries = CurveExtractor.extract(aM.getImage(), this, null);
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

    private static final String KCommandRoiPin  = "COMMAND_ROI_OPERATIONS_PIN"; // NOI18N    
    private static final String KCommandRoiFlipVert  = "COMMAND_ROI_OPERATIONS_FLIP_V"; // NOI18N
    private static final String KCommandRoiFlipHorz  = "COMMAND_ROI_OPERATIONS_FLIP_H"; // NOI18N
    private static final String KCommandRoiRotate90CW   = "COMMAND_ROI_OPERATIONS_ROTATE_90_CW"; // NOI18N
    private static final String KCommandRoiRotate90CCW  = "COMMAND_ROI_OPERATIONS_ROTATE_90_CCW"; // NOI18N
    private static final String KCommandRoiConvertToIso = "COMMAND_ROI_OPERATIONS_CONVERT_TO_ISO"; // NOI18N
   
    @Override
    public ArrayList<JMenuItem> makeCustomMenu(Object aVoidStar) {
        ArrayList<JMenuItem> mnu = new ArrayList<>();
              
        if (isPinnable()) {
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.PIN"));            
            mi.setState(isPinned());
            
            mi.setActionCommand(KCommandRoiPin); 
            mnu.add(mi);
        }

        if (this instanceof Overlay.ICanFlip) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_HORZ"));           
            mi.setActionCommand(KCommandRoiFlipHorz);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_VERT"));           
            mi.setActionCommand(KCommandRoiFlipVert);
            mnu.add(mi);         
        }
        
        if (this instanceof Overlay.ICanRotate) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CW"));            
            mi.setActionCommand(KCommandRoiRotate90CW);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CCW"));            
            mi.setActionCommand(KCommandRoiRotate90CCW);
            mnu.add(mi);
        }
         
        if (this instanceof Overlay.ICanIso) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.CONVERT_TO_ISO"));            
            mi.setActionCommand(KCommandRoiConvertToIso);
            mnu.add(mi);           
        }    
        return mnu;
    }

    @Override
    public boolean handleCustomCommand(String aCommand) {
        switch(aCommand) {
        case KCommandRoiPin: 
                pin(!isPinned());
                break;
            
            case KCommandRoiFlipHorz:
                ((Overlay.ICanFlip)this).flip(false);
                
                break;
            case KCommandRoiFlipVert:
                ((Overlay.ICanFlip)this).flip(true);
                
                break;
            case KCommandRoiRotate90CW:
                ((Overlay.ICanRotate)this).rotate(90);
               
                break;
            case KCommandRoiRotate90CCW:
                ((Overlay.ICanRotate)this).rotate(-90);
               
                break;
            case KCommandRoiConvertToIso:
                ((Overlay.ICanIso)this).convertToIso(0);
                
                ;break;
        }
        return true;
    }

    @Override
    public boolean isSelectable() {
         return true;
    }

    @Override
    public boolean isMovable() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    public boolean isCloneable() {
        return true;
    }

    @Override
    public boolean isPinnable() {
        return true;
    }
}
