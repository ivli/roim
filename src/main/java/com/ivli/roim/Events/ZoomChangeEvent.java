
package com.ivli.roim.Events;

/**
 *
 * @author likhachev
 */
public final class ZoomChangeEvent extends java.util.EventObject {
    final double iZoom;
    
    public ZoomChangeEvent(Object aO, double aZoom) {
        super(aO);
        iZoom = aZoom;
    }
    
    public double getZoom() {
        return iZoom;
    }
}
