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
package com.ivli.roim;

import com.ivli.roim.core.IImageView;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ConvolveOp;
import java.awt.image.AffineTransformOp;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.event.EventListenerList;
import javax.swing.JComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Range;
import com.ivli.roim.core.Window;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.events.ZoomChangeEvent;
import com.ivli.roim.events.ZoomChangeListener;
import java.io.IOException;


public class ImageView  extends JComponent implements IImageView {//implements IWLManager {     
    private static final double DEFAULT_SCALE_X = 1.0;
    private static final double DEFAULT_SCALE_Y = 1.0;    
    private static final double MIN_SCALE = .01;
    
    protected int iFit = Settings.get(Settings.KEY_DEFAULT_IMAGE_SCALE, ZoomFit.ONE_TO_ONE);     
    protected       IMultiframeImage iModel;                     
    protected       Controller iController;    
    protected final AffineTransform iZoom;
    protected final Point iOrigin;    
    protected       Object iInterpolation;
           
    protected  ROIManager iROIMgr;
    protected final EventListenerList iListeners;
        
    private   VOILut iVLUT;
    private   PresentationLut iPLUT;
        
    protected int iCurrent;
    protected BufferedImage iBuf; 
    
    public ImageView() {      
        iCurrent = 0;                 
        iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0, 0);  
        iListeners = new EventListenerList(); 
        iInterpolation =  InterpolationMethod.get(InterpolationMethod.INTERPOLATION_NEAREST_NEIGHBOR);        
        iController = new Controller(this);
    
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {
                invalidateBuffer();
                notifyZoomChanged();
                iZoomStep = Math.min(getImage().getWidth(), getImage().getHeight()) / Settings.get(Settings.KEY_ZOOM_STEP_FACTOR, 10.);
            }                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}                    
        });        
        
    }  
    
    public void setImage(IMultiframeImage anImage) {  
        //this();        
        iModel = anImage;     
        iVLUT = new VOILut(this);        
        iPLUT = new PresentationLut();        
        iROIMgr = new ROIManager(this);    
        
        /*     */   
        String lut = Settings.get(Settings.KEY_DEFAULT_PRESENTATION_LUT, LutLoader.BUILTIN_LUTS[1]);
        
        try {
            iPLUT.open(lut);
        } catch (IOException ex) {
            try {
                iPLUT.open(lut = LutLoader.BUILTIN_LUTS[1]);
                
            } catch (IOException ex1) {
                System.exit(-1);
            }
        }
 
        Settings.set(Settings.KEY_DEFAULT_PRESENTATION_LUT, lut);               
    }
                 
    public AffineTransform getZoom() {
        return iZoom;
    }    
    
    public IMultiframeImage getModel() {
        return iModel;
    }
    
    public ImageFrame getImage() {
        return iModel.get(getFrameNumber());
    }
    
    public ROIManager getROIMgr() {
        return iROIMgr;
    }    
    
    public VOILut getLUTMgr() {
        return iVLUT;
    }
    
    public void setFit(int aFit) {               
        iFit = aFit; 
        Settings.set(Settings.KEY_DEFAULT_IMAGE_SCALE, aFit);
        invalidateBuffer();       
    }
           
    public void setInterpolationMethod(Object aMethod) {
        iInterpolation = aMethod;
        Settings.set(Settings.KEY_INTERPOLATION_METHOD, InterpolationMethod.get(aMethod));
        invalidateBuffer();       
    }
    
    public Object getInterpolationMethod() {
        return iInterpolation;
    }
        
    protected int getVisualWidth() {
        return iModel.getWidth();
    }
    
    protected int getVisualHeight() {
        return iModel.getHeight();
    }
        
    public void addWindowChangeListener(WindowChangeListener aL) {
        logger.info("-> addWindowChangeListener {}", aL); //NOI18N
        iListeners.add(WindowChangeListener.class, aL);
        aL.windowChanged(new WindowChangeEvent(this, this.getWindow()));
    }
   
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iListeners.remove(WindowChangeListener.class, aL);
    }
            
    protected void notifyWindowChanged() {
        final WindowChangeEvent evt = new WindowChangeEvent(this, this.getWindow());
                
        for (WindowChangeListener l : iListeners.getListeners(WindowChangeListener.class)) {
            l.windowChanged(evt);     
        }
    }
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iListeners.add(FrameChangeListener.class, aL);
        if (iModel.getImageType() == ImageType.DYNAMIC)
            aL.frameChanged(new FrameChangeEvent(this, iCurrent, iModel.getNumFrames(),                                                            
                                                        this.getRange(),                                                
                                                        iModel.getTimeSliceVector().getSlice(getFrameNumber()))); 
        else
            aL.frameChanged(new FrameChangeEvent(this, iCurrent, iModel.getNumFrames(),                                                            
                                                        this.getRange(),                                                
                                                        iModel.getTimeSliceVector().getSlice(getFrameNumber()))); 
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iListeners.remove(FrameChangeListener.class, aL);
    } 
    
    protected void notifyFrameChanged() {
        final FrameChangeEvent evt = new FrameChangeEvent(this, getFrameNumber(), iModel.getNumFrames(),
                                                            //new Range(iModel.get(iCurrent).getMin(), iModel.get(iCurrent).getMax()),
                                                            this.getRange(),
                                                            iModel.getTimeSliceVector().getSlice(getFrameNumber()));                
      
        for (FrameChangeListener l : iListeners.getListeners(FrameChangeListener.class))
            l.frameChanged(evt);       
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iListeners.add(ZoomChangeListener.class, aL);
        aL.zoomChanged(new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY()));
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
            logger.info(ex); // can I do anything more conscious here            
        }
        return ret;
    }
    
    public AffineTransform virtualToScreen() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(iZoom);
        return ret;
    }
   
    public int getFrameNumber() {
        return iCurrent;
    }  
           
    public boolean loadFrame(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {        
            iCurrent = aN;         
            this.setRange(new Range(iModel.get(iCurrent).getMin(), iModel.get(iCurrent).getMax()));
            iROIMgr.update();   
            notifyFrameChanged();
            notifyWindowChanged();
            invalidateBuffer();
        }
        
        return true;
    }
    
    double iZoomStep;// = Math.min(aC.getImage().getWidth(), aC.getImage().getHeight()) / Settings.get(Settings.KEY_ZOOM_STEP_FACTOR, 10.);
    
    public void zoom(double aFactor) {
        iFit = ZoomFit.NONE;        
        iZoom.setToScale(Math.max(iZoom.getScaleX() + aFactor/iZoomStep, MIN_SCALE), Math.max(iZoom.getScaleY() + aFactor/iZoomStep, MIN_SCALE));               
        invalidateBuffer();             
    }
     
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;
       // repaint();
    }
             
    public void reset() {
        iROIMgr.clear();
        iOrigin.x = iOrigin.y = 0;
        iZoom.setToScale(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);  
        iFit = Settings.get(Settings.KEY_DEFAULT_IMAGE_SCALE, ZoomFit.ONE_TO_ONE);
        invalidateBuffer();
    }    
    
    public static WritableRaster filter(Raster aR) {
        final float[] emboss = new float[] { -2,0,0,   0,1,0,   0,0,2 };
        final float[] blurring = new float[] { 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f, 1f/9f,1f/9f,1f/9f };
        final float[] sharpening = new float[] { -1,-1,-1,   -1,9,-1,   -1,-1,-1 };
        
        Kernel kernel = new Kernel(3, 3, sharpening);
        ConvolveOp op = new ConvolveOp(kernel);
        return op.filter(aR, null);
    }
      
    protected void invalidateBuffer() {
        iBuf = null;
    }
    
    protected void updateScale() {
        double scale;
        
        switch (iFit) {
            case ZoomFit.ONE_TO_ONE: scale = 1.0; break;
            case ZoomFit.VISIBLE:
                final double scaleX = (double)getWidth() / (double)getVisualWidth(); 
                final double scaleY = (double)getHeight() / (double)getVisualHeight(); 
                scale = Math.min(scaleX, scaleY); break;                
            case ZoomFit.HEIGHT: scale = (double)getHeight() / (double)getVisualHeight(); break;
            case ZoomFit.WIDTH:  scale = (double)getWidth() / (double)getVisualWidth(); break;
            case ZoomFit.NONE: //falltrough to default
            default: 
                return;                            
        } 
                  
        iZoom.setToScale(scale, scale);  
    }                 
    
    protected void updateBufferedImage() {                  
        updateScale();        
        notifyZoomChanged();
        
        RenderingHints hts = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);
        BufferedImage src = transform(iModel.get(iCurrent).getBufferedImage(), null);                
        iBuf = z.filter(src, null); 
    }
    
    @Override
    public void paintComponent(Graphics g) {                   
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);        
        iROIMgr.paint((Graphics2D)g, virtualToScreen());                         
        iController.paint((Graphics2D)g); //must reside last in the paint chain   
    }
              
    public void openLUT(String aL) {
        try {
            iPLUT.open(aL);
            Settings.set(Settings.KEY_DEFAULT_PRESENTATION_LUT, aL);
        } catch (IOException ex) {
           logger.error("Unable to open LUT file {}", ex);
        }
        invalidateBuffer();  
        repaint();
    }

    public void setWindow(Window aW) {
        if (!iVLUT.getWindow().equals(aW) && iVLUT.getRange().contains(aW)) {            
            iVLUT.setWindow(aW);               
            invalidateBuffer();
            notifyWindowChanged();
            repaint();
        }       
    }
    
    public Window getWindow() {
        return new Window(iVLUT.getWindow());
    }

    public void setRange(Range aR) {            
        iVLUT.setRange(aR);                    
    }


    public Range getRange() {
        return new Range(iVLUT.getRange());
    }


    public void setInverted(boolean aI) {
        if (aI != isInverted()) {    
            iVLUT.setInverted(aI);
            invalidateBuffer();
            notifyWindowChanged();
            repaint();
        }        
    }

    public boolean isInverted() {
        return iVLUT.isInverted();
    }

    public void setLinear(boolean aI) {
        if (aI != isLinear()) {
            iVLUT.setLinear(aI);
            invalidateBuffer();
            notifyWindowChanged();  
            repaint();
        }
    }

    public boolean isLinear() {
        return iVLUT.isLinear();
    }

    public BufferedImage transform (BufferedImage aSrc, BufferedImage aDst) {
        return iPLUT.transform(iVLUT.transform(aSrc, aDst), null);
    }
    
        
    private static final Logger logger = LogManager.getLogger(ImageView.class);
}




