
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
public class Ruler extends Overlay {
    
    Ruler(Rectangle aS, ROIManager aM) { 
        super("RULER", aS, aM);              
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
    int getCaps() {
        return MOVEABLE|SELECTABLE;
    }
    
    @Override
    void update() {
        
    }
       
}
