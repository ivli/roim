/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.Events;

import java.util.EventObject;

/**
 *
 * @author likhachev
 */
public class ZoomChangeEvent extends EventObject {
    double iZoom;
    
    ZoomChangeEvent(Object aO, double aZoom) {
        super(aO);
        iZoom = aZoom;
    }
}
