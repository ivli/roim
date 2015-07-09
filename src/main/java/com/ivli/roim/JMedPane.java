/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.ListIterator;
import javax.swing.JPanel;
import java.io.IOException;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
public class JMedPane extends JLayeredPane{
    MEDImageComponent iImg;
    LUTControl        iLut;
    ROIManager        iRoim;
    
    public JMedPane() {        
        
    }
    
    void open(String aName) throws IOException {    
        iImg = new MEDImageComponent();
        iRoim = new ROIManagerImpl();
        iImg.setROIManager(iRoim);
        iImg.open(aName); 

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); 
        iImg.setPreferredSize(new java.awt.Dimension(600, 600));  
        iImg.setMinimumSize(new java.awt.Dimension(575, 600));

        iImg.setAlignmentX(Component.LEFT_ALIGNMENT);
        iLut  = new LUTControl(this, iImg);        
        iLut.setAlignmentX(Component.RIGHT_ALIGNMENT);

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
       
    ListIterator<Overlay> getOverlaysList() {        
        return iImg.getROIManager().getOverlaysList();
    }
    
    
    private static final Logger logger = LogManager.getLogger(JMedPane.class);   
}
