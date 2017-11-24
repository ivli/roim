/*
 * Copyright (C) 2016 likhachev
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


import java.io.File;
import java.util.Locale;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.RenderingHints;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.JFrame;
import com.ivli.roim.view.ImageView;
import com.ivli.roim.controls.AboutDialog;
import com.ivli.roim.controls.CalcPanel;
import com.ivli.roim.controls.ChartView;
import com.ivli.roim.controls.FileOpenDialog;
import com.ivli.roim.controls.FrameControl;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.controls.ROIListPanel;
import com.ivli.roim.view.IImageView;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFactory;
import com.ivli.roim.events.*;
import com.ivli.roim.view.ROIManager;
import com.ivli.roim.view.Settings;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DYNAMIC extends JFrame implements FrameChangeListener, WindowChangeListener, ZoomChangeListener, ProgressListener {     
   
    private ChartView  iChart; 
    private ImageView  iView1;
    private ImageView  iView2;
    private ROIManager iMgr;
    /**/
    private static final void adjustLAF() {
        if (Locale.getDefault().equals(new Locale("ru", "RU"))) { //NOI18N
            /*  add locale to JColorChooser */            
            UIManager.put("ColorChooser.okText",     "Выбрать");
            UIManager.put("ColorChooser.cancelText", "Отменить");
            UIManager.put("ColorChooser.resetText",  "Сброс");            
        }
    }
    
    { //ensure localisation            
        adjustLAF();        
    }
    
    public DYNAMIC() {         
        LOG.info("-->Entering application"); // NOI18N       
        setIconImage(new ImageIcon(ClassLoader.getSystemResource("images/khibiny.png")).getImage());
      
        initComponents(); 
        
        //jMenuItem7.setEnabled(false);
        //jMenuItem8.setEnabled(false);
    }
    	
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAutoRequestFocus(false);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("DYNAMIC.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("DYNAMIC.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("DYNAMIC.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("DYNAMIC.jLabel4.text")); // NOI18N

        jLabel6.setText(bundle.getString("DYNAMIC.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(103, 103, 103))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)))
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(400);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel5.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(jPanel5);

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(597, 600));
        jPanel1.setVerifyInputWhenFocusTarget(false);
        jPanel1.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(bundle.getString("DYNAMIC.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(bundle.getString("DYNAMIC.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jSplitPane1.setTopComponent(jTabbedPane1);

        jMenu1.setText(bundle.getString("DYNAMIC.jMenu1.text")); // NOI18N

        jMenuItem2.setText(bundle.getString("DYNAMIC.jMenuItem2.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        jMenuItem1.setText(bundle.getString("DYNAMIC.jMenuItem1.text")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("DYNAMIC.jMenu2.text")); // NOI18N
        jMenu2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jMenu2ComponentShown(evt);
            }
        });

        jMenuItem7.setText(bundle.getString("DYNAMIC.jMenuItem7.text")); // NOI18N
        jMenuItem7.setEnabled(false);
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText(bundle.getString("DYNAMIC.jMenuItem8.text")); // NOI18N
        jMenuItem8.setEnabled(false);
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuBar1.add(jMenu2);

        jMenu4.setText(bundle.getString("DYNAMIC.jMenu4.text")); // NOI18N

        jMenu3.setText(bundle.getString("DYNAMIC.jMenu3.text")); // NOI18N
        jMenu3.setEnabled(false);

        jMenuItem10.setText(bundle.getString("DYNAMIC.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuItem11.setText(bundle.getString("DYNAMIC.jMenuItem11.text")); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuItem12.setText(bundle.getString("DYNAMIC.jMenuItem12.text")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem12);

        jMenu4.add(jMenu3);

        jMenu5.setText(bundle.getString("DYNAMIC.jMenu5.text")); // NOI18N
        jMenu5.setEnabled(false);

        jMenuItem13.setText(bundle.getString("DYNAMIC.jMenuItem13.text")); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setText(bundle.getString("DYNAMIC.jMenuItem14.text")); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText(bundle.getString("DYNAMIC.jMenuItem15.text")); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuItem5.setText(bundle.getString("DYNAMIC.jMenuItem5.text")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem5);

        jMenu4.add(jMenu5);

        jMenuItem4.setText(bundle.getString("DYNAMIC.jMenuItem4.text")); // NOI18N
        jMenuItem4.setEnabled(false);
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuBar1.add(jMenu4);

        jMenu6.setText(bundle.getString("DYNAMIC.jMenu6.text")); // NOI18N
        jMenu6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jMenu6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jMenuItem6.setText(bundle.getString("DYNAMIC.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItem6);

        jMenuBar1.add(jMenu6);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 564, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        LOG.info("<--BYE, BYE...");// NOI18N
        dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
     
    private void openImage(String[] aF) {   
        String dicom = null;
        String roif = null;
        
        if (null == aF || aF.length == 0) {        
            FileOpenDialog dlg = new FileOpenDialog(this, 
                                                    java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CHOICE_FILE_TO_OPEN"), 
                                                    "dcm", //NOI18N 
                                                    java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("DICOM_FILE_TITLE"),
                                                    Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home")),
                                                    true
                                                    );
            if(!dlg.doModal())
                return;
            else
                dicom = dlg.getFileName();
        } else {
            File f = new File(aF[0]);        
            if (f.exists() && !f.isDirectory())
                dicom = aF[0];
            if (aF.length > 1 && new File(aF[1]).exists()) 
                roif = aF[1];
        }

        initPanels(ImageFactory.create(dicom, null), roif);
        setTitle(dicom);             
    }
        
    private void initPanels(IMultiframeImage anImage, String aROILIST) {       
        jPanel1.removeAll();
        jPanel3.removeAll();      
        jPanel5.removeAll();       
       
        iMgr = ROIManager.create(anImage);       
        
        iView1 = ImageView.create(anImage, null, iMgr);  
        
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(iView1, BorderLayout.CENTER);
        jPanel1.add(LUTControl.create(iView1), BorderLayout.LINE_END);
        jPanel1.add(FrameControl.create(iView1), BorderLayout.PAGE_END);
        jPanel1.validate(); 
        
        if (anImage.getNumFrames() > 1) {
            iView2 = ImageView.create(anImage.processor().collapse(), null, iMgr); 
            jPanel3.add(iView2, BorderLayout.CENTER);
            jPanel3.add(LUTControl.create(iView2), BorderLayout.LINE_END);
            jPanel3.validate(); 
            jPanel3.setVisible(true);
            jTabbedPane1.setEnabledAt(1, true);            
        } else {
            jPanel3.setVisible(false);           
            jTabbedPane1.setEnabledAt(1, false);
        }
        //CHART        
        if (anImage.getImageType().getTypeName().equals(ImageType.NM_DYNAMIC)) {
            iChart = ChartView.create();                    
            iChart.setPreferredSize(jPanel5.getPreferredSize());
            jPanel5.add(iChart);      
            iMgr.addChangeListener(iChart);          
            jPanel5.setVisible(true);
            jPanel5.setSize(jPanel5.getPreferredSize());
            jSplitPane1.setDividerLocation(400);
        } 
        else 
            jPanel5.setVisible(false);
        validate();
        /*        
        if (mi.getImageType() != ImageType.STATIC) {     
            iGrid = GridImageView.create(mi, 4, 4, null) ;          
            iGrid.setPreferredSize(jPanel6.getSize());                  
            jPanel6.setLayout(new BorderLayout());     
            jPanel6.add(iGrid, BorderLayout.CENTER);   
            jPanel6.add(LUTControl.create(iGrid), BorderLayout.LINE_END);   
        }        
        */
        
        if (aROILIST != null) {
            try {
                iMgr.internalize(new ObjectInputStream(new FileInputStream(aROILIST)));
            } catch (IOException | ClassNotFoundException ex) {
                LOG.throwing(ex);
                JOptionPane.showMessageDialog(this, "Unable to open file", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        iView1.addFrameChangeListener(this);        
        iView1.addWindowChangeListener(this);
        iView1.addZoomChangeListener(this);
        jMenuItem7.setEnabled(true);
        jMenuItem8.setEnabled(true);       
        jMenuItem4.setEnabled(true);
        jMenu3.setEnabled(true);
        jMenu5.setEnabled(true);
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        openImage(null);//"D:/images/cholescy.dcm");
    }//GEN-LAST:event_jMenuItem2ActionPerformed
          
    private void jMenu2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jMenu2ComponentShown
     
    }//GEN-LAST:event_jMenu2ComponentShown

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if(null != jTabbedPane1.getSelectedComponent()) {
         ///   ((IImageView)jTabbedPane1.getSelectedComponent()).reset();                
         ////TODO:
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        ROIListPanel panel = new ROIListPanel(iMgr);
        JDialog dialog = new JDialog(this, 
                                    "ROI manager", 
                                    Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);
        repaint();   
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
       // IImageView v = ((IImageView)jTabbedPane1.getSelectedComponent());        
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setFit(ImageView.ZoomFit.HEIGHT);        
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setFit(ImageView.ZoomFit.VISIBLE);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setFit(IImageView.ZoomFit.WIDTH);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        CalcPanel panel = new CalcPanel(iMgr);
        JDialog dialog = new JDialog(this, Dialog.ModalityType.MODELESS);
        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);
        repaint();      
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        ((ImageView)((JPanel)jTabbedPane1.getSelectedComponent()).getComponent(0)).setFit(IImageView.ZoomFit.PIXELS);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        AboutDialog dlg = new AboutDialog(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
        // TODO add your handling code here:
        LOG.info(evt);
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        try {
         /*  IF USE_NIMBUS_LAF */ 
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // NOI18N
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        
        /*  ELSE 
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
        /*  END_IF USE_NIMBUS_LAF */    
        
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOG.error(ex);
        }
       
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DYNAMIC nmc = new DYNAMIC();
                if (args.length > 0)
                    nmc.openImage(args);
                
                nmc.setVisible(true);
            }
        });
    }
        
    public void ProgressChanged(ProgressEvent aE) {
        LOG.debug("{} %% complete", aE.getProgress() * 100 );
        jLabel2.setText(String.format("%d %% complete", (int)(aE.getProgress() * 100)));
        jLabel2.repaint();
    }
    
    public void zoomChanged(ZoomChangeEvent aE) {
        jLabel6.setText(String.format("%3.0f", aE.getZoom() * 100.0)); // NOI18N
        
    }
    
    public void windowChanged(WindowChangeEvent aE) {
        jLabel4.setText(String.format("%3.0f/%3.0f", aE.getWindow().getLevel(), aE.getWindow().getWidth())); // NOI18N        
    }
    
    public void frameChanged(FrameChangeEvent aE) { 
        ImageView iw = (ImageView)aE.getSource();        
        jLabel1.setText(String.format("%d:%d", aE.getFrame() + 1, iw.getImage().getNumFrames())); // NOI18N
        
        String label2text;
        
        switch (iw.getImage().getImageType().getTypeName()) {
            case ImageType.NM_DYNAMIC: {
                TimeSlice ts = iw.getImage().getTimeSliceVector().getSlice(aE.getFrame());
                label2text = String.format("%s - %s", ts.getFrom().format(), ts.getTo().format());
            } break;
            case ImageType.NM_WHOLEBODY: {
                label2text = aE.getFrame() == 0 ? "ANT" : "POST";
            } break;
            case ImageType.NM_TOMO: //fall-through
            case ImageType.NM_VOLUME:{
                label2text = String.format("%d", aE.getFrame());
            } break;
                
            case ImageType.NM_STATIC: //fall-through
            default: label2text = "---"; break;

        }
        
        jLabel2.setText(label2text);                                                                 
        jLabel3.setText(String.format("%3.0f/%3.0f", iw.getMin(), iw.getMax())); // NOI18N
        
    }
  
    //public void OverlayChanged(OverlayChangeEvent anEvt) {
    
    //}

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    private final static Logger LOG = LogManager.getLogger();
}
