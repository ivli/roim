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

import org.jfree.data.xy.XYSeries;

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.IWLManager;
import com.ivli.roim.core.Range;
import com.ivli.roim.core.Window;
import com.ivli.roim.events.*;
import com.ivli.roim.core.ImageFrame;


public class ImageView extends JComponent {     
    private static final double  DEFAULT_SCALE_X = 1.;
    private static final double  DEFAULT_SCALE_Y = 1.;
    
    public static final int FIT_NO_FIT  = 0;
    public static final int FIT_VISIBLE = 1;
    public static final int FIT_WIDTH   = 2;
    public static final int FIT_HEIGHT  = 3; 
    
    private int iFit = Settings.DEFAULT_FIT;     
    protected final IMultiframeImage iModel;                     
    protected       Controller iController;    
    protected final AffineTransform iZoom;
    protected final Point iOrigin;    
    protected       Object iInterpolation;// = Settings.INTERPOLATION_METHOD;
    
    protected final IWLManager iLUTMgr;        
    protected final ROIManager iROIMgr;
    protected final EventListenerList iList;
    
    protected BufferedImage iBuf; 
    
    protected int iCurrent;
   // private ImageFrame iFrame;
    
    ImageView(IMultiframeImage anImage) {  
        iModel = anImage;
        iCurrent = 0;       
        
        iController = new Controller(this);
        
        iZoom   = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0, 0); 
        iInterpolation = Settings.INTERPOLATION_METHOD;
        iLUTMgr = new WLManager();        
        iROIMgr = new ROIManager(this);         
        iList = new EventListenerList();
        
        
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {
                invalidateBuffer();
                notifyZoomChanged();
            }                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}                    
        });        
    }
                 
    public AffineTransform getZoom() {
        return iZoom;
    }    
    
    public IMultiframeImage getModel() {
        return iModel;
    }
    
    public ImageFrame getImage() {
        return iModel.get(getCurrent());
    }
    
    public ROIManager getROIMgr() {
        return iROIMgr;
    }    
      
    public IWLManager getLUTMgr() {
        return iLUTMgr;
    }    
    
    public void setFit(int aFit) {        
        if (aFit < FIT_NO_FIT || aFit > FIT_HEIGHT)
            throw new java.lang.IllegalArgumentException();
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
    
    protected int getVisualWidth() {return iModel.getWidth();}
    protected int getVisualHeight() {return iModel.getHeight();}
    
    protected void updateScale() {
        double scale;
        
        switch (iFit) {
            case FIT_VISIBLE:
                final double scaleX = (double)getWidth() / (double)getVisualWidth(); 
                final double scaleY = (double)getHeight() / (double)getVisualHeight(); 
                scale = Math.min(scaleX, scaleY); break;                
            case FIT_HEIGHT: scale = (double)getHeight() / (double)getVisualHeight(); break;
            case FIT_WIDTH:  scale = (double)getWidth() / (double)getVisualWidth(); break;
            case FIT_NO_FIT: //falltrough to default
            default: 
                return;                            
        }        
        
        iZoom.setToScale(scale, scale); //does it make sense to implement non isomorphic scale?
    }
                     
    public void addWindowChangeListener(WindowChangeListener aL) {
        //iWinListeners.add(aL);
        iList.add(WindowChangeListener.class, aL);
        aL.windowChanged(new WindowChangeEvent(this, iLUTMgr.getWindow()/*, iImage.image().getMin(), iImage.image().getMax(), true*/));
    }
   
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iList.remove(WindowChangeListener.class, aL);
        //iWinListeners.remove(aL);
    }
            
    protected void notifyWindowChanged() {
        final WindowChangeEvent evt = new WindowChangeEvent(this, iLUTMgr.getWindow()/*, iImage.image().getMin(), iImage.image().getMax(), aRC*/);
                
        for (WindowChangeListener l : iList.getListeners(WindowChangeListener.class))
            l.windowChanged(evt);     
    }
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iList.add(FrameChangeListener.class, aL);
        aL.frameChanged(new FrameChangeEvent(this, iCurrent, iModel.getNumFrames(),
                                                            //new Range(iModel.get(iCurrent).getMin(), iModel.get(iCurrent).getMax()),
                                                    getLUTMgr().getRange(),                                                
                                                    iModel.getTimeSliceVector().getSlice(getCurrent())));                           
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iList.remove(FrameChangeListener.class, aL);
    } 
    
    protected void notifyFrameChanged() {
        final FrameChangeEvent evt = new FrameChangeEvent(this, getCurrent(), iModel.getNumFrames(),
                                                            //new Range(iModel.get(iCurrent).getMin(), iModel.get(iCurrent).getMax()),
                                                            getLUTMgr().getRange(),
                                                            iModel.getTimeSliceVector().getSlice(getCurrent()));                
      
        for (FrameChangeListener l : iList.getListeners(FrameChangeListener.class))
            l.frameChanged(evt);       
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iList.add(ZoomChangeListener.class, aL);
        aL.zoomChanged(new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY()));
    }
    
    public void removeZoomChangeListener(ZoomChangeListener aL) {
        iList.remove(ZoomChangeListener.class,aL);
    }
     
    protected void notifyZoomChanged() {
        final ZoomChangeEvent evt = new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY());        
                
        for (ZoomChangeListener l : iList.getListeners(ZoomChangeListener.class))
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
   
    protected void invalidateBuffer() {
        iBuf = null;
    }
    
    public int getCurrent() {
        return iCurrent;
    }  
           
    public boolean loadFrame(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {        
            iCurrent = aN;
         
            iLUTMgr.setRange(new Range(iModel.get(iCurrent).getMin(), iModel.get(iCurrent).getMax()));// frameChanged();

            iROIMgr.update();   

            notifyFrameChanged();
            notifyWindowChanged();

            invalidateBuffer();
        }
        
        return true;
    }
    
    public void zoom(double aFactor) {
        iFit = FIT_NO_FIT;
        
        iZoom.setToScale(iZoom.getScaleX() + aFactor, iZoom.getScaleY() + aFactor);        
        
        notifyZoomChanged();
        invalidateBuffer();
        ///repaint();            
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
        iFit = Settings.DEFAULT_FIT;
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
      
    protected void updateBufferedImage() {                  
        updateScale();
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, iInterpolation);
        AffineTransformOp z = new AffineTransformOp(getZoom(), hts);
        BufferedImage src  = getLUTMgr().transform(iModel.get(iCurrent).getBufferedImage(), null);
                
        iBuf = z.filter(src, null);                  
    }
    
    @Override
    public void paintComponent(Graphics g) {                   
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);        
        iROIMgr.paint((Graphics2D)g, virtualToScreen());                         
        iController.paint((Graphics2D)g); //must reside last in the paint queue   
    }
           
    public class WLManager implements IWLManager {    
        private VOILut          iVLUT;
        private PresentationLut iPLUT;
        
        private boolean iLockRange  = false;
        private boolean iLockWindow = false;
        
        //private 
        WLManager() {       
            iVLUT = new VOILut(iModel.get(iCurrent));
            iPLUT = new PresentationLut(null);
        }
            
        public void lockRange(boolean aLock) {
            iLockRange = aLock;
        }
        
        public void lockWindow(boolean aLock) {
            iLockWindow = aLock;
        }
        
        @Override
        public void openLUT(String aL) {
            iPLUT.open(aL);
            invalidateBuffer();  
            repaint();
        }
        
        @Override
        public void setWindow(Window aW) {
            if (!iLockWindow && !iVLUT.getWindow().equals(aW) && iVLUT.getRange().contains(aW)) {            
                iVLUT.setWindow(aW);               
                invalidateBuffer();
                notifyWindowChanged();
                repaint();
            }       
        }

        @Override
        public Window getWindow() {
            return new Window(iVLUT.getWindow());
        }
        
        @Override
        public void setRange(Range aR) {
            if (!iLockRange)
                iVLUT.setRange(aR);                    
        }
        
        @Override
        public Range getRange() {
            return new Range(iVLUT.getRange());
        }

        @Override
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

        public XYSeries makeXYSeries(XYSeries ret) {
            return iVLUT.makeXYSeries(ret);
        }

        @Override
        public java.awt.image.BufferedImage transform (java.awt.image.BufferedImage aSrc, java.awt.image.BufferedImage aDst) {
            return iPLUT.transform(iVLUT.transform(aSrc, aDst), null);
        }
    }
    
    private static final Logger logger = LogManager.getLogger(ImageView.class);
}




