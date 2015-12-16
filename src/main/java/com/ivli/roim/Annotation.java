
package com.ivli.roim;


import com.ivli.roim.core.Filter;
import java.awt.Graphics2D;
import java.awt.Color;
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
    private Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   
    private final ROI iRoi; 
    private Color  iColor;
    private String iAnnotation;           
    
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE;}
    
    public ROI getRoi() {return iRoi;}
   
    public void setFilters(Filter[] aF) {
        iFilters = aF;
    }    
    
    public Annotation(ROI aRoi) {
        super("ANNOTATION", null, aRoi.getManager());  
        iRoi = aRoi;      
        iColor = iRoi.getColor();
        update();                       
        aRoi.addROIChangeListener(this);
    }
     
    public void update() {     
        iAnnotation = new String();

        for (Filter f : iFilters)
            iAnnotation += f.getMeasurement().format(f.get(iRoi));     
        final Rectangle2D bnds = getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
        /*
       
        iShape = new Rectangle2D.Double(iRoi.getShape().getBounds2D().getX(), 
                                        iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());
        */      
        
       iShape = new Rectangle2D.Double(null == iShape ? iRoi.getShape().getBounds2D().getX() : getShape().getBounds2D().getX(), 
                                       null == iShape ? iRoi.getShape().getBounds2D().getY() - bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX() :
                                               getShape().getBounds2D().getY(), 
                                        bnds.getWidth() * getManager().getView().screenToVirtual().getScaleX(), 
                                        bnds.getHeight() * getManager().getView().screenToVirtual().getScaleX());
    }
        
    @Override
    public void ROIChanged(ROIChangeEvent anEvt) {               
        switch (anEvt.getChange()) {
            case Cleared: 
                //commit suicide 
                getManager().deleteOverlay(this);
                break;
            case Moved: {//if not pinned move the same dX and dY
                final double[] deltas = (double[])anEvt.getExtra();
                ///logger.info(String.format("%f, %f",deltas[0], deltas[1]));
                move(deltas[0], deltas[1]);
                update();
            } break;
            case Changed:   
            default: //fall-through
                update(); break;
        }        
    }
           
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        final Rectangle2D temp = aTrans.createTransformedShape(getShape()).getBounds();     
        aGC.setColor(iColor);
            
        aGC.drawString(iAnnotation, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));       
        aGC.draw(temp);                 
    }               
       
           
    private static final Logger logger = LogManager.getLogger(Annotation.class);
}
