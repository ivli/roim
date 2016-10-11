
package com.ivli.roim.view;

import com.ivli.roim.calc.AbstractFormatter;
import com.ivli.roim.calc.IOperand;
import com.ivli.roim.calc.IOperation;
import com.ivli.roim.calc.Operand;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import com.ivli.roim.core.Uid;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class Ruler extends ScreenObject {
    private double iDistance;
    
    Ruler(Shape aR, IImageView aV) {         
        super("RULER",  aR, aV);            
    }
          
    @Override
    void paint(AbstractPainter aP) {   
        if (aP.getView() == getView())
            aP.paint(this);    
    } 
     
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU | PINNABLE;
    }
    
    @Override
    void update(OverlayManager aM) {  
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
        
        iDistance = a.distance(b) * aM.getImage().getPixelSpacing().getX();
        LOG.debug("distance is: {}", iDistance);           
    }       
    
    IOperation getOperation() {                
        return new IOperation() {
            Ruler iR = Ruler.this;
            @Override
            public IOperand value() {                
                return new Operand(new Scalar(Measurement.DISTANCE, iR.iDistance));
            }
            @Override
            public String format(AbstractFormatter aF) {
                return aF.format(value());
            }            
        };
    }
    
    private static final Logger LOG = LogManager.getLogger();
}
