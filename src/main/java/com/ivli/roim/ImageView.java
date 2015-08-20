
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
import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ImageView extends JComponent implements WindowChangeNotifier {

    private static final boolean DRAW_OVERLAYS_ON_BUFFERED_IMAGE = false; //i cry ther's no #ifdef 
    private static final boolean DRAW_SUMMED_FRAME = false; 
    private static final boolean SUMMED_FRAME_PANE = false; 
    private static final double  DEFAULT_SCALE_X = 1.;
    private static final double  DEFAULT_SCALE_Y = 1.;
    private static final int     SELECTION_TOLERANCE_X = 3;
    private static final int     SELECTION_TOLERANCE_Y = 1;
    private static final int     ZOOM_TO_FIT = 1;
    
    private       IMultiframeImage iImage;
    protected     VOILut           iVLut;                   
    protected     PresentationLut  iPLut;  
    private final Controller       iController;    
    private final AffineTransform  iZoom;
    private final Point            iOrigin;   
    private       BufferedImage    iBuf; 
    private       ROIManager       iROIMgr;
    private       HashSet<WindowChangeListener> iWinListeners;      
    private       HashSet<ZoomChangeListener>   iZoomListeners;
    private       HashSet<FrameChangeListener>  iFrameListeners;
    
    ImageView(IMultiframeImage aImage) {  
        iImage = aImage;
        iController = new Controller(this);   
        iVLut  = new VOILut();                    
        iPLut = new PresentationLut(null);  
        iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0, 0);   
        iROIMgr = new ROIManager(this);
        iWinListeners = new HashSet(); 
        iZoomListeners = new HashSet();
        iFrameListeners = new HashSet();
        
        iVLut.setImage(iImage.image());
    }
       
    public AffineTransform getZoom() {
        return iZoom;
    }
    
    public IMultiframeImage getImage() {
        return iImage;
    }
    
    public ROIManager getManager() {
        return iROIMgr;
    }    
/*
    void setVOILUT(VOILut aLut) {
        iVLut = aLut;
        iVLut.setImage(iImage.image());        
    }

    void setPresentationLUT(PresentationLut aLut) {
        iPLut = aLut;
    }
 */   
    public void setLUTControl(LUTControl aCtrl) {
        //setVOILUT(aCtrl.iVLut);
        //setPresentationLUT(aCtrl.iPLut);
        aCtrl.attach(iPLut, iVLut);
        aCtrl.addComponent(this);
    }
    
    public void fitWidth() {
        final double scale = getWidth()/iImage.getWidth();
        iZoom.setToScale(scale, scale);
        invalidateBuffer();
    }
    
    public void fitHeight() {
        double scale = ((double)getHeight())/(double)iImage.getHeight();
        iZoom.setToScale(scale, scale);
        invalidateBuffer();
    }
    
    public void addWindowChangeListener(WindowChangeListener aL) {
        iWinListeners.add(aL);
        aL.windowChanged(new WindowChangeEvent(this, iVLut.getWindow(), getMin(), getMax(), true));
    }

    public void removeWindowChangeListener(WindowChangeListener aL) {
        iWinListeners.remove(aL);
    }
            
    private void notifyWindowChanged(boolean aRC) {
        final WindowChangeEvent wce = new WindowChangeEvent(this, iVLut.getWindow(), getMin(), getMax(), aRC);
        for (WindowChangeListener l : iWinListeners)
            l.windowChanged(wce);
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iZoomListeners.add(aL);
    }
    
    public void removeZoomChangeListener(ZoomChangeListener aL) {
        iZoomListeners.remove(aL);
    }
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iFrameListeners.add(aL);
    }
    
    public void removeFrameChangeListener(FrameChangeListener aL) {
        iFrameListeners.remove(aL);
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
    
    public Window getWindow() {return iVLut.getWindow();}
    
    public void setWindow (Window aW) { 
        if (!iVLut.getWindow().equals(aW)) {
            if (aW.getLevel() > getMin() && aW.getLevel() < getMax()) {
                iVLut.setWindow(aW);               
                invalidateBuffer();
                notifyWindowChanged(false);
                repaint();
            }
        }
    }
                
    public boolean isInverted() {
        return iVLut.isInverted();
    }

    public void setInverted(boolean aI) {
        if (iVLut.setInverted(aI)) {              
            //updateBufferedImage();
            invalidateBuffer();
            notifyWindowChanged(false);
        }
    } 
    
    public boolean isLinear() {return iVLut.isLinear();}
    
    public void setLinear(boolean aI) {
        iVLut.setLinear(aI);
        //updateBufferedImage();
        invalidateBuffer();
        notifyWindowChanged(false);  
    }

    public double getMin() {return iImage.image().getStats().getMin();}// getMinimum();}
    public double getMax() {return iImage.image().getStats().getMax();}//getMaximum();}    
    
    public int getNumFrames() throws IOException {
        return iImage.getNumFrames();
    }    
           
    void loadFrame(int aN) throws IndexOutOfBoundsException {                
        iImage.getAt(aN);   
        ///iWM.reset(iImage);
        iVLut.setImage(iImage.image());
        notifyWindowChanged(true);      
        iROIMgr.update();                
        invalidateBuffer();
        iFrameListeners.stream().forEach((f) -> {
            f.frameChanged(new FrameChangeEvent(this, aN));
        });
    }
    
    public void zoom(double aFactor, int aX, int aY) {
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
    
    public void setLUT(String aLUT) {
        iPLut.setLUT(aLUT);        
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
        
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(iZoom, hts);
        BufferedImage src = iPLut.transform(iVLut.transform(iImage.image().getBufferedImage(), null), null);
        iBuf = z.filter(iPLut.transform(src, null), null);                  
    }
    
    public void paintComponent(Graphics g) {           
        //super.paintComponent(g);
        if (null == iBuf) 
            updateBufferedImage();
              
        g.drawImage(iBuf, iOrigin.x, iOrigin.y, iBuf.getWidth(), iBuf.getHeight(), null);
        
        if (!DRAW_OVERLAYS_ON_BUFFERED_IMAGE) {
            final AffineTransform trans = virtualToScreen();            
            final Color clr = g.getColor();
            
            //for (Overlay o : iOverlays) {                
            //    o.draw((Graphics2D)g, trans);
            iROIMgr.draw((Graphics2D)g, trans);
           // }
        
            g.setColor(clr);
        }
    
               
        iController.paint((Graphics2D)g); //must paint the last   
    }
     
    private static final Logger logger = LogManager.getLogger(ImageView.class);
}




