
package com.ivli.roim;


import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.core.IMultiframeImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImagePanel extends JPanel {      
    protected ImageView iView;    
    protected LUTControl iLut;
    
    public enum VIEWMODE {
        FRAME,
        GRID
    }
       
    void open(IMultiframeImage anImage) /*throws IOException */{                                         
        //iView = new GridImageView(new MultiframeImage(DICOMImageProvider.New(aName)), 6, 6);  
        doOpen(new ImageView(anImage));
    }
    
    void openGrid(IMultiframeImage anImage, int aRows, int aCols) /*throws IOException */{                                         
        //iView = new GridImageView(new MultiframeImage(DICOMImageProvider.New(aName)), 6, 6);  
        doOpen(new GridImageView(anImage, Math.max(aRows, 1), Math.max(aCols, 1)));
    }
    
    protected void doOpen(ImageView aView) /*throws IOException */{           
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
