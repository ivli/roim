
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends Overlay {
   
    Ruler(Shape aR, ROIManager aM) {         
        super("RULER",  aR, aM);            
    }
          
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        final Shape rect = aTrans.createTransformedShape(iShape);                           
        aGC.setColor(Color.YELLOW);       
        aGC.draw(rect);
        
        Point2D.Double a = null, b = null;
        
        PathIterator pi = iShape.getPathIterator(null);
        
        while(!pi.isDone()) {
            double [] temp = new double [2];//{.0;.0};
            switch(pi.currentSegment(temp)){
                case PathIterator.SEG_MOVETO:
                    a = new Point2D.Double(temp[0], temp[1]); break;
                case PathIterator.SEG_LINETO:
                    b = new Point2D.Double(temp[0], temp[1]); break;
                default:
                    return;
           }   
           pi.next();
        }
        
        if (null == a || null == b)
            return; 
        
        double d = a.distance(b) * getManager().getImage().getPixelSpacing().getX();
        logger.info(String.format("distance is: %f", d));
        
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU | PINNABLE;
    }
    
    @Override
    void update() {                
    }       
    
    private static final Logger logger = LogManager.getLogger(Ruler.class);
}
