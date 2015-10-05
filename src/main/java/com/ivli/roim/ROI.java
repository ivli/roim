
package com.ivli.roim;

import java.util.HashSet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ROI extends Overlay implements Overlay.IFlip, Overlay.IRotate {      
    transient ROIManager iMgr; 
    private Color iColor;
    private ROIStats iStats;
              
    private Series iSeries;
    private HashSet<Overlay> iAnnos; 
    
    @Override
    int getCaps() {return MOVEABLE|SELECTABLE|CANFLIP|CANROTATE|CLONEABLE;}
    
    ROI(Shape aS, ROIManager aMgr, Color aC) {
        super(aS, new String()); 
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);
        iMgr   = aMgr;
        iStats = new ROIStats(this.calculateAreaInPixels(), aMgr.getImage().getPixelSpacing());     
        makeCurve();
    }
    
    ROI(ROI aR) {
        super(aR.iShape, aR.iName);  
        iColor = aR.iColor; 
        iMgr   = aR.iMgr;        
        iStats = new ROIStats(aR.iStats);   
    }
           
    ROIManager getManager() {
        return iMgr;
    }
    
    void register(Overlay aO) {
        if (null == iAnnos)
            iAnnos = new HashSet();
        iAnnos.add(aO);
    }
    
    boolean remove(Overlay aO) {
        if (null != iAnnos) {
            return iAnnos.remove(aO);
        }
        return false;
    }   
       
    public ROIStats getStats() {
        return iStats;//iCurve.get(getManager().getImage().getCurrent());
    }
    
    public Color getColor() {
        return iColor;
    }
    
    public void setColor(Color aC) {
        iColor = aC;
    }
    
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        aGC.setColor(iColor);
        aGC.draw(aTrans.createTransformedShape(getShape()));
    }
    
    @Override
    protected void move(double adX, double adY) {
        AffineTransform trans = AffineTransform.getTranslateInstance(adX, adY);    
        iShape = trans.createTransformedShape(iShape);
        
        makeCurve();    
        
        update();
        
         if (null != iAnnos) {
             iAnnos.stream().forEach((o) -> {
                 o.move(adX, adY);
            });
        }
    }  
            
    private int calculateAreaInPixels() {

        final java.awt.Rectangle bnds = getShape().getBounds();
        int AreaInPixels = 0;

        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                if (getShape().contains(i, j)) 
                  ++AreaInPixels;
        
        return AreaInPixels;
    }                 
    
    private void makeCurve() {
        CurveExtractor ce = new CurveExtractor(getManager().getImage());
        Series cv = ce.extract(this);
        iSeries = cv;
    }
    
    public Series getCurve() {      
        return iSeries;
    }
    
    @Override
    void update() {        
        /**/
        Measure mes = iSeries.get(getManager().getImage().getCurrent());
        
        iStats = new ROIStats(iStats.getPixels(), iStats.getArea(), mes.getMin(), mes.getMax(), mes.getIden());
        
        //iStats.iMax  = mes.iMax;
        ///iStats.iMin  = mes.iMin;
        ///iStats.iIden = mes.iIden;
         
        if (null != iAnnos) {
            iAnnos.stream().forEach((o) -> {
                o.update();
            });
        }         
    }
    
    @Override
    public void flip(boolean aV) {}
    @Override
    public void rotate(int aV) {
        Rectangle rect = getShape().getBounds();
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);
        //at.scale(modifier / 100.0, modifier/ 100.0);
        //at.translate(modifier, modifier);
      
        iShape = at.createTransformedShape(iShape);
 
    }
  
    
    
    byte [] treshold(int aValue, int [] aData, int aW, int aH) {
        
        byte [] ret = new byte [aW*aH];
        
        for (int i = 0; i < aW*aH; ++i)            
            ret [i] = (byte) (aData[i] > aValue ? 1:0);
        
        for (int i = 1; i < aW-1; ++i)
            for (int j = 1; j < aH-1; ++j) {
                if (1 < ret[aW*i+j] && ret[aW*i+(j+1)] + ret[aW*i+(j-1)] + ret[aW*(i+1)+j] + ret[aW*(i-1)+j] >=3)
                    ret[i*aW+j] = 1;     
            }

        return ret;
    }
      /*  
    public void isolevel(int aTolerance) {
        Rectangle bounds = iShape.getBounds();
        
        Raster r = iSrc.getBufferedImage().getData();
        int [] data = new int [bounds.width*bounds.height];
        data = r.getPixels(bounds.x, bounds.y, bounds.width, bounds.height, data);
                
        byte [] tr = treshold((int)(iStats.iMax-iStats.iMin)/2, data, bounds.width, bounds.height);
        
        MarchingSquares ms = new MarchingSquares(bounds.width, bounds.height, tr);
        
        Path p = ms.identifyPerimeter();
        
        List<Direction> dir = p.getDirections();
       
        Point2D.Double pt = new Point2D.Double(p.getOriginX()+bounds.x, p.getOriginY()+bounds.y);
        
        Path2D.Double np = new Path2D.Double();
        
        np.moveTo(pt.x, pt.y);
        
        for (Iterator<Direction> it = dir.iterator(); it.hasNext();) {    
            switch(it.next()) {
                case N: pt.y-=1; break; 
                case S: pt.y+=1; break;
                case E: pt.x+=1; break;
                case W: pt.x-=1; break;
            }
            np.lineTo(pt.x, pt.y);
        }
        np.closePath();
        ///AffineTransform tr = AffineTransform.getTranslateInstance(-bounds.x, -bounds.y);
        iShape = np;
    }
    */
    private static final Logger logger = LogManager.getLogger(ROI.class);
}
