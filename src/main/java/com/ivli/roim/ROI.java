
package com.ivli.roim;

import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import com.ivli.roim.events.EStateChanged;
import java.util.HashSet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ROI extends ROIBase implements Overlay.IFlip, Overlay.IRotate {          
    private Color iColor;          
    private final int iAreaInPixels;        
    private SeriesCollection iSeries;        
    private HashSet<Overlay> iAnnos; 
    
    @Override
    int getCaps() {return MOVEABLE|SELECTABLE|CANFLIP|CANROTATE|CLONEABLE|HASMENU;}
    
    ROI(Shape aS, ROIManager aMgr, Color aC) {
        super(aS, aMgr, new String()); 
        iColor = (null != aC) ? aC : Colorer.getNextColor(ROI.class);
          
        iAreaInPixels = calculateAreaInPixels();
        iSeries = CurveExtractor.extract(this);
    }
    
    ROI(ROI aR) {
        super(aR.iShape, aR.getManager(), aR.getName());          
        iColor = aR.getColor();         
        iAreaInPixels = aR.iAreaInPixels;
        iSeries = aR.iSeries;
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
           
    public int getAreaInPixels() {
        return iAreaInPixels;
    }
    
    public double getDensity() {
        return getSeries(Measurement.DENSITY).get(getManager().getImage().getCurrent());
    }
    
    public Series getSeries(int anId) {       
        return iSeries.get(anId);
    }
    
    public Color getColor() {
        return iColor;
    }
    
    public void setColor(Color aC) {
        iColor = aC;
        getManager().notifyROIChanged(this, EStateChanged.ChangedColor);
    }
    
    @Override
    public void setName(String aName) {
       super.setName(aName);
       getManager().notifyROIChanged(this, EStateChanged.ChangedName);         
    }
    
    @Override
    void paint(Graphics2D aGC, AffineTransform aTrans) {
        aGC.setColor(iColor);
        aGC.draw(aTrans.createTransformedShape(getShape()));
    }
    
    @Override
    protected void move(double adX, double adY) {
        //AffineTransform trans = AffineTransform.getTranslateInstance(adX, adY);    
        Shape temp = AffineTransform.getTranslateInstance(adX, adY).createTransformedShape(iShape);        
        Rectangle2D.Double bounds = new Rectangle2D.Double(.0, .0, getManager().getImage().getWidth(), getManager().getImage().getHeight());
        
        if (!bounds.contains(temp.getBounds())) {
            logger.info("!!movement out of range");
        } else {
       
        iShape  = temp;    
        iSeries = CurveExtractor.extract(this);    
        
        if (null != iAnnos) {
            iAnnos.stream().forEach((o) -> {
                o.move(adX, adY);
            });
        }
        
        update();
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
        
    @Override
    void update() {        
        
        if (null != iAnnos) {
            iAnnos.stream().forEach((o) -> {
                o.update();
            });
        }         
    }
    
    @Override
    public void flip(boolean aV) {                
        
        Rectangle r = iShape.getBounds();
        AffineTransform tx;
        
        if (aV) {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -getShape().getBounds().getHeight());
        } else {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-getShape().getBounds().getWidth(), 0);       
        }
        
        
        iShape = tx.createTransformedShape(iShape);
        
        r = iShape.getBounds();
    }
    
    @Override
    public void rotate(int aV) {
        final Rectangle rect = getShape().getBounds();
        AffineTransform tx = new AffineTransform();
        
        tx.rotate(Math.toRadians(aV), rect.getX() + rect.width/2, rect.getY() + rect.height/2);
              
        iShape = tx.createTransformedShape(iShape);
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
