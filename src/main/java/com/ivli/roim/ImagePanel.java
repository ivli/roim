
package com.ivli.roim;


import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import com.ivli.roim.controls.LUTControl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImagePanel extends JPanel {      
    protected ImageView iView;    
    protected LUTControl iLut;
    
      
    public void open(ImageView aView) {
        doOpen(aView);
    }
           
    public ImageView getView() {
        return iView;
    }
    
    protected void doOpen(ImageView aView) {           
        removeAll();
        
        iView = aView;//new ImageView(anImage);  
        
         /*TODO: registration instead of instantiation */
        iLut  = new LUTControl(iView.getLUTMgr());                                     
        
        iView.addWindowChangeListener(iLut); 
        iView.addFrameChangeListener(iLut);
                
        setLayout(new BorderLayout());                         
        add(iView);           
        add(iLut, BorderLayout.LINE_END);  
    }    
    
    void openLUT(String aName) {
        iView.getLUTMgr().openLUT(aName);        
    }
    
    void resetView() {
        iView.resetView();
    }
    
    void loadFrame(int aN) {
        iView.loadFrame(aN);
    }
        
    public void setInterpolationMethod(Object aMethod) {
        if (null != iView) {
            iView.setInterpolationMethod(aMethod);     
            repaint();
        }
    }
    
    public void setFit(int aFit) {
        if (null != iView) {
            iView.setFit(aFit);     
            repaint();
        }
    }
        
    public Iterator<Overlay> getOverlaysList() {        
        return iView.getROIMgr().getOverlaysList();
    }
        
    private static final Logger logger = LogManager.getLogger(ImagePanel.class);   
}
