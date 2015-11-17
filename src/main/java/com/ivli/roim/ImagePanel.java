
package com.ivli.roim;

import com.ivli.roim.controls.LUTControl;
import java.util.Iterator;
import java.io.IOException;
import java.awt.BorderLayout;
import javax.swing.JPanel;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImagePanel extends JPanel {
    private static final boolean SHOW_COMPOSITE = false;
   
    protected ImageView iView;    
    protected LUTControl iLut;
    
           
    void open(String aName) throws IOException {                                         
        iView = new GridImageView(new MultiframeImage(DICOMImageProvider.New(aName)), 2, 3);        
        
         /*TODO: registration instead of instantiation */
        iLut  = new LUTControl(iView.getLUTMgr());                                     
        
        iView.addWindowChangeListener(iLut); 
        iView.addFrameChangeListener(iLut);
                
        setLayout(new BorderLayout());                         
        add(iView);           
        add(iLut, BorderLayout.LINE_END);  
    }
    
    void setLUT(String aName) {
        iView.getLUTMgr().setLUT(aName);        
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
