
package com.ivli.roim;

import com.ivli.roim.core.IImageProvider;
import com.ivli.roim.controls.LUTControl;
import java.util.Iterator;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageView extends JPanel {
    private static final boolean SHOW_COMPOSITE = false;
   
    ImageComponent iView;
    ImageComponent iComp;
    LUTControl iLut;
    
           
    void open(String aName) throws IOException {                                         
        iView = new ImageComponent(new MultiframeImage(DICOMImageProvider.New(aName)));        
        iLut  = new LUTControl(iView.getLUTMgr());                                     
        
        iView.addWindowChangeListener(iLut); //!!!!
                
        setLayout(new BorderLayout());                 
                
        if (!SHOW_COMPOSITE) 
            add(iView);           
        else {
            final int width  = iView.getImage().getWidth() / 2;  //iProvider.getWidth() / 2;
            final int height = iView.getImage().getHeight(); //iProvider.getHeight();     
            iComp = new ImageComponent(iView.getImage().makeCompositeFrame(0, -1));
            iView.setPreferredSize(new java.awt.Dimension(width, height));  
            iView.setMinimumSize(new java.awt.Dimension(width, height));

            iComp.setPreferredSize(new java.awt.Dimension(width, height));  
            iComp.setMinimumSize(new java.awt.Dimension(width, height)); 
            
            JComponent temp = new JPanel();
            temp.setLayout(new GridLayout());
            temp.add(iView);//, BorderLayout.CENTER);
            temp.add(iComp);
            add(temp);                        
        }        
   
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
       
    public Iterator<Overlay> getOverlaysList() {        
        return iView.getROIMgr().getOverlaysList();
    }
    
    
    private static final Logger logger = LogManager.getLogger(ImageView.class);   
}
