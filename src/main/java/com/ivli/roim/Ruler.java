
package com.ivli.roim;


import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 *
 * @author likhachev
 */
public class Ruler extends ROIBase {
    
    Ruler(Rectangle aS, ROIManager aM) { 
        super(aS, aM, "RULER");              
    }
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(aTrans, hts);
                    
        //BufferedImage src = iWM.transform(true != SUMMED_FRAME_PANE ? iImg.getBufferedImage():iImg.makeCompositeFrame(0, -1), null);
        
        //java.awt.Image iBuf = z.filter(iPicture, null);   
        Rectangle r = getShape().getBounds();
        //aTrans.
        AffineTransform trans = AffineTransform.getTranslateInstance(aTrans.getTranslateX(), aTrans.getTranslateY());
        
        //aGC.drawImage(iBuf, 0, 0, r.width, r.height, null);
        //aGC.drawImage(iBuf, trans, null);        
    } 
     
    @Override
    void update() {
        
    }
     
    @Override
    void move(double adX, double adY) {
         
        AffineTransform trans = AffineTransform.getTranslateInstance(adX, adY);    
        iShape = trans.createTransformedShape(iShape);
        
       // AffineTransform trans = AffineTransform.getTranslateInstance(adX, adY);    
       // iShape = trans.createTransformedShape(iShape);
        //iOrigin.x += adX;
        //iOrigin.y += adY;
    }
    
}
