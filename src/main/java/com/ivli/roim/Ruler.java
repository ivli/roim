
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends Overlay implements Overlay.IRotate {
    Point2D iStart;
    Point2D iFinish;
    
    Ruler(Point2D aS, Point2D aF, ROIManager aM) {         
        super("RULER", new Rectangle()/*(Point)aS, new Dimension(aF.x-aS.x, aF.y-aS.y))*/, aM);      
        iStart  = aS;
        iFinish = aF;
    }
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        final Rectangle rect = aTrans.createTransformedShape(iShape).getBounds();                           
        aGC.setColor(Color.RED);
        aGC.drawLine((int)iStart.getX(), (int)iStart.getY(), (int)iFinish.getX(), (int)iFinish.getY());  
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
