
package com.ivli.roim;


import com.ivli.roim.core.Filter;
import java.awt.Graphics2D;
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
    private boolean iMultiline = true;
    private Filter []iFilters = {Filter.DENSITY, Filter.AREAINPIXELS};   
    private final ROI iRoi; 
     // private final Color iColor;
    private java.util.Collection<String> iAnnotation = new java.util.ArrayList<>();           
    
    @Override
    int getCaps(){return HASMENU|MOVEABLE|SELECTABLE|PINNABLE|HASCUSTOMMENU;}
    
    public ROI getRoi() {return iRoi;}
   
    public void setFilters(Filter[] aF) {
        iFilters = aF;
        notifyROIChanged(ROIChangeEvent.CHG.Changed, null);
    }  
    
    public Filter[] getFilters() {
        return iFilters;
    }
    
    public void setMultiline(boolean aM) {
        iMultiline = aM;
        notifyROIChanged(ROIChangeEvent.CHG.Changed, null);
    }
    
    public boolean isMultiline() {
        return iMultiline;
    }
    
    public Annotation(ROI aRoi) {
        super("ANNOTATION", null, aRoi.getManager());  
        iRoi = aRoi;      
        //iColor = iRoi.getColor();
        update();                       
        aRoi.addROIChangeListener(this);
    }
    
    Rectangle2D calcBounds() {
        
        double width  = 0;
        double height = 0;
        
        final java.awt.FontMetrics fm = getManager().getView().getFontMetrics(getManager().getView().getFont());
        
        for (String s:iAnnotation) {
            Rectangle2D b = fm.getStringBounds(s, getManager().getView().getGraphics());        
          
            if (iMultiline) {
                width = Math.max(width, b.getWidth());
                height += b.getHeight();
            } else {
                width += b.getWidth();;
                height = Math.max(height, b.getHeight());
            }
        }
        return new Rectangle2D.Double(0, 0, width, height);
    }
    
    public void update() {     
        iAnnotation.clear();

        for (Filter f : iFilters)
            iAnnotation.add(f.getMeasurement().format(f.get(iRoi)));     
        final Rectangle2D bnds = calcBounds();//getManager().getView().getFontMetrics(getManager().getView().getFont()).getStringBounds(iAnnotation, getManager().getView().getGraphics());        
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
        aGC.setColor(iRoi.getColor());
        if (!iMultiline) {
            String str = new String();
            str = iAnnotation.stream().map((s) -> s).reduce(str, String::concat);            
            aGC.drawString(str, (int)temp.getX(), (int)(temp.getY() + temp.getHeight() - 4));                   
        } else {            
            final double stepY = temp.getHeight() / iAnnotation.size();
            double posY = temp.getY() + stepY - 4.;
            for(String str : iAnnotation) {                               
                aGC.drawString(str, (int)temp.getX(), (int)posY);
                posY += stepY;
            }
        }
        aGC.draw(temp);                 
    }               
       
    
    public String []getCustomMenu() {
        java.util.ArrayList<String> ret = new java.util.ArrayList<>();
        
        for (Filter f:iFilters)
            ret.add(f.getMeasurement().getName());
        
        return (String[])ret.toArray();
    }
          
    
    private static final Logger logger = LogManager.getLogger(Annotation.class);
}
