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
interface ROIManager {
    MEDImageBase getImage();
    MEDImageComponentBase getComponent(); 
    
    void attach(MEDImageComponentBase aPane);
    void clear();
    void update();
    void draw(Graphics2D aGC, AffineTransform aT);
    void createRoiFromShape(Shape aS);
    void cloneRoi(ROI aR);
    void moveRoi(Overlay aO, double adX, double adY);
    Overlay findOverlay(Point aP);
    boolean deleteOverlay(Overlay aR);
   // void extract(Extractor anExtractor);
    ListIterator<Overlay> getOverlaysList();   
}
