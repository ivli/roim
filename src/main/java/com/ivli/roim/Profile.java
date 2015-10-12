
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javafx.scene.paint.Color;

/**
 *
 * @author likhachev
 */
public class Profile extends Overlay {
    
    transient ROIManager iMgr; 
    
    public Profile(java.awt.Shape aS, ROIManager aMgr) {
        super(aS, "RULER");
        iMgr = aMgr;
    }
    
    public int getCaps() {
        return MOVEABLE & SELECTABLE;
    }
    
    public void paint(Graphics2D aGC, AffineTransform aTrans) {

        //final java.awt.Rectangle cr = aGC.getClipBounds();
        final java.awt.Rectangle bn = aTrans.createTransformedShape(iShape).getBounds();  
        
        final java.awt.Color tmp = aGC.getColor();
        
        aGC.setColor(java.awt.Color.MAGENTA);
        aGC.drawLine(bn.x, bn.y, bn.x+bn.width, bn.y);                           
        
        aGC.setColor(java.awt.Color.RED);
        aGC.drawLine(bn.x, bn.y+bn.height, bn.x+bn.width, bn.y+bn.height);
      
        aGC.setColor(tmp);
    } 
   
    public void update(){}    
    public void move(double adX, double adY) {
        iShape  = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);  
    } 
       
   
    
    
}
