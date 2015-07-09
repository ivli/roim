/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
/**
 *
 * @author likhachev
 */

interface MEDImageComponentBase {
    AffineTransform screenToVirtual();
    AffineTransform virtualToScreen();
    AffineTransform getZoom();
    
    Rectangle  getBounds();
    MEDImageBase   getImage();
}
