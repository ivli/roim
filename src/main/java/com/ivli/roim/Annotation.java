/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author likhachev
 */
public class Annotation extends Overlay {
    
    protected static final int ANNOTATION_DISPLAY_AREA_PIXELS = 0x1;
    protected static final int ANNOTATION_DISPLAY_AREA_UNITS  = 0x2;
    protected static final int ANNOTATION_DISPLAY_CNTS_MIN    = 0x4;
    protected static final int ANNOTATION_DISPLAY_CNTS_MAX    = 0x8;
    protected static final int ANNOTATION_DISPLAY_CNTS_IDEN   = 0x10;
    protected static final int ANNOTATION_DISPLAY_POS_PIXELS  = 0x20;
    protected static final int ANNOTATION_DISPLAY_POS_LIN     = 0x40;
    protected static final int ANNOTATION_DISPLAY_USER_TEXT   = 0x80;
      
    private static final int iFields = ANNOTATION_DISPLAY_AREA_PIXELS|ANNOTATION_DISPLAY_CNTS_IDEN;
    
    private final ROI iRoi; //eah, where're my favorite c++'s constant referencies
    private String iText; 
    
    @Override
    int getCaps(){return MOVEABLE | SELECTABLE;}
   
    Annotation(ROI aRoi) {
        super(new Rectangle2D.Double(), null);
        iRoi = aRoi;        
        ((Rectangle2D.Double)iShape).setRect(aRoi.getShape().getBounds2D());
        aRoi.register(this);
    }
    
    @Override
    void move(double adX, double adY) {
       ((Rectangle2D.Double)iShape).x += adX;
       ((Rectangle2D.Double)iShape).y += adY;   
    }
    
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();
        ROIStats s = iRoi.getStats();
        String out = new String();
        
        aGC.setColor(iRoi.getColor());
        
        if(0 != (iFields & ANNOTATION_DISPLAY_AREA_PIXELS))
            out += String.format("pix=%.0f", s.getPixels());
        //if(0 != (iFields & ANNOTATION_DISPLAY_AREA_PIXELS))
        //    out += String.format("pixels=%.1f", s.iBounds);
        if(0 != (iFields & ANNOTATION_DISPLAY_AREA_UNITS))
            out += String.format(", area=%.1f", s.getArea());
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_MIN))
            out += String.format(", min=%.1f", s.getMin());
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_MAX))
            out += String.format(", max=%.1f", s.getMax());      
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_IDEN))
            out += String.format(", iden=%.1f", s.getIden());        
        out += ".";        
        aGC.drawString(out, (int)temp.getX(), (int)temp.getY());       
    }
    
    @Override
    void update() {
        
    }    
}
