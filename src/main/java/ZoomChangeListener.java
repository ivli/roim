/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.events;

/**
 *
 * @author likhachev
 */
public interface ZoomChangeListener extends java.util.EventListener {
    void zoomChanged(ZoomChangeEvent aE);
}
