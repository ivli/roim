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

import com.ivli.roim.core.Curve;
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
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Uid;
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
    private static final double DEFAULT_SCALE_X = 1.0;
    private static final double DEFAULT_SCALE_Y = 1.0;    
    private static final double MIN_SCALE = .01;
       
    protected ZoomFit iFit; //a method of initial fitting into the window       
    protected Object iInterpolation; //interpolation method   
    
    protected Point iOrigin; //image point [0,0] on the window implements panoramic transform 
    protected AffineTransform iZoom; //zoom factor relating to original image size
               
    protected VOITransform iVLUT; //VOI LUT combines W/L and Presentation LUT 
   
    protected int iCurrent; //frame that is currently shown
    ImageFrame iFrame;
    protected IMultiframeImage iModel; //the image   
    
    protected IController iController; //Controller of MVC 
    protected ROIManager iMgr; //
    
    protected BufferedImage iBuf2; //pre processed image of original size       
    protected BufferedImage iBuf;  //offscreen buffer made of a iBuf2 after zoom and pan   
        
  
    private final EventListenerList iListeners; //
   
    public static ImageView create(IMultiframeImage aI, ViewMode aMode) {        
        return create(aI, aMode, new ROIManager(aI, Uid.getNext(), false));    
    }
        
    public static ImageView create(IMultiframeImage aI, ViewMode aMode, ROIManager aM) {
        ImageView ret = new ImageView();
        ret.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        ret.setFit(ImageView.ZoomFit.VISIBLE);
        ret.setController(new Controller(ret));
        ret.iMgr = aM;
        
        ret.setImage(aI);
        ret.setViewMode(aMode);         
        ret.addComponentListener(new ComponentListener() {    
            @Override
            public void componentResized(ComponentEvent e) {
                ret.invalidateBuffer();
                ret.notifyZoomChanged();
                ret.iZoomStep = Math.min(ret.getFrame().getWidth(), ret.getFrame().getHeight()) / Settings.get(Settings.KEY_ZOOM_STEP_FACTOR, 10.);
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
    
    protected ImageView() {                                
        iCurrent = 0; 
        iOrigin = new Point(0, 0);              
        iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);          
        iVLUT = new VOITransform();                             
        iListeners = new EventListenerList();        
    }
      
    final void setController(IController aC) {
        if (null != iController) {
            removeMouseListener(iController);
            removeMouseMotionListener(iController);
            removeMouseWheelListener(iController);
            removeKeyListener(iController);
        }
        
        iController = aC;
        addMouseListener(iController);
        addMouseMotionListener(iController);
        addMouseWheelListener(iController);
        addKeyListener(iController);
    }
    
    protected ViewMode iMode = ViewMode.DEFAULT;
        
    public void setViewMode(ViewMode aM) {
        if (null != aM && aM.isCompatible(iModel) && aM != ViewMode.DEFAULT_IMAGE_MODE) {
            iMode = aM;
        } else {
            /*
            switch (iModel.getImageType()) {
                case IMAGE:            
                case STATIC:    
                case WHOLEBODY:
                    iMode = DEFAULT_STATIC_IMAGE_MODE; break;
                case DYNAMIC: 
                case GATED: 
                    iMode = DEFAULT_DYNAMIC_IMAGE_MODE; break;
                case TOMO:
                case TOMO_G://TODO: not reconstructed image
                case VOLUME:
                case VOLUME_G:
                    iMode = DEFAULT_TOMO_IMAGE_MODE; break;
                //CR/CT
                case AXIAL:
                case LOCALIZER:
                case UNKNOWN:  
            }*/
        } 
        
        switch (iMode.iType) {        
            case DEFAULT:
            case FRAME:
            case CINE: 
            case RANGE: break;
            case COMPOSITE:
                setImage(iModel.processor().collapse(aM.iFrameFrom, aM.iFrameTo)); break;
            case SLICE: //TODO:
                break;
            case VOLUME: {
               // MIPProjector prj = new MIPProjector(getImage(), getImage().getNumFrames());
               // setImage(prj.getDst());
               // prj.project();
            } break;
        }
    }
    
    @Override
    public void setLUT(String aLUT) {          
        iVLUT.setLUT(LUTTransform.create(aLUT));
        invalidateBuffer();
    }    
    
    @Override
    public void setROIMgr(ROIManager aMgr) {
        iMgr = aMgr;  
    }
    
    @Override
    public void setImage(IMultiframeImage anImage) {                
        iModel = anImage;             
        iVLUT.setTransform(iModel.getRescaleTransform());  
       
        iCurrent = 0; 
        iFrame = iModel.get(iCurrent);   
        setWindow(Window.fromRange(getMin(), getMax()));
    }
                 
    @Override
    public AffineTransform getZoom() {
        return iZoom;
    }    
    
    public IMultiframeImage getImage() {
        return iModel;
    }
    
    @Override
    public ImageFrame getFrame() {
        return iFrame;//
    }
    
    public ROIManager getROIMgr() {
        return iMgr;
    } 
    
    /*    
    public VOITransform getLUTMgr() {
        return iVLUT;
    }
    */
    
    public void setFit(ZoomFit aFit) {               
        iFit = aFit;         
        invalidateBuffer();       
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
        iListeners.remove(ZoomChangeListener.class,aL);
    }
     
    protected void notifyZoomChanged() {
        final ZoomChangeEvent evt = new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY());        
                
        for (ZoomChangeListener l : iListeners.getListeners(ZoomChangeListener.class))
            l.zoomChanged(evt);       
    }
  
    public AffineTransform screenToVirtual() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(iZoom);
        
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
        ret.concatenate(iZoom);
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
            iFrame = iModel.get(aN);            
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
    
    public void zoom(double aFactor) {
        iFit = ZoomFit.NONE;        
        iZoom.setToScale(Math.max(iZoom.getScaleX() + aFactor/iZoomStep, MIN_SCALE), Math.max(iZoom.getScaleY() + aFactor/iZoomStep, MIN_SCALE));               
        invalidateBuffer();  
        repaint();
    }
     
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;     
        repaint();
    }
             
    public void reset() {
        iMgr.clear();
        iOrigin.x = iOrigin.y = 0;
        iZoom.setToScale(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);  
        iFit = ZoomFit.PIXELS;
        invalidateBuffer();
        repaint();
    }    
             
    protected void invalidateBuffer() {
        iBuf = null;       
    }
    
    protected void updateScale() {
        double scale;
        
        switch (iFit) {
            case PIXELS: scale = 1.0; break;
            case VISIBLE:
                final double scaleX = (double)getWidth() / (double)getVisualWidth(); 
                final double scaleY = (double)getHeight() / (double)getVisualHeight(); 
                scale = Math.min(scaleX, scaleY); break;                
            case HEIGHT: scale = (double)getHeight() / (double)getVisualHeight(); break;
            case WIDTH:  scale = (double)getWidth() / (double)getVisualWidth(); break;
            case NONE: //falltrough to default
            default: 
                return;                            
        } 
                  
        iZoom.setToScale(scale, scale);  
    }                         
             
    protected void updateBufferedImage() {
        updateScale();               
        RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);        
       
        
        ///ImageFrame frm = iModel.get(iCurrent);
        
        BufferedImage bi = new BufferedImage(iFrame.getWidth(), iFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
        bi.getRaster().setDataElements(0, 0, iFrame.getWidth(), iFrame.getHeight(), iModel.get(iCurrent).getPixelData());
       /*         
        iBuf = iVLUT.transform(z.filter(bi, null), null); 
        
        /* */ 
        iBuf2 = iVLUT.transform(bi, iBuf2);        
        iBuf = z.filter(iBuf2, iBuf);                 
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
            AffineTransformOp z = new AffineTransformOp(getZoom(), hts);        

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
       
        iMgr.paint(new ROIPainter((Graphics2D)g, this));//virtualToScreen()));                        
        iController.paint((Graphics2D)g); // must be the last in the paint chain   
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
        return getFrame().getStats().getMin();
    }
    
    public double getMax() {
        return getFrame().getStats().getMax();
    }           
    
    @Override
    public BufferedImage transform (BufferedImage aSrc, BufferedImage aDst) {
        return iVLUT.transform(aSrc, aDst);
    }
    
    @Override
    public Curve getWindowCurve() {
        return iVLUT.getLUTCurve(); //To change body of generated methods, choose Tools | Templates.
    }    
        
    private static final Logger LOG = LogManager.getLogger();   
}




