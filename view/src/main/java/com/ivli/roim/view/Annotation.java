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

import java.util.ArrayList;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Dialog;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.core.Filter;
import com.ivli.roim.core.ISeriesProvider;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.OverlayChangeListener;
import com.ivli.roim.calc.BaseFormatter;
import com.ivli.roim.core.IFrameProvider;

/**
 *
 * @author likhachev
 */
public abstract class Annotation extends ScreenObject implements OverlayChangeListener { 
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final boolean DEFAULT_MULTILINE = true;
    private static final boolean DEFAULT_HAS_BORDER = true;
     
    protected Overlay iOverlay;  
    protected final ArrayList<String> iAnnotation; 
 
    protected Color iColor     = DEFAULT_COLOR;
    protected boolean iMultiline = DEFAULT_MULTILINE;
    protected boolean iBordered  = DEFAULT_HAS_BORDER;
       
    protected Annotation(IImageView aView, int aN, Shape aShape, String aName) {
        super(aView, aN, aShape, aName);          
        iAnnotation = new ArrayList<>();
    }
   
    @Override
    public int getStyles() {
        return OVL_VISIBLE|OVL_MOVEABLE|OVL_SELECTABLE|OVL_PINNABLE|OVL_HAVE_MENU|OVL_HAVE_CONFIG; 
    }
    
    public void setMultiline(boolean aM) {
        iMultiline = aM;
        notify(OverlayChangeEvent.CODE.PRESENTATION, null);
    }

    public boolean isMultiline() {
        return iMultiline;
    }
    
    public void setBordered(boolean aB) {
        iBordered = aB;
        notify(OverlayChangeEvent.CODE.PRESENTATION, null);
    }

    public boolean hasBorder() {
        return iBordered;
    }
    
    public Color getColor() {return iColor;}
   
    @Override
    public void paint(IPainter aP) {   
        aP.paint(this);    
    } 

    public ArrayList<String> getText() {
        return iAnnotation;
    }    
    /**
     * 
     */
    public static class Static extends Annotation {        
        private ArrayList<Filter> iFilters;           
        private ISeriesProvider  iProvider;
                
        public Static(IImageView aV, ISeriesProvider aSP, Overlay aO, Color aC) {
            super(aV, aV.getFrameNumber(), aO.getShape(), "ANNOTATION::STATIC"); //NOI18N                                                              
            iOverlay = aO;   
            iProvider = aSP;
            iColor = aC;           
            iFilters = new ArrayList<>();
            for (Measurement m: iProvider.getDefaults())
                iFilters.add(Filter.getFilter(m.getName()));
        }
  
        public void setFilters(ArrayList<Filter> aF) {
            iFilters = aF;            
            notify(OverlayChangeEvent.CODE.PRESENTATION, null);
        }  
      
        public ArrayList<Filter> getFilters() {
            return iFilters;
        }
              
        @Override
        public void update(OverlayManager aM) {
            iAnnotation.clear();
            
            iFilters.forEach((f) -> {
                iAnnotation.add(f.getMeasurement().format(f.filter().eval(iProvider).get(getView().getFrameNumber()), true));
            });
        }
                         
        @Override
        public void OverlayChanged(OverlayChangeEvent anEvt) {   
            if (!anEvt.getObject().equals(iOverlay))
                return; //not interested in 
            switch (anEvt.getCode()) {
                case DELETED: 
                    //commit suicide 
                    ((OverlayManager)anEvt.getSource()).deleteObject(this);
                    break;
                case MOVED: {//if it is not pinned down then move it the same dX and dY                    
                    final double[] deltas = (double[])anEvt.getExtra(); 
                    OverlayManager mgr = (OverlayManager)anEvt.getSource();
                    mgr.moveObject(this, deltas[0], deltas[1]); //selfmovement kills border checkings so it can get off the screen                                                 
                } break;
                case RESHAPED: {                                        
                    OverlayManager mgr = (OverlayManager)anEvt.getSource();                    
                    this.update(mgr);
                } break;
                case COLOR_CHANGED:
                case NAME_CHANGED:                      
                default: //fall-through
                    //update(); break;
            }        
        }
        
        public String []getCustomMenu() {
            java.util.ArrayList<String> ret = new java.util.ArrayList<>();

            iFilters.forEach((f) -> {
                ret.add(f.getMeasurement().getName());
            });

            return (String[])ret.toArray();
        }
        
        @Override
        public boolean showConfigDialog(Object aNotUsed) {
            com.ivli.roim.controls.AnnotationPanel panel = new com.ivli.roim.controls.AnnotationPanel(this);
            javax.swing.JDialog dialog = new javax.swing.JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);

            dialog.setContentPane(panel);
            dialog.validate();
            dialog.pack();
            dialog.setResizable(false);
            dialog.setVisible(true);

            return false;
        }
         
        public ArrayList<String> getListOfMeasurements() {                   
            ArrayList<String> ret = new ArrayList<>();
            for(Measurement m:iProvider.getListOfMeasurements())
                ret.add(m.getName());
            return ret;
        }        
    }
          
    /**
     *
     */
    public static class Active extends Annotation {        
        private final IOperation iOp;
                   
        Active(IOperation anOp, Overlay aR, IImageView aV) {
            super(aV, null != aV ? aV.getFrameNumber():IFrameProvider.INVALID_FRAME, aR.getShape(), null);                    
            iOp = anOp;       
            iOverlay = aR;
        }   
            
        @Override
        public void update(OverlayManager aM){
            iAnnotation.clear();            
            iAnnotation.add(iOp.format(new BaseFormatter(getView().getFrameNumber())));
        }
        
        public boolean showDialog(Object a) {return false;}
                                 
        @Override
        public void OverlayChanged(OverlayChangeEvent anEvt) {              
            switch (anEvt.getCode()) {
                case DELETED: 
                    break;
                case MOVED: {  
                    final double[] deltas = (double[])anEvt.getExtra();                    
                    ((OverlayManager)anEvt.getSource()).moveObject(this, deltas[0], deltas[1]);
                    //update();
                } break;            
                case NAME_CHANGED:
                case COLOR_CHANGED:
                default: //fall-through
                    update((OverlayManager)anEvt.getSource());                                
                    break;
            }        
        }      
    }          
}
