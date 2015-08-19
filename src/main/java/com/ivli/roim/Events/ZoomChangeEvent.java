/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.Events;

/**
 *
 * @author likhachev
 */
public class ZoomChangeEvent extends java.util.EventObject {
    double iZoom;
    
    public ZoomChangeEvent(Object aO, double aZoom) {
        super(aO);
        iZoom = aZoom;
    }
    
    public double getZoom() {
        return iZoom;
    }
}
