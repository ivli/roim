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

import com.ivli.roim.core.IWLManager;
import com.ivli.roim.core.Range;
import com.ivli.roim.core.Window;
import java.io.IOException;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.Point;
import java.awt.image.ConvolveOp;
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

import com.ivli.roim.events.*;

public class ImageView extends JComponent /*implements WindowChangeNotifier*/ {

    private static final boolean DRAW_OVERLAYS_ON_BUFFERED_IMAGE = false; //i cry ther's no #ifdef     
    private static final double  DEFAULT_SCALE_X = 1.;
    private static final double  DEFAULT_SCALE_Y = 1.;
    
    
    enum EFit {
        Visible, Width, Height, Zoom; 
    }
    
    private EFit iFit = EFit.Zoom;  //     
    private final IMultiframeImage iImage;                     
    private final Controller      iController;    
    private final AffineTransform iZoom;
    private final Point           iOrigin;       
    
    private final IWLManager      iLUTMgr;        
    private final ROIManager      iROIMgr;
    private final EventListenerList iList;
    
    private BufferedImage iBuf; 
    
    ImageView(IMultiframeImage aImage) {  
        iImage      = aImage;
        iController = new Controller(this);          
        
        iZoom   = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0, 0); 
        iLUTMgr = new WLManager();        
        iROIMgr = new ROIManager(this);         
        iList = new EventListenerList();
    
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {invalidateBuffer();}                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {}                    
            });
        
    }
       
    public AffineTransform getZoom() {
        return iZoom;
    }
    
    public IMultiframeImage getImage() {
        return iImage;
    }
    
    public ROIManager getROIMgr() {
        return iROIMgr;
    }    
      
    public IWLManager getLUTMgr() {
        return iLUTMgr;
    }    
    
    public void fitWidth() {
        iFit = EFit.Width; 
        invalidateBuffer();
    }
    
    public void fitHeight() {
        iFit = EFit.Height; 
        invalidateBuffer();
    }
        
    private void scale() {
        double scale;
        
        switch (iFit) {
            case Visible:
                scale = Math.min((double)getWidth() / (double)iImage.getWidth()
                                    , (double)getHeight() / (double)iImage.getHeight()); break;                
            case Height: scale = (double)getHeight() / (double)iImage.getHeight(); break;
            case Width:  scale = (double)getWidth() / (double)iImage.getWidth(); break;
            default: 
                return;                            
        }        
        
        iZoom.setToScale(scale, scale); //does it make sense to implement non isomorphic scale?
        invalidateBuffer();
    }
          
    public void addROIChangeListener(ROIChangeListener aL) {
        ///iROIListeners.add(aL);
        iList.add(ROIChangeListener.class, aL);
    }
    
    void notifyROIChanged(ROI aR, EStateChanged aS) {
       ROIChangeEvent evt = new ROIChangeEvent(this, aR, aS);
       
       ROIChangeListener arr[] = iList.getListeners(ROIChangeListener.class);
       
       for (ROIChangeListener l : arr)
           l. ROIChanged(evt);
    }
       
    public void addWindowChangeListener(WindowChangeListener aL) {
        //iWinListeners.add(aL);
        iList.add(WindowChangeListener.class, aL);
        aL.windowChanged(new WindowChangeEvent(this, iLUTMgr.getWindow(), getMin(), getMax(), true));
    }
   
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iList.remove(WindowChangeListener.class, aL);
        //iWinListeners.remove(aL);
    }
            
    private void notifyWindowChanged(boolean aRC) {
        final WindowChangeEvent evt = new WindowChangeEvent(this, iLUTMgr.getWindow(), getMin(), getMax(), aRC);
        
        WindowChangeListener arr[] = iList.getListeners(WindowChangeListener.class);
        
        for (WindowChangeListener l : arr)
            l.windowChanged(evt);     
    }
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iList.add(FrameChangeListener.class, aL);
        aL.frameChanged(new FrameChangeEvent(this, iImage.getCurrent(), iImage.getNumFrames()));
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iList.remove(FrameChangeListener.class, aL);
    } 
    
    private void notifyFrameChanged(int aN) {
        final FrameChangeEvent evt = new FrameChangeEvent(this, aN, iImage.getNumFrames());        
        
        FrameChangeListener arr[] = iList.getListeners(FrameChangeListener.class);
        
        for (FrameChangeListener l : arr)
            l.frameChanged(evt);       
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iList.add(ZoomChangeListener.class, aL);
        aL.zoomChanged(new ZoomChangeEvent(this, iZoom.getScaleX(), iZoom.getScaleY()));
    }
    
    public void removeZoomChangeListener(ZoomChangeListener aL) {
        iList.remove(ZoomChangeListener.class,aL);
    }
     
    private void notifyZoomChanged(double aX, double aY) {
        final ZoomChangeEvent evt = new ZoomChangeEvent(this, aX, aY);        
        
        ZoomChangeListener arr[] = iList.getListeners(ZoomChangeListener.class);
        
        for (ZoomChangeListener l : arr)
            l.zoomChanged(evt);       
    }
    
    public AffineTransform screenToVirtual() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(iZoom);
        try { 
            ret.invert(); 
        } catch (Exception e) { 
            logger.error(e); // can I do anything more conscious here            
        }
        return ret;
    }
    
    public AffineTransform virtualToScreen() {
        AffineTransform ret = AffineTransform.getTranslateInstance(iOrigin.x, iOrigin.y); 
        ret.concatenate(iZoom);
        return ret;
    }
   
    private void invalidateBuffer() {iBuf = null;}
        
    private double getMin() {return iImage.image().getMin();} 
    private double getMax() {return iImage.image().getMax();} 
    
    public int getNumFrames() throws IOException {
        return iImage.getNumFrames();
    }    
           
    void loadFrame(int aN) throws IndexOutOfBoundsException {                        
        iImage.advance(aN);   
        ///iWM.reset(iImage);
        notifyFrameChanged(aN);
         
        //iLUTMgr.setImage(iImage.image());
        iLUTMgr.setRange(new Range(iImage.image().getMin(), iImage.image().getMax()));// frameChanged();
        
        notifyWindowChanged(true);
        
        iROIMgr.update();                
        invalidateBuffer();
    }
    
    public void zoom(double aFactor, int aX, int aY) {
        iFit = EFit.Zoom;
        
        iZoom.setToScale(iZoom.getScaleX() + aFactor, iZoom.getScaleY() + aFactor);        
        
        notifyZoomChanged(iZoom.getScaleX(), iZoom.getScaleY());
        invalidateBuffer();
        repaint();            
    }
     
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;
       // repaint();
    }
             
    void resetView() {
        iROIMgr.clear();
        iOrigin.x = iOrigin.y = 0;
        iZoom.setToScale(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);  
        //updateBufferedImage();
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
      
    private void updateBufferedImage() {          
        /*
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(iZoom, hts);
                    
        BufferedImage src = iWM.transform(iImage.getBufferedImage(), null);
        
        iBuf = z.filter(iLUT.transform(src, null), null);   
        */
        
        scale();
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(iZoom, hts);
        BufferedImage src  = iLUTMgr.transform(iImage.image().getBufferedImage(), null);
        //BufferedImage src = iPLut.transform(s1, null);
        
        iBuf = z.filter(src, null);                  
    }
    
    public void paintComponent(Graphics g) {           
        //super.paintComponent(g);
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);
        
        if (!DRAW_OVERLAYS_ON_BUFFERED_IMAGE) 
            iROIMgr.paint((Graphics2D)g, virtualToScreen());
          
               
        iController.paint((Graphics2D)g); //must paint the last   
    }
     
      
    public class WLManager implements IWLManager {    
        VOILut          iVLUT;
        PresentationLut iPLUT;

        //private 
        WLManager() {       
            iVLUT = new VOILut(iImage.image());
            iPLUT = new PresentationLut(null);
        }
/*
        public void frameChanged() {   
            iVLUT.setImage(iImage.image());
        }
*/
        
        public void setRange(Range aR) {
            iVLUT.setRange(aR);
                    
        }
        
        public void setLUT(String aL) {
            iPLUT.open(aL);
            invalidateBuffer();  
            repaint();
        }

        public void setWindow(Window aW) {
            if (!iVLUT.getWindow().equals(aW) && iVLUT.getRange().contains(aW)) {            
                iVLUT.setWindow(aW);               
                invalidateBuffer();
                notifyWindowChanged(false);
                repaint();
            }       
        }

        public Window getWindow() {
            return iVLUT.getWindow();
        }

        public Range getRange() {
            return new Range(iVLUT.getRange());
        }

        public void setInverted(boolean aI) {
            if (iVLUT.setInverted(aI)) {                          
                invalidateBuffer();
                notifyWindowChanged(false);
                repaint();
            }        
        }

        public boolean isInverted() {
            return iVLUT.isInverted();
        }

        public void setLinear(boolean aI) {
            if (iVLUT.setLinear(aI)) {
                invalidateBuffer();
                notifyWindowChanged(false);  
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




