package com.ivli.roim;

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
    
    
    protected     VOILut          iWM;                   
    protected     PresentationLut iLUT;  
    private       MultiframeImage iImage;
    private final Controller      iController;    
    private final AffineTransform iZoom;
    private final Point           iOrigin;   
    private       BufferedImage   iBuf; 
    private       ROIManager      iRoim;
    private final HashSet<WindowChangeListener> iWinListeners;// = new HashSet();    
    
        
    ImageView() {         
        iController = new Controller(this);   
        iWM  = new VOILut();                    
        iLUT = new PresentationLut(null);  
        iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
        iOrigin = new Point(0,0);   
        iRoim = new ROIManager(this);
        iWinListeners = new HashSet();    
    }
   
    void open(String aFileName) throws IOException {           
        iImage = MultiframeImage.New(aFileName);        
        iWM.setImage(iImage.getCurrentFrame());
        invalidateBuffer();
    }
    
    AffineTransform getZoom() {
        return iZoom;
    }
    
    MultiframeImage getImage() {
        return iImage;
    }
    
    ROIManager getManager() {
        return iRoim;
    }
    

    void setLUTControl(LUTControl aCtrl) {
        setVOILUT(aCtrl.iWM);
        setPresentationLUT(aCtrl.iLUT);
        aCtrl.addComponent(this);
    }

    void setVOILUT(VOILut aLut) {
        iWM = aLut;
        iWM.setImage(iImage.getCurrentFrame());        
    }

    void setPresentationLUT(PresentationLut aLut) {iLUT=aLut;}
        
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
        aL.windowChanged(new WindowChangeEvent(this, iWM.getWindow(), getMinimum(), getMaximum(), true));
    }

    public void removeWindowChangeListener(WindowChangeListener aL) {
        iWinListeners.remove(aL);
    }
    
    private void notifyWindowChanged(boolean aRC) {
        final WindowChangeEvent wce = new WindowChangeEvent(this, iWM.getWindow(), getMinimum(), getMaximum(), aRC);
        for (WindowChangeListener l : iWinListeners)
            l.windowChanged(wce);
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
    
    Window getWindow() {return iWM.getWindow();}
    
    void setWindow (Window aW) { 
        if (!iWM.getWindow().equals(aW)) {
            if (aW.getLevel() > getMinimum() && aW.getLevel() < getMaximum()) {
                iWM.setWindow(aW);               
                invalidateBuffer();
                notifyWindowChanged(false);
                repaint();
            }
        }
    }
                
    boolean isInverted() {
        return iWM.isInverted();
    }

    void setInverted(boolean aI) {
        if (iWM.setInverted(aI)) {              
            //updateBufferedImage();
            invalidateBuffer();
            notifyWindowChanged(false);
        }
    } 
    
    boolean isLinear() {return iWM.isLinear();}
    
    void setLinear(boolean aI) {
        iWM.setLinear(aI);
        //updateBufferedImage();
        invalidateBuffer();
        notifyWindowChanged(false);  
    }

    double getMinimum() {return iImage.getCurrentFrame().getStats().getMin();}// getMinimum();}
    double getMaximum() {return iImage.getCurrentFrame().getStats().getMax();}//getMaximum();}    
    
    int  getNoOfFrames() throws IOException {
        return iImage.getNoOfFrames();
    }    
           
    void loadFrame(int aN) throws IndexOutOfBoundsException {                
        iImage.loadFrame(aN);   
        ///iWM.reset(iImage);
        iWM.setImage(iImage.getCurrentFrame());
        notifyWindowChanged(true);      
        iRoim.update();                
        invalidateBuffer();
    }
    
    public void zoom(double aFactor, int aX, int aY) {
        iZoom.setToScale(iZoom.getScaleX() + aFactor, iZoom.getScaleY() + aFactor);        
        
        invalidateBuffer();
        repaint();
    }
     
    public void pan(int adX, int adY) {
        iOrigin.x += adX;
        iOrigin.y += adY;
       // repaint();
    }
             
    void resetView() {
        iRoim.clear();
        iOrigin.x = iOrigin.y = 0;
        iZoom.setToScale(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);  
        //updateBufferedImage();
        invalidateBuffer();
    }    
    
    void setLUT(String aLUT) {
        iLUT.setLUT(aLUT);        
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
        BufferedImage src = iLUT.transform(iWM.transform(iImage.getBufferedImage(), null), null);
        iBuf = z.filter(iLUT.transform(src, null), null);                  
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
            iRoim.draw((Graphics2D)g, trans);
           // }
        
            g.setColor(clr);
        }
    
               
        iController.paint((Graphics2D)g); //must paint the last   
    }
     
    private static final Logger logger = LogManager.getLogger(ImageView.class);
}




