
package com.ivli.roim;


import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class Annotation extends Overlay implements ROIChangeListener {   
    static final int ANNOTATION_ROI_NAME = 0x1;
    static final int ANNOTATION_AREA_IN_PIXELS = ANNOTATION_ROI_NAME << 1;
    static final int ANNOTATION_DENSITY = ANNOTATION_AREA_IN_PIXELS << 1;
    static final int ANNOTATION_AREA_MILLIS = ANNOTATION_DENSITY << 1;
    static final int ANNOTATION_MIN_PIXEL = ANNOTATION_AREA_MILLIS << 1;    
    static final int ANNOTATION_MAX_PIXEL = ANNOTATION_MIN_PIXEL << 1;   
    static final int ANNOTATION_AVERAGE_PIXEL_VALUE = ANNOTATION_MAX_PIXEL << 1;
    static final int ANNOTATION_DISPLAY_USER_TEXT = ANNOTATION_AVERAGE_PIXEL_VALUE << 1;
      
    static final int FIELDS_TO_DISPLAY = ANNOTATION_AREA_IN_PIXELS|ANNOTATION_DENSITY;
    
    final ROI iRoi; //eah, where're my favorite c++'s constant referencies
    //private String iText; 
    //private Color iColor;
  
    private String iAnnotation;// = new String();
           
    class Field<T> {
        final String iName;
        final String iUnits;
        T iValue;
        
        Field(String aN, T aV, String aU) {
            iName  = aN; 
            iValue = aV; 
            iUnits = aU;            
        }
        
        String format() {
            return String.format("%s%s", iValue, iUnits);
        }
    };
           
    final static int FIELD_ROI_NAME            = 0;
    final static int FIELD_AREA_IN_PIXELS      = FIELD_ROI_NAME + 1;
    final static int FIELD_DENSITY             = FIELD_AREA_IN_PIXELS + 1;
    final static int FIELD_AREA_IN_MILLIS      = FIELD_DENSITY + 1;
    final static int FIELD_MIN_PIXEL_VALUE     = FIELD_AREA_IN_MILLIS + 1;
    final static int FIELD_MAX_PIXEL_VALUE     = FIELD_MIN_PIXEL_VALUE + 1;
    final static int FIELD_AVERAGE_PIXEL_VALUE = FIELD_MAX_PIXEL_VALUE + 1;
    final static int FIELD_USER_TEXT           = FIELD_AVERAGE_PIXEL_VALUE + 1;
    
    Field[] iFields = new Field[] {
        new Field<>("FIELD.ROI_NAME", 
                    new String() , 
                    "UNITS.ROI_NAME"
                   ),  

        new Field<>("FIELD.AREA_IN_PIXELS", 
                    1, 
                    "UNITS.PIXELS"
                   ),  

        new Field<>("FIELD.DENSITY", 
                    1, 
                    "UNITS.COUNTS"
                   ),  

        new Field<>("FIELD.AREA_IN_MILLIS", 
                    new Integer[]{0, 0} , 
                    "UNITS.MILLIMETERS"
                   ),  
                
        new Field<>("FIELD.MIN_PIXEL_VALUE", 
                    0, 
                    "UNITS.COUNTS"
                   ), 
        
        new Field<>("FIELD.MAX_PIXEL_VALUE", 
                    0, 
                    "UNITS.COUNTS"
                   ),  
        
        new Field<>("FIELD.AVERAGE_PIXEL_VALUE", 
                    0, 
                    "UNITS.COUNTS"
                   ), 
        
        new Field<>("FIELD.USER_TEXT", 
                    new String(), 
                    "UNITS.USER_TEXT"
                   )      
    };     
    
    boolean iRecalcRect = true;
            
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE;}
   
    Annotation(ROI aRoi) {
        super("", null, aRoi.getManager());
                
        iRoi = aRoi;
        
        update(aRoi);

        Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());
        
        
        iShape = new Rectangle2D.Double(aRoi.getShape().getBounds2D().getX(), 
                                        aRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());
                
        aRoi.addROIChangeListener(this);
    }
    
        
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        final Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();     
        aGC.setColor(iRoi.getColor());
            
        aGC.drawString(iAnnotation, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));       
        aGC.draw(temp);                 
    }
           
    public void update() {
        iAnnotation = new String();
        if(0 != (FIELDS_TO_DISPLAY & ANNOTATION_DENSITY))
            iAnnotation += iFields[FIELD_DENSITY].format();
        if(0 != (FIELDS_TO_DISPLAY & ANNOTATION_AREA_IN_PIXELS))
            iAnnotation += iFields[FIELD_AREA_IN_PIXELS].format();
        /*
        if(0 != (iFields & ANNOTATION_DISPLAY_AREA_UNITS))
            out += String.format(", area=%.1f", s.getArea());
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_MIN))
            out += String.format(", min=%.1f", s.getMin());
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_MAX))
            out += String.format(", max=%.1f", s.getMax());      
        if(0 != (iFields & ANNOTATION_DISPLAY_CNTS_IDEN))
            out += String.format(", iden=%.1f", s.getIden());        
        out += "."; 
        
        */
    }
    
    private void update(ROI aR) {
        iFields[FIELD_ROI_NAME].iValue = aR.getName();
        iFields[FIELD_DENSITY].iValue  = aR.getDensity();
        iFields[FIELD_AREA_IN_PIXELS].iValue = aR.getAreaInPixels();    
        update();
    }
    
    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {       
        //if (anEvt.getROI() != iRoi) 
        //    return; //we don't care of other ROIs
        switch (anEvt.getChange()) {
            case Cleared: 
                //commit suicide 
                getManager().deleteOverlay(this);
                break;
            case Moved: {//if not pinned move the same dX and dY
                final double[] deltas = (double[])anEvt.getExtra();
                ///logger.info(String.format("%f, %f",deltas[0], deltas[1]));
                move(deltas[0], deltas[1]);
                update((ROI)anEvt.getROI());
            } break;
            case Changed:   
            default: //fall-through
                update((ROI)anEvt.getROI()); break;
        }        
    }
    
    
     private static final Logger logger = LogManager.getLogger(Annotation.class);
}
