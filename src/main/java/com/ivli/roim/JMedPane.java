/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.Iterator;
import java.io.IOException;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMedPane extends JLayeredPane{
    ImageView iImg;
    LUTControl        iLut;
    //ROIManager        iRoim;
           
    void open(String aName) throws IOException {    
        iImg = new ImageView();
        //iRoim = new ROIManagerImpl();
        ///iImg.setROIManager(iRoim);
        iImg.open(aName); 

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 
        iImg.setPreferredSize(new java.awt.Dimension(600, 600));  
        iImg.setMinimumSize(new java.awt.Dimension(575, 600));
        iImg.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        //iLut  = new LUTControl(iImg);        
        iLut  = new LUTControl();        
        iLut.setAlignmentX(Component.RIGHT_ALIGNMENT);
        iImg.setLUTControl(iLut);
        
        
        add(iImg, JLayeredPane.DEFAULT_LAYER);
        add(iLut, JLayeredPane.DEFAULT_LAYER);

        iLut.registerListeners(this);
        iImg.addWindowChangeListener(iLut);     
    }
    
    void setLUT(String aName) {
        iImg.setLUT(aName);        
    }
    
    void resetView() {
        iImg.resetView();
    }
    
    void loadFrame(int aN) {
        iImg.loadFrame(aN);
    }
    
    void fitWidth() {        
        iImg.fitWidth();
    }
       
    Iterator<Overlay> getOverlaysList() {        
        return iImg.getManager().getOverlaysList();
    }
    
    
    private static final Logger logger = LogManager.getLogger(JMedPane.class);   
}
