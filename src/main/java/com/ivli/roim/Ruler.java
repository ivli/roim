
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends Overlay {
   
    Ruler(Rectangle2D aR, ROIManager aM) {         
        super("RULER",  aR, aM);    
        Path2D.Double p = new Path2D.Double();             
        p.moveTo(aR.getX(), aR.getY());
        p.lineTo(aR.getX() + aR.getWidth(), aR.getY() + aR.getHeight());
        iShape = p;
    }
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        final Shape rect = aTrans.createTransformedShape(iShape);                           
        aGC.setColor(Color.RED);       
        aGC.draw(rect);
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU;
    }
    
    @Override
    void update() {        
        Rectangle rect = getShape().getBounds();
        double diag = Math.sqrt(rect.height * rect.height + rect.width * rect.width);
        double angle = Math.asin(rect.width / diag);
        logger.info(String.format("%f, %f", diag, angle));
    }       
    
    private static final Logger logger = LogManager.getLogger(Ruler.class);
}
