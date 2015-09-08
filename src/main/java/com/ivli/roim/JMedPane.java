
package com.ivli.roim;

import com.ivli.roim.controls.LUTControl;
import java.util.Iterator;
import java.io.IOException;
import java.awt.Component;
import java.awt.BorderLayout;
//import javax.swing.FlowLayout;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMedPane extends JLayeredPane{
    ImageView  iView;
    ImageView  iComp;
    LUTControl iLut;
    IImageProvider iProvider;
           
    void open(String aName) throws IOException {            
        boolean SHOW_COMPOSITE = false;
        
        iProvider = DICOMImageProvider.New(aName);
        
        iView = new ImageView(new MultiframeImage(iProvider));
        
        
        setLayout(new BorderLayout()); 
        
        final int width  = iProvider.getWidth();
        final int height = iProvider.getHeight();
        
        
        if (!SHOW_COMPOSITE) {   //yeah, there's no #ifdef          
            //iView.setPreferredSize(new java.awt.Dimension(width, height));  
            //iView.setMinimumSize(new java.awt.Dimension(width, height));
            iView.setAlignmentX(Component.TOP_ALIGNMENT);
        
        } else {        
            iComp = new ImageView(iView.getImage().makeCompositeFrame(0, -1));
            //iView.setPreferredSize(new java.awt.Dimension(width, height));  
            //iView.setMinimumSize(new java.awt.Dimension(width, height));
            iView.setAlignmentX(Component.TOP_ALIGNMENT);

            //iComp.setPreferredSize(new java.awt.Dimension(width, height));  
            //iComp.setMinimumSize(new java.awt.Dimension(width, height));
            iComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        
        iLut = new LUTControl(iView.getLUTMgr());        
        iLut.setAlignmentX(Component.TOP_ALIGNMENT);                      
        iView.addWindowChangeListener(iLut); //!!!!
        
        add(iView, BorderLayout.CENTER);
        if(SHOW_COMPOSITE)
            add(iComp, JLayeredPane.DEFAULT_LAYER);
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
    
    void fitWidth() {        
        iView.fitWidth();
    }
       
    Iterator<Overlay> getOverlaysList() {        
        return iView.getROIMgr().getOverlaysList();
    }
    
    
    private static final Logger logger = LogManager.getLogger(JMedPane.class);   
}
