
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends Overlay implements Overlay.IRotate {
    
    Ruler(Rectangle aS, ROIManager aM) { 
        super("RULER", aS, aM);              
    }
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        final Rectangle rect = aTrans.createTransformedShape(iShape).getBounds();                           
        aGC.setColor(Color.RED);
        aGC.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y + rect.height);  
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | CANROTATE | HASMENU;
    }
    
    @Override
    void update() {        
        Rectangle rect = getShape().getBounds();
        double diag = Math.sqrt(rect.height * rect.height + rect.width * rect.width);
        double angle = Math.asin(rect.width / diag);
        logger.info(String.format("%f, %f", diag, angle));
    }       
    
    public void rotate(double aV) {        
        final Rectangle rect = getShape().getBounds();
        AffineTransform tx = new AffineTransform();        
        tx.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);              
        iShape = tx.createTransformedShape(iShape);
    }      
    
    private static final Logger logger = LogManager.getLogger(Ruler.class);
}
