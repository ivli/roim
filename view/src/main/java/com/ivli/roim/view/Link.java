/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.view;

//import static com.ivli.roim.view.Overlay.OVL_VISIBLE;

import com.ivli.roim.events.OverlayChangeEvent;


/**
 *
 * @author likhachev
 */

class Link extends ScreenObject {
    final public Overlay     iFrom; 
    final public ScreenObject  iTo;

    Link(IImageView aV, Overlay aFrom, ScreenObject aTo) {
        super(aV);
        iFrom = aFrom;
        iTo   = aTo;
    }

    @Override
    public int getStyles() {
        return OVL_VISIBLE;
    }

    @Override
    public void update(OverlayManager aM) {            
    }

    @Override
    public void paint(IPainter aP) {
        aP.paint(this);
    }

    @Override
    public void OverlayChanged(OverlayChangeEvent anEvt) {              
        switch (anEvt.getCode()) {
            case DELETED: 
                break;
            case MOVED: {  
                //final double[] deltas = (double[])anEvt.getExtra();                    
                //((OverlayManager)anEvt.getSource()).moveObject(this, deltas[0], deltas[1]);
                //update();
            } break;            
            case NAME_CHANGED:
            case COLOR_CHANGED:
            default: //fall-through
                //update((OverlayManager)anEvt.getSource());                                
                break;
        }        
    }      


    org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
