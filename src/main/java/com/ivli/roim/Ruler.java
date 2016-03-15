
package com.ivli.roim;

import com.ivli.roim.calc.IOperand;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.calc.Operand;
import java.awt.Graphics2D;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends Overlay {
    private double iDistance;
    
    Ruler(Shape aR, ROIManager aM) {         
        super("RULER",  aR, aM);            
    }
          
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {        
        final Shape rect = aTrans.createTransformedShape(iShape);                           
        aGC.setColor(Color.YELLOW);       
        aGC.draw(rect);   
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU | PINNABLE;
    }
    
    @Override
    void update() {  
        Point2D.Double a = null, b = null;
        
        PathIterator pi = iShape.getPathIterator(null);
        
        while(!pi.isDone()) {
            double [] temp = new double [2];
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
        
        iDistance = a.distance(b) * getManager().getImage().getPixelSpacing().getX();
        logger.info(String.format("distance is: %f", iDistance));           
    }       
    
    IOperation getOperation() {
        update();        
        return new IOperation() {
            Ruler iR = Ruler.this;
            public IOperand value() {
                return new Operand(iR.iDistance);
            }
            public String getString() {
                return String.format("%.1f", iR.iDistance);
            }
            
            public String getCompleteString() {
                return String.format("%.1f mm", iR.iDistance); 
            }            
        };
    }
    
    private static final Logger logger = LogManager.getLogger(Ruler.class);
}
