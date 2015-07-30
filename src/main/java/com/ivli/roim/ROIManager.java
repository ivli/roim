/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ListIterator;

/**
 *
 * @author likhachev
 */
public abstract class ROIManager {
    abstract MEDImageBase getImage();
    abstract void attach(MEDImageComponentBase aPane);
    abstract void clear();
    abstract void update();
    abstract void draw(Graphics2D aGC, AffineTransform aT);
    abstract void createRoiFromShape(Shape aS);
    abstract void cloneRoi(ROI aR);
    abstract void moveRoi(Overlay aO, double adX, double adY);
    abstract Overlay findOverlay(Point aP);
    abstract boolean deleteOverlay(Overlay aR);
   // void extract(Extractor anExtractor);
    abstract ListIterator<Overlay> getOverlaysList();
    
    MEDImageComponentBase getComponent() {
        return iComponent;
    }
    
    MEDImageComponentBase iComponent;
}
