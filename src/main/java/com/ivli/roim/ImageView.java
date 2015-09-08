
package com.ivli.roim;

//import com.ivli.roim.Events.WindowChangeListener;
//import com.ivli.roim.Events.WindowChangeNotifier;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.Events.*;
import java.io.IOException;
import java.util.HashSet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
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
import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.data.xy.XYSeries;


public class ImageView extends JComponent implements WindowChangeNotifier {

    private static final boolean DRAW_OVERLAYS_ON_BUFFERED_IMAGE = false; //i cry ther's no #ifdef 
    private static final boolean DRAW_SUMMED_FRAME = false; 
    private static final boolean SUMMED_FRAME_PANE = false; 
    private static final double  DEFAULT_SCALE_X = 1.;
    private static final double  DEFAULT_SCALE_Y = 1.;
    private static final int     SELECTION_TOLERANCE_X = 3;
    private static final int     SELECTION_TOLERANCE_Y = 1;
    private static final int     ZOOM_TO_FIT = 1;
    
    enum EFit {
        Width, Height, Visible, Zoom; 
    }
    
    private EFit iFit = EFit.Visible;  // 
    
    private final IMultiframeImage iImage;                 
    
    private final Controller      iController;    
    private final AffineTransform iZoom;
    private final Point           iOrigin;   
    
    private final ROIManager      iROIMgr;
    private final IWLManager      iLUTMgr;
    
    private HashSet<WindowChangeListener> iWinListeners;      
    private HashSet<ZoomChangeListener>   iZoomListeners;
    private HashSet<FrameChangeListener>  iFrameListeners;
    
    private BufferedImage iBuf; 
    
    ImageView(IMultiframeImage aImage) {  
        iImage      = aImage;
        iController = new Controller(this);          
        
        iZoom   = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0, 0); 
        iLUTMgr = new WLManager();        
        iROIMgr = new ROIManager(this);
         //observers
        iWinListeners   = new HashSet(); 
        iZoomListeners  = new HashSet();
        iFrameListeners = new HashSet();  
        
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
        double scale = 1.0;
        
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
          
    @Override
    public void addWindowChangeListener(WindowChangeListener aL) {
        iWinListeners.add(aL);
        aL.windowChanged(new WindowChangeEvent(this, iLUTMgr.getWindow(), getMin(), getMax(), true));
    }

    @Override
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iWinListeners.remove(aL);
    }
            
    private void notifyWindowChanged(boolean aRC) {
        final WindowChangeEvent evt = new WindowChangeEvent(this, iLUTMgr.getWindow(), getMin(), getMax(), aRC);
        iWinListeners.stream().forEach((l) -> {
            l.windowChanged(evt);
            });
    }
   public void addFrameChangeListener(FrameChangeListener aL) {
        iFrameListeners.add(aL);
        aL.frameChanged(new FrameChangeEvent(this, iImage.getCurrent(), iImage.getNumFrames()));
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iFrameListeners.remove(aL);
    } 
    
    private void notifyFrameChanged(int aN) {
        final FrameChangeEvent evt = new FrameChangeEvent(this, aN, iImage.getNumFrames());        
        iFrameListeners.stream().forEach((f) -> {
            f.frameChanged(evt);
            });
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iZoomListeners.add(aL);
        aL.zoomChanged(new ZoomChangeEvent(this,iZoom.getScaleX()));
    }
    
    public void removeZoomChangeListener(ZoomChangeListener aL) {
        iZoomListeners.remove(aL);
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
     
    /*
    public void setWindow(Window aW) { 
        if (!iLUTMgr.getWindow().equals(aW) && iLUTMgr.getRange().contains(aW)) {            
            iLUTMgr.setWindow(aW);               
            invalidateBuffer();
            notifyWindowChanged(false);
            repaint();
        }
    }

    public void setInverted(boolean aI) {
        if (iLUTMgr.setInverted(aI)) {              
            //updateBufferedImage();
            invalidateBuffer();
            notifyWindowChanged(false);
        }
    } 
    
    public void setLinear(boolean aI) {
        iLUTMgr.setLinear(aI);        
        invalidateBuffer();
        notifyWindowChanged(false);  
    }

    public void setLUT(String aLUT) {
        iLUTMgr.setLut(aLUT);        
        invalidateBuffer();      
    }
    */
    
    private double getMin() {return iImage.image().getStats().getMin();} 
    private double getMax() {return iImage.image().getStats().getMax();} 
    
    public int getNumFrames() throws IOException {
        return iImage.getNumFrames();
    }    
           
    void loadFrame(int aN) throws IndexOutOfBoundsException {                
        iImage.getAt(aN);   
        ///iWM.reset(iImage);
        notifyFrameChanged(aN);
         
        //iLUTMgr.setImage(iImage.image());
        iLUTMgr.frameChanged();
        notifyWindowChanged(true);      
        iROIMgr.update();                
        invalidateBuffer();
    }
    
    public void zoom(double aFactor, int aX, int aY) {
        iFit = EFit.Zoom;
        
        iZoom.setToScale(iZoom.getScaleX() + aFactor, iZoom.getScaleY() + aFactor);        
        
        invalidateBuffer();
        repaint();

        for (ZoomChangeListener l: iZoomListeners)
            l.zoomChanged(new ZoomChangeEvent(this, iZoom.getScaleX()));
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
        public WLManager() {       
            iVLUT = new VOILut(iImage.image());
            iPLUT = new PresentationLut(null);
        }

        public void frameChanged() {   
            iVLUT.setImage(iImage.image());
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




