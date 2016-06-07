/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim;


import com.ivli.roim.view.Settings;
import com.ivli.roim.view.ImageView;
import com.ivli.roim.view.Overlay;
import com.ivli.roim.io.LutReader;
import java.util.Iterator;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Dialog;
import javax.swing.JDialog;
import com.ivli.roim.controls.VOILUTPanel;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.events.*;



public class ImagePanel extends JPanel {      
    private ImageView iView;    
    private LUTControl iLut;
                  
    public void setView(ImageView aView) {       
        iView = aView;
        iLut  = new LUTControl();                                     
        iLut.attach(aView);
       
        //loadFrame(3);
        
        iView.setLUT(Settings.get(Settings.KEY_DEFAULT_PRESENTATION_LUT, LutReader.getInstalledLUT()[0]));
        setLayout(new BorderLayout());                         
        add(iView);           
        add(iLut, BorderLayout.LINE_END);  
    } 
    
    public void ImagePanel() {
        
    }
    
    public ImageView getView() {
        return iView;
    }    
    
    public void addFrameChangeListener(FrameChangeListener aL) {
        iView.addFrameChangeListener(aL);
    }
    
    public void addWindowChangeListener(WindowChangeListener aL) {
         iView.addWindowChangeListener(aL);
    }
    
    public void addZoomChangeListener(ZoomChangeListener aL) {
        iView.addZoomChangeListener(aL);
    }
    
    public void addROIChangeListener(ROIChangeListener aL)  {
        iView.getROIMgr().addROIChangeListener(aL);
    }
    
    public void setLUT(String aName) {
        iView.setLUT(aName);        
    }
    
    void reset() {    
        iView.reset();                
        repaint();
    }
    
    void loadFrame(int aN) {
        iView.loadFrame(aN);
    }
        
    public void setInterpolationMethod(Object aMethod) {        
        iView.setInterpolationMethod(aMethod);     
        repaint();    
    }
    
    public void setFit(Object aFit) {   
        iView.setFit((ImageView.ZoomFit)aFit);     
        repaint();   
    }
        
    public void showLUTDialog() {
        VOILUTPanel panel = new VOILUTPanel(iLut, iView);
        JDialog dialog = new JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    public Iterator<Overlay> getOverlaysList() {        
        return iView.getROIMgr().getOverlaysList();
    }   
}
