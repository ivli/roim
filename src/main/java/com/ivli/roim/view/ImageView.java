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


import com.ivli.roim.core.IFrameProvider;
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
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
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
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.Uid;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.events.ZoomChangeEvent;
import com.ivli.roim.events.ZoomChangeListener;

public class ImageView extends JComponent implements IImageView {
    private static final double DEFAULT_SCALE_X = 1.0;
    private static final double DEFAULT_SCALE_Y = 1.0;    
    private static final double MIN_SCALE = .01;
       
    protected ZoomFit iFit;             
    protected Object iInterpolation;   
    
    protected Point iOrigin;              
    protected AffineTransform iZoom;    
           
    protected ROIManager iROIMgr;
    protected VOILut iVLUT;
   
    protected int iCurrent;
    protected IMultiframeImage iModel;                     
    protected IController iController; 
    
    protected BufferedImage iBuf; //offscreen buffer 
    protected BufferedImage iBuf2; //pre zoomed image      
         
    private EventListenerList iListeners;

    public static ImageView create(IMultiframeImage aI, ViewMode aMode) {        
        return create(aI, aMode, new ROIManager(aI, new Uid(), false));    
    }
        
    public static ImageView create(IMultiframeImage aI, ViewMode aMode, ROIManager aM) {
        ImageView ret = new ImageView();
        ret.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        ret.setFit(ImageView.ZoomFit.VISIBLE);
        ret.setController(new Controller(ret));
        ret.setROIMgr(aM);
        ret.setImage(aI);
        ret.setViewMode(aMode);
        return ret;    
    }
    
    protected ImageView() {                                
        iOrigin = new Point(0, 0);              
        iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);          
        iVLUT = new VOILut(null);        
        iCurrent = 0;              
        iListeners = new EventListenerList(); 
        
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {
                invalidateBuffer();
                notifyZoomChanged();
                iZoomStep = Math.min(getFrame().getWidth(), getFrame().getHeight()) / Settings.get(Settings.KEY_ZOOM_STEP_FACTOR, 10.);
            }                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}                    
        });           
    }
      
    final void setController(IController aC) {
        if (null != iController) {
            removeMouseListener(iController);
            removeMouseMotionListener(iController);
            removeMouseWheelListener(iController);
            removeKeyListener(iController);
        }
        
        iController  = aC;
        addMouseListener(iController);
        addMouseMotionListener(iController);
        addMouseWheelListener(iController);
        addKeyListener(iController);
    }
    
    enum VIEWTYPE {
        DEFAULT, //
        FRAME, // display single frame of a number 
        CINE,  // display single frame, can move from first to last
        RANGE, // display single frame, can move within the range
        COMPOSITE, // composite frame (a summ of frames within the range)
        SLICE, // single slice at a number
        VOLUME // MIP volume
    }

    public static class ViewMode {
        public static final ViewMode DEFAULT = new ViewMode(VIEWTYPE.DEFAULT, IFrameProvider.FIRST, IFrameProvider.LAST);
                
        public static ViewMode frame(int aFrameNumber) {
            return new ViewMode(VIEWTYPE.FRAME, aFrameNumber, IFrameProvider.LAST);
        }
        
        public static ViewMode cine() {
            return new ViewMode(VIEWTYPE.CINE, IFrameProvider.FIRST, IFrameProvider.LAST);
        }
        
        public static ViewMode range(int aFrom, int aTo) {
            return new ViewMode(VIEWTYPE.RANGE, aFrom, aTo);
        }
        
        public static ViewMode composite(int aSummFrom, int aSummTo) {
            return new ViewMode(VIEWTYPE.COMPOSITE, aSummFrom, aSummTo);
        }
        
        public static ViewMode slice(int aSliceNumber) {
            return new ViewMode(VIEWTYPE.FRAME, aSliceNumber, IFrameProvider.LAST);
        }
        
        public static ViewMode volume(int aSliceFrom, int aSliceTo) {
            return new ViewMode(VIEWTYPE.VOLUME, aSliceFrom, aSliceTo);
        }
        
        public boolean isCompatible(IMultiframeImage aI) {
           
            switch (iType) {            
                case FRAME: 
                    switch (aI.getImageType()) {
                        case IMAGE:
                        case STATIC:
                        case DYNAMIC:
                        case GATED:
                        case WHOLEBODY:
                            if (aI.hasAt(iFrameFrom))
                                return true;                   
                    }                
                case CINE:  
                case RANGE:
                case COMPOSITE:
                    switch (aI.getImageType()) {                        
                        case DYNAMIC:
                        case GATED:     
                            if (aI.hasAt(iFrameFrom) && aI.hasAt(iFrameTo))
                                return true;                   
                    }                
                
                case SLICE:    
                case VOLUME:  
                    switch (aI.getImageType()) {                        
                        case VOLUME:
                        case VOLUME_G:                       
                            return true;                   
                    }  
                case DEFAULT:
                    return true;
            }
            
            return false;
        }
        
        private ViewMode(VIEWTYPE aType, int aF, int aT) {
            iType = aType;
            iFrameFrom = aF;
            iFrameTo = aT;
        }
        
        VIEWTYPE iType;
        int iFrameFrom;
        int iFrameTo;
    }
    
    protected ViewMode iMode = ViewMode.DEFAULT;
    
    public static final ViewMode DEFAULT_IMAGE_MODE = ViewMode.frame(0);
    public static final ViewMode DEFAULT_STATIC_IMAGE_MODE = ViewMode.frame(0);
    public static final ViewMode DEFAULT_DYNAMIC_IMAGE_MODE = ViewMode.cine();
    public static final ViewMode DEFAULT_VOLUME_IMAGE_MODE = ViewMode.slice(0);
    public static final ViewMode DEFAULT_COMPOSITE_IMAGE_MODE = ViewMode.composite(IFrameProvider.FIRST, IFrameProvider.LAST);
    
    public void setViewMode(ViewMode aM) {
        if (null != aM && aM.isCompatible(iModel) && aM != DEFAULT_IMAGE_MODE) {
            iMode = aM;
        } else {
            switch (iModel.getImageType()) {
                case IMAGE:            
                case STATIC:    
                case WHOLEBODY:
                    iMode = DEFAULT_STATIC_IMAGE_MODE; break;
                case DYNAMIC: 
                case GATED: 
                    iMode = DEFAULT_DYNAMIC_IMAGE_MODE; break;
                case TOMO:
                case TOMO_G:
                case VOLUME:
                case VOLUME_G:
                    iMode = DEFAULT_VOLUME_IMAGE_MODE; break;
                //CR/CT
                case AXIAL:
                case LOCALIZER:
                case UNKNOWN:  
            }
        } 
        
        switch (iMode.iType) {        
            case DEFAULT:
            case FRAME:
            case CINE: 
            case RANGE: break;
            case COMPOSITE:
                setImage(iModel.processor().collapse(aM.iFrameFrom, aM.iFrameTo)); break;
            case SLICE:
            case VOLUME:    
        }
    }
    
    public void setLUT(String aLUT) {         
        iVLUT.setLUT(aLUT);
        invalidateBuffer();
    }    
    
    public void setROIMgr(ROIManager aMgr) {
        iROIMgr = aMgr;  
        //iROIMgr.setView(this);
    }
    
    @Override
    public void setImage(IMultiframeImage anImage) {                
        iModel = anImage;             
        iVLUT.setTransform(iModel.getTransform());  
        //iMode = 
        setFrameNumber(iCurrent);
    }
                 
    @Override
    public AffineTransform getZoom() {
        return iZoom;
    }    
    
    public IMultiframeImage getImage() {
        return iModel;
    }
    
    public ImageFrame getFrame() {
        return iModel.get(getFrameNumber());
    }
    
    public ROIManager getROIMgr() {
        return iROIMgr;
    } 
        
    public VOILut getLUTMgr() {
        return iVLUT;
    }
    
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
        
    protected int getVisualWidth() {
        return iModel.getWidth();
    }
    
    protected int getVisualHeight() {
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
        final FrameChangeEvent evt = new FrameChangeEvent(this, getFrameNumber());/*, iModel.getNumFrames(),                                                           
                                                          this.getRange(),
                                                          //iModel.getTimeSliceVector().getSlice());                
                                                            getFrameNumber());
        */
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
    
    public AffineTransform virtualToScreen() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(iZoom);
        return ret;
    }
   
    public int getFrameNumber() {
        return iCurrent;
    }  
           
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
    
    @Override
    public boolean setFrameNumber(int aN) {                                
        if (!iModel.hasAt(aN)) {            
            return false;
        } else {             
            iCurrent = aN;   
            iVLUT.setWindow(new Window(iModel.get(iCurrent).getRange()));
            iROIMgr.update();               
                   
            invalidateBuffer();
            notifyFrameChanged();     
        }
        
        return true;
    }
    
    double iZoomStep;
    
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
        iFit = ZoomFit.PIXELS;//Settings.get(Settings.KEY_DEFAULT_IMAGE_SCALE, ZoomFit.PIXELS);
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
        //BufferedImage src = transform(iModel.get(iCurrent).getBufferedImage(), null);   
        //BufferedImage src = iPLUT.transform(iVLUT.transform(iBufImage, null), null);
        iBuf2 = iVLUT.transform(iModel.get(iCurrent), iBuf2);
        iBuf = z.filter(iBuf2, iBuf); 
    }
    
    @Override
    public void paintComponent(Graphics g) {                   
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);       
        ROIPainter p = new ROIPainter((Graphics2D)g, virtualToScreen(), this);
        iROIMgr.paint(p);                        
        iController.paint((Graphics2D)g); // must be the last in the paint chain   
    }

    public void setWindow(Window aW) {    
        if (getFrame().getRange().contains(aW)) {
            iVLUT.setWindow(aW);               
            invalidateBuffer();
            notifyWindowChanged();       
        }
    }
    
    public Window getWindow() {
        return iVLUT.getWindow();
    }

    public Range getRange() {
        return iModel.get(iCurrent).getRange();
    }

    public void setInverted(boolean aI) {          
        iVLUT.setInverted(aI);
        invalidateBuffer();
        notifyWindowChanged();  
    }

    public boolean isInverted() {
        return iVLUT.isInverted();
    }

    public void setLinear(boolean aI) {      
        iVLUT.setLinear(aI);
        invalidateBuffer();
        notifyWindowChanged();        
    }

    public boolean isLinear() {
        return iVLUT.isLinear();
    }
       
    public BufferedImage transform (ImageFrame aSrc) {
        return iVLUT.transform(aSrc, null);
    }
              
    private static final Logger LOG = LogManager.getLogger();
}




