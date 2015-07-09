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

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;


public class MEDImageComponent extends JComponent implements MEDImageComponentBase, WindowChangeNotifier {

    private static final boolean DRAW_OVERLAYS_ON_BUFFERED_IMAGE = false; //i cry ther's no #ifdef 
    private static final boolean DRAW_SUMMED_FRAME = false; 
    final static private boolean SUMMED_FRAME_PANE = false; 
    final static private double DEFAULT_SCALE_X = 1.;
    final static private double DEFAULT_SCALE_Y = 1.;
    final static private int SELECTION_TOLERANCE_X = 3;
    final static private int SELECTION_TOLERANCE_Y = 1;
    final static private int ZOOM_TO_FIT = 1;
    
    
    private       MEDImageBase iImage;
    private       Controller iController;
            final VOILut          iWM  = new VOILut();                    
            final PresentationLut iLUT = new PresentationLut(null);  
    private final AffineTransform iZoom = AffineTransform.getScaleInstance(DEFAULT_SCALE_X, DEFAULT_SCALE_Y);
    private final Point iOrigin = new Point(0,0);     
    private       BufferedImage iBuf; //offscreen image
    private final HashSet<WindowChangeListener> iWinListeners = new HashSet();    
    private       ROIManager iRoim;
        
    public MEDImageComponent() {         
        iController = new Controller(this);         
    }
   
    public void open(String aFileName) throws IOException {   
        
        iImage = MEDImage.New(aFileName); 
        ///iImage.open();
        iWM.setImage(iImage);
        invalidateBuffer();
    }
    
    public AffineTransform getZoom() {return iZoom;}
    public MEDImageBase getImage() {return iImage;}
    public ROIManager getROIManager() {return iRoim;}
    public void setROIManager(ROIManager aRoim) {iRoim = aRoim; iRoim.attach(this);}
    
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
        for (WindowChangeListener l:iWinListeners)
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

    double getMinimum() {return iImage.getImageStats().iMin;}// getMinimum();}
    double getMaximum() {return iImage.getImageStats().iMax;}//getMaximum();}    
    
    int  getNoOfFrames() throws IOException {return iImage.getNoOfFrames();}    
    
    void loadFrame(int aN) throws IndexOutOfBoundsException {                
        iImage.loadFrame(aN);   
        ///iWM.reset(iImage);
        iWM.setImage(iImage);
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
        RenderingHints hts  = new RenderingHints(RenderingHints.KEY_INTERPOLATION, Settings.INTERPOLATION_METHOD);
        AffineTransformOp z = new AffineTransformOp(iZoom, hts);
                    
        BufferedImage src = iWM.transform(iImage.getBufferedImage(), null);
        
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
     
    private static final Logger logger = LogManager.getLogger(MEDImageComponent.class);
}




