/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.event.EventListenerList;
import javax.swing.JComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ivli.roim.core.Window;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.events.ZoomChangeEvent;
import com.ivli.roim.events.ZoomChangeListener;
import com.ivli.roim.core.IMultiframeImage;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ImageView extends JComponent implements IImageView {
   // private static final double DEFAULT_SCALE_X = 1.0;
   // private static final double DEFAULT_SCALE_Y = 1.0;   
    private static final double DEFAULT_ZOOM = 1.0;
    private static final double MIN_SCALE = .01;
       
    ///protected FITMODE iFit; //a method of initial fitting into the window       
    protected Object iInterpolation; //interpolation method   
    
    protected Point iOrigin = new Point(0,0); //image point [0,0] on the window implements panoramic transform 
    
    //zoom factor assuming isotropic image zoom ie zoom.x == zoom.y, zoom := 1.0 => no zoom image is shown pixel_to_pixel
    protected double iZoom = DEFAULT_ZOOM; 
    protected int iCurrent = 0; //frame that is currently shown  
    
    protected VOITransform iVLUT; //VOI LUT combines W/L and Presentation LUT 
   
    protected IMultiframeImage iModel; //the image   
    
    protected IController iController; //Controller of MVC 
    protected ROIManager  iMgr; //
    
    protected BufferedImage iBuf2; //pre processed image of original size       
    protected BufferedImage iBuf;  //offscreen buffer made of a iBuf2 after zoom and pan   
  
    private final EventListenerList iListeners = new EventListenerList(); //

    public static ImageView create(IMultiframeImage aI, VOITransform aTransform, ROIManager aM) {
        ImageView ret = new ImageView(null != aTransform ? aTransform: new VOITransform());              
        
        ret.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      
        ret.doConstruct(aM != null ? aM : ROIManager.create(aI));
        ret.setImage(aI);
             
        ret.addComponentListener(new ComponentListener() {    
            @Override
            public void componentResized(ComponentEvent e) {
                ret.invalidateBuffer();
                ret.notifyZoomChanged();
                ret.iZoomStep = Math.min(ret.iModel.get(ret.iCurrent).getWidth(), ret.iModel.get(ret.iCurrent).getHeight()) / Settings.get(Settings.KEY_ZOOM_STEP_FACTOR, 10.);
            }                                               
            @Override
            public void componentHidden(ComponentEvent e) {}
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}                    
        });  
        
        ret.addFrameChangeListener(ret.iMgr);
        return ret;    
    }
    
    protected ImageView(VOITransform aTransform) {                                      
        iVLUT = aTransform;           
    }
      
    final void doConstruct(ROIManager aM) {
        if (null != iController) {
            removeMouseListener(iController);
            removeMouseMotionListener(iController);
            removeMouseWheelListener(iController);
            removeKeyListener(iController);
        }
        
        iController = new Controller(this);
        addMouseListener(iController);
        addMouseMotionListener(iController);
        addMouseWheelListener(iController);
        addKeyListener(iController);
        iMgr = aM;
    }
    
    @Override
    public void setLUT(String aLUT) {          
        iVLUT.setLUT(LUTTransform.create(aLUT));
        invalidateBuffer();
        repaint();
    }    
    
    @Override
    public void setROIMgr(ROIManager aMgr) {
        iMgr = aMgr;  
    }
    
    @Override
    public void setImage(IMultiframeImage anImage) {                
        iModel = anImage;             
        iVLUT.setTransform(iModel.getModalityTransform());  
       
        iCurrent = 0; 
        ///iFrame = iModel.get(iCurrent);   
        setWindow(Window.fromRange(getMin(), getMax()));
    }
                 
    @Override
    public double getScale() {
        return iZoom;
    }    
    
    @Override
    public IMultiframeImage getImage() {
        return iModel;
    }
   
    public ROIManager getROIMgr() {
        return iMgr;
    }     
    
    public void fit(FITMODE aFit) {                         
        switch (aFit) {
            case FIT_PIXELS: iZoom = 1.0; break;                          
            case FIT_HEIGHT: iZoom = (double)getHeight() / (double)getVisualHeight(); break;
            case FIT_WIDTH:  iZoom = (double)getWidth() / (double)getVisualWidth(); break;
            case FIT_VISIBLE:               
                 iZoom = Math.min((double)getWidth() / (double)getVisualWidth(), (double)getHeight() / (double)getVisualHeight());              
            default:
        }        
        invalidateBuffer(); 
        repaint();
    }
           
    public void setInterpolationMethod(Object aMethod) {
        iInterpolation = aMethod;       
        invalidateBuffer();       
    }
    
    public Object getInterpolationMethod() {
        return iInterpolation;
    }
        
    public int getVisualWidth() {
        return iModel.getWidth();
    }
    
    public int getVisualHeight() {
        return iModel.getHeight();
    }
        
    public void addWindowChangeListener(WindowChangeListener aL) {        
        iListeners.add(WindowChangeListener.class, aL);       
    }
   
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iListeners.remove(WindowChangeListener.class, aL);
    }
            
    protected void notifyWindowChanged() {
        final WindowChangeEvent evt = new WindowChangeEvent(this, getWindow());
                
        for (WindowChangeListener l : iListeners.getListeners(WindowChangeListener.class)) {
            l.windowChanged(evt);     
        }
    }
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iListeners.add(FrameChangeListener.class, aL);       
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iListeners.remove(FrameChangeListener.class, aL);
    } 
    
    protected void notifyFrameChanged() {
        final FrameChangeEvent evt = new FrameChangeEvent(this, getFrameNumber());
        
        for (FrameChangeListener l : iListeners.getListeners(FrameChangeListener.class))
            l.frameChanged(evt);       
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iListeners.add(ZoomChangeListener.class, aL);
        //aL.zoomChanged(new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY()));
    }
    
    public void removeZoomChangeListener(ZoomChangeListener aL) {
        iListeners.remove(ZoomChangeListener.class, aL);
    }
     
    protected void notifyZoomChanged() {
        final ZoomChangeEvent evt = new ZoomChangeEvent(this, iZoom, iZoom);        
                
        for (ZoomChangeListener l : iListeners.getListeners(ZoomChangeListener.class))
            l.zoomChanged(evt);       
    }
  
     @Override
    public void removeListenerFromAllLists(Object aL) {        
        if (aL instanceof ZoomChangeListener)
            removeZoomChangeListener((ZoomChangeListener)aL);
        if (aL instanceof WindowChangeListener)
            removeWindowChangeListener((WindowChangeListener)aL);
        if (aL instanceof FrameChangeListener)
            removeFrameChangeListener((FrameChangeListener)aL);
    }
    
    public AffineTransform screenToVirtual() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(AffineTransform.getScaleInstance(iZoom, iZoom));
        
        try {            
           ret.invert(); 
        } catch (NoninvertibleTransformException ex) { 
            LOG.info(ex); // can I do anything more conscious here            
        }
        return ret;
    }
    
    @Override
    public AffineTransform virtualToScreen() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(AffineTransform.getScaleInstance(iZoom, iZoom));
        return ret;
    }
    @Override      
    public Point2D screenToVirtual(Point2D aS) {      
        Rectangle2D r = screenToVirtual().createTransformedShape(new Rectangle2D.Double(aS.getX(), aS.getY(), 1, 1)).getBounds2D();        
        return new Point2D.Double(r.getX(), r.getY());
    }
    @Override
    public Point2D virtualToScreen(Point2D aS) {      
        Rectangle2D r = virtualToScreen().createTransformedShape(new Rectangle2D.Double(aS.getX(), aS.getY(), 1, 1)).getBounds2D();        
        return new Point2D.Double(r.getX(), r.getY());
    }
    
    public int getFrameNumber() {
        return iCurrent;
    }  
    
    /*       
    protected BufferedImage createBufferedImage(ImageFrame aF) {               
        WritableRaster wr = Raster.createBandedRaster(DataBuffer.TYPE_INT, aF.getWidth(), aF.getHeight(), 1, new Point());        
        wr.setDataElements(0, 0, aF.getWidth(), aF.getHeight(), aF.getPixelData());
       
        return new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),                                                               
                                                         new int[] {8},
                                                         false,		// has alpha
                                                         false,		// alpha premultipled
                                                         Transparency.OPAQUE,
                                                         wr.getDataBuffer().getDataType()),                                                                                                                                                                                         
                                 wr, true, null);        
    }   
    */
    
    
    @Override
    public boolean setFrameNumber(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {                  
            Window w = getWindow();
            double oldTop = w.getTop();
            double oldBot = w.getBottom();
            double oldMin = getMin();
            double oldMax = getMax();            
            double oldRange = oldMax - oldMin;
            iCurrent = aN; 
            //iFrame = iModel.get(aN);            
            double newMin = getMin();
            double newMax = getMax();
            double newRange = newMax - newMin;
            
            //order is important: invoke notifyFrameChanged before setWindow 
            notifyFrameChanged();
            
            if (Math.abs(oldRange - newRange) > 1.0 ) {
                final double scale = newRange / oldRange;
                iVLUT.setWindow(Window.fromRange(oldBot*scale, oldTop*scale));           
            }
            
            invalidateBuffer();            
            repaint();
        }
        
        return true;
    }
    
    private double iZoomStep;
    
    @Override
    public void zoom(double aFactor) {      
        iZoom = Math.max(iZoom + aFactor/iZoomStep, MIN_SCALE);               
        invalidateBuffer();  
        repaint();
    }
     
    @Override
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;             
        repaint();
    }
             
    @Override
    public void reset() {
        iMgr.clear();
        iOrigin.x = iOrigin.y = 0;
        iZoom = DEFAULT_ZOOM;       
        invalidateBuffer();
        repaint();
    }    
             
    protected void invalidateBuffer() {
        iBuf = null;       
    }            
             
    protected void updateBufferedImage() {                     
        RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
        AffineTransformOp z = new AffineTransformOp(AffineTransform.getScaleInstance(iZoom, iZoom), hts);        
               
        BufferedImage bi = new BufferedImage(iModel.getWidth(), iModel.getHeight(), BufferedImage.TYPE_INT_RGB);
        bi.getRaster().setDataElements(0, 0, iModel.getWidth(), iModel.getHeight(), iModel.get(iCurrent).getPixelData());
       
        iBuf2 = transform(bi, iBuf2);        
        iBuf = z.filter(iBuf2, iBuf);                 
    }
    
    IPainter getDefaultPainter(Graphics2D aGC) {
        return new RoundPainter(aGC, this);
    }
    
    @Override
    public void paintComponent(Graphics g) {                           
        if (null == iBuf) {
            long endTime, startTime = System.currentTimeMillis(); 
            updateBufferedImage();
            endTime = System.currentTimeMillis();
            LOG.debug("buffer update took " + (endTime - startTime)); 
            /*
            long endTime1, endTime2, startTime = System.currentTimeMillis(); 
            updateScale();               
            RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
            AffineTransformOp z = new AffineTransformOp(getScale(), hts);        

            iBuf2 = iVLUT.transform(iModel.get(iCurrent), iBuf2);    
            endTime1 = System.currentTimeMillis();
            iBuf = z.filter(iBuf2, iBuf);      
            endTime2 = System.currentTimeMillis();
            LOG.debug("buffer update took " + (endTime2 - startTime) + ", " +
                "voilut transform " + (endTime1 - startTime) + ", " +
                "zoom " + (endTime2 - endTime1) + "millis");
            */
        }
        
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);       
       
        iMgr.paint(getDefaultPainter((Graphics2D)g)); 
        
        if (null != iController.getActionItem()) // must get called last in the painting sequence
            iController.getActionItem().paint((Graphics2D)g);    
    }

    @Override
    public void setWindow(Window aW) {    
        if (aW.getBottom() >= getMin() && aW.getTop() <= getMax()) {            
            LOG.debug("window changed" + aW);            
            iVLUT.setWindow(aW);              
            notifyWindowChanged();
            invalidateBuffer();             
            repaint();
        }
    }
    
    @Override
    public Window getWindow() {
        return iVLUT.getWindow();
    }

    public void setInverted(boolean aI) {          
        iVLUT.setInverted(aI);
        invalidateBuffer();
        notifyWindowChanged();  
        repaint();
    }

    @Override
    public boolean isInverted() {
        return iVLUT.isInverted();
    }

    @Override
    public void setLinear(boolean aI) {      
        iVLUT.setLinear(aI);
        invalidateBuffer();
        notifyWindowChanged();   
        repaint();
    }

    @Override
    public boolean isLinear() {
        return iVLUT.isLinear();
    }
       
    public double getMin() {
        return iModel.get(iCurrent).getStats().getMin();
    }
    
    public double getMax() {
        return iModel.get(iCurrent).getStats().getMax();
    }           
    
    
    BufferedImage transform (BufferedImage aSrc, BufferedImage aDst) {
        return iVLUT.transform(aSrc, aDst);
    }
    
    @Override
    public ImageTransform getTransform() {
        return iVLUT;
    }
    /*
    @Override
    public Curve getWindowCurve() {
        return iVLUT.getLUTCurve(); //To change body of generated methods, choose Tools | Templates.
    }    
    */   
    private static final Logger LOG = LogManager.getLogger();   
}




