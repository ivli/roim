
package com.ivli.roim;

import com.ivli.roim.controls.LUTControl;
import java.util.Iterator;
import java.io.IOException;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMedPane extends JLayeredPane{
    ImageView  iView;
    ImageView  iComp;
    LUTControl iLut;
    IImageProvider iProvider;
           
    void open(String aName) throws IOException {            
        boolean SHOW_COMPOSITE = true;
        
        iProvider = DICOMImageProvider.New(aName);
        
        iView = new ImageView( new MultiframeImage(iProvider));
        
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 
        
        if (!SHOW_COMPOSITE) {   //yeah, there's no #ifdef          
            iView.setPreferredSize(new java.awt.Dimension(600, 600));  
            iView.setMinimumSize(new java.awt.Dimension(575, 600));
            iView.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        } else {        
            iComp = new ImageView(iView.getImage().makeCompositeFrame(0, -1));
            iView.setPreferredSize(new java.awt.Dimension(300, 600));  
            iView.setMinimumSize(new java.awt.Dimension(300, 600));
            iView.setAlignmentX(Component.LEFT_ALIGNMENT);

            iComp.setPreferredSize(new java.awt.Dimension(300, 600));  
            iComp.setMinimumSize(new java.awt.Dimension(300, 600));
            iComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        
        iLut = new LUTControl(iView.getLUTMgr());        
        iLut.setAlignmentX(Component.RIGHT_ALIGNMENT);                      
        iView.addWindowChangeListener(iLut); //!!!!
        
        add(iView, JLayeredPane.DEFAULT_LAYER);
        if(SHOW_COMPOSITE)
            add(iComp, JLayeredPane.DEFAULT_LAYER);
        add(iLut, JLayeredPane.DEFAULT_LAYER);
          
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
    
    void fitWidth() {        
        iView.fitWidth();
    }
       
    Iterator<Overlay> getOverlaysList() {        
        return iView.getROIMgr().getOverlaysList();
    }
    
    
    private static final Logger logger = LogManager.getLogger(JMedPane.class);   
}
