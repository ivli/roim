
package com.ivli.roim.Events;

/**
 *
 * @author likhachev
 */
public final class ZoomChangeEvent extends java.util.EventObject {
    final double iZoom;
    
    public ZoomChangeEvent(Object aO, double aX, double aY) {
        super(aO);
        iZoom = aX;
    }
    
    public double getZoom() {
        return iZoom;
    }
}
