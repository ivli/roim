
package com.ivli.roim;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.awt.Rectangle;
import java.awt.image.Raster;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class Profile extends Overlay {      
    private boolean iShow = true;
    
    private boolean iNormalize = false;
    private double  iHist[];
    
    public Profile(Rectangle2D aS, ROIManager aMgr) {
        super("PROFILE", aS, aMgr); //NOI18N 
        makeHistogram();
    }
    
    @Override
    int getCaps() {
        return MOVEABLE | SELECTABLE | HASMENU;
    }
    
    @Override
    public void paint(Graphics2D aGC, AffineTransform aTrans) {
        final Rectangle bn = aTrans.createTransformedShape(iShape).getBounds();          
        final Color tmp = aGC.getColor();
        
        aGC.setColor(Settings.BLUEVIOLET);
        aGC.drawLine(bn.x, bn.y, bn.x+bn.width, bn.y);                           
        
        aGC.setColor(java.awt.Color.RED);
        aGC.drawLine(bn.x, bn.y+bn.height, bn.x+bn.width, bn.y+bn.height);
      
        aGC.setColor(tmp);
        
        if (iShow)
            drawHistogram(aGC, aTrans);
    } 
   
    @Override
    public void update() {
        makeHistogram();        
    }            
    
    @Override
    public void move(double adX, double adY) {             
        final Rectangle2D r = iShape.getBounds2D();
        
        Shape temp = AffineTransform.getTranslateInstance(.0, adY).createTransformedShape(
                                 new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), Math.max(1.0, r.getHeight() + adX))
                              );  
        
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getWidth(), getManager().getHeight());
        
        if (bounds.contains(temp.getBounds())) {
            iShape = temp;
            update();
        }
    }  
    
    private void makeHistogram() {
        final Rectangle bounds = iShape.getBounds();

        getManager().getView().getImage().extract((Raster aR) -> {
            iHist = new double[bounds.width];
            
            double temp[] = new double [aR.getNumBands()];
            
            for (int i = 0; i < bounds.width; ++i)
                for (int j = bounds.y; j < bounds.y + bounds.height; ++j)
                    iHist[i] += aR.getPixel(i, j, temp)[0];
        });
    
    }
    
    private void drawHistogram(Graphics2D aGC, AffineTransform aTrans) {        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (double d : iHist) {
            min = Math.min(min, d);
            max = Math.max(max, d);
        }
        
        double maxV = getManager().getView().getImage().getMax();
        double minV = getManager().getView().getImage().getMin();
        final double range =  maxV - minV; 
        
        Rectangle bounds = new Rectangle(0, 0, getManager().getWidth(), getManager().getHeight());
                       
        final double scale = Math.min(iShape.getBounds().getY()/(4*range), bounds.getHeight()/(4*range));
                                              
        Path2D.Double s = new Path2D.Double();
        
        int n = 0;
        s.moveTo(0, iShape.getBounds().getY() - iHist[n] * scale);
        
        for (;n < iHist.length; ++n) 
            s.lineTo(n, iShape.getBounds().getY() - iHist[n] * scale);
        
       
        aGC.setXORMode(Color.WHITE);     
        
        aGC.draw(aTrans.createTransformedShape(s));
                
        aGC.setPaintMode(); //turn XOR mode off
    }
    
    public boolean normalize() {
        return iNormalize = !iNormalize; 
    }
    
    public boolean showHistogram() {
        return iShow = !iShow; 
    }
    
    private static final Logger logger = LogManager.getLogger(Profile.class);
}
