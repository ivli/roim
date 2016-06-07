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
import com.ivli.roim.view.GridImageView;
import com.ivli.roim.algorithm.MIPProjector;
import com.ivli.roim.core.MultiframeImage;

import com.ivli.roim.io.IImageProvider;
import com.ivli.roim.core.IMultiframeImage;
import java.awt.*;
import java.io.IOException;
import javax.swing.JDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.UIManager;

import com.ivli.roim.controls.*;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.events.*;
import com.ivli.roim.io.ImageProviderFactory;
import com.ivli.roim.view.ImageViewFactory;
import java.io.File;
import java.util.Locale;
import javax.swing.ImageIcon;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class NewJFrame extends javax.swing.JFrame implements FrameChangeListener, WindowChangeListener, ZoomChangeListener, ROIChangeListener, ProgressListener {     
    private ImagePanel  iImage;
    private ImagePanel  iGrid;    
    private ImagePanel  iOff;
    private ChartPanel   iChart;
    private ChartPanel   iChart2;
    private IImageProvider iProvider;
    
    /**/
    private static final void addjustLAF() {
        if (Locale.getDefault().equals(new Locale("ru", "RU"))) { //NOI18N
            /*  add locale to JFileChooser */
            UIManager.put("FileChooser.lookInLabelText", "Папка");
            UIManager.put("FileChooser.cancelButtonText", "Отмена");
            UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
            UIManager.put("FileChooser.openButtonText", "Открыть");
            UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Тип");
            UIManager.put("FileChooser.fileNameLabelText", "Файл");
            UIManager.put("FileChooser.detailsViewButtonToolTipText", "Подробно");
            UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Подробно");
            UIManager.put("FileChooser.upFolderToolTipText",    "На один уровень вверх"); 
            UIManager.put("FileChooser.upFolderAccessibleName", "На один уровень вверх"); 
            UIManager.put("FileChooser.homeFolderToolTipText",   "Домой"); 
            UIManager.put("FileChooser.homeFolderAccessibleName", "Домой"); 
            UIManager.put("FileChooser.fileNameHeaderText", "Имя"); 
            UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
            UIManager.put("FileChooser.fileTypeHeaderText", "Тип"); 
            UIManager.put("FileChooser.fileDateHeaderText", "Дата"); 
            UIManager.put("FileChooser.fileAttrHeaderText", "Аттрибуты");
            UIManager.put("FileChooser.listViewButtonToolTipText", "Список"); 
            UIManager.put("FileChooser.listViewButtonAccessibleName", "Список");      
            UIManager.put("FileChooser.openDialogTitleText", "Выберите файл");
            UIManager.put("FileChooser.saveDialogTitleText", "Выберите файл");
            UIManager.put("FileChooser.saveAsButtonText", "Сохранить как");
            UIManager.put("FileChooser.saveButtonText", "Сохранить");
            UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");
            
            UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");
            
            UIManager.put("FileChooser.desktopAccessibleName", "Рабочий стол");
            UIManager.put("FileChooser.desktopToolTipText",    "Рабочий стол");
            
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);       
            
            UIManager.put("ColorChooser.okText", "Выбрать");
            UIManager.put("ColorChooser.cancelText", "Отменить");
            UIManager.put("ColorChooser.resetText", "Сброс");
            
        }
    }
    
    public NewJFrame() {         
        logger.info("-->Entering application."); // NOI18N
        //iPanel = new ImagePanel();
        ImageIcon img = new ImageIcon(ClassLoader.getSystemResource("images/khibiny.png"));
        setIconImage(img.getImage());
        initComponents();    
    }
    	
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
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

        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setPreferredSize(new java.awt.Dimension(597, 600));
        jPanel1.setVerifyInputWhenFocusTarget(false);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jTabbedPane1.addTab(bundle.getString("NewJFrame.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(bundle.getString("NewJFrame.jPanel3.TabConstraints.tabTitle"), jPanel3); // NOI18N

        jPanel4.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(bundle.getString("NewJFrame.jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText(bundle.getString("NewJFrame.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("NewJFrame.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("NewJFrame.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("NewJFrame.jLabel4.text")); // NOI18N

        jLabel6.setText(bundle.getString("NewJFrame.jLabel6.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        jMenu1.setText(bundle.getString("NewJFrame.jMenu1.text")); // NOI18N

        jMenuItem2.setText(bundle.getString("NewJFrame.jMenuItem2.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);
        jMenu1.add(jSeparator2);

        jMenuItem1.setText(bundle.getString("NewJFrame.jMenuItem1.text")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("NewJFrame.jMenu2.text")); // NOI18N
        jMenu2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jMenu2ComponentShown(evt);
            }
        });

        jMenuItem3.setText(bundle.getString("NewJFrame.jMenuItem3.text")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuItem7.setText(bundle.getString("NewJFrame.jMenuItem7.text")); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem8.setText(bundle.getString("NewJFrame.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuBar1.add(jMenu2);

        jMenu4.setText(bundle.getString("NewJFrame.jMenu4.text")); // NOI18N

        jMenu3.setText(bundle.getString("NewJFrame.jMenu3.text")); // NOI18N

        jMenuItem10.setText(bundle.getString("NewJFrame.jMenuItem10.text")); // NOI18N
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem10);

        jMenuItem11.setText(bundle.getString("NewJFrame.jMenuItem11.text")); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem11);

        jMenuItem12.setText(bundle.getString("NewJFrame.jMenuItem12.text")); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem12);

        jMenu4.add(jMenu3);

        jMenu5.setText(bundle.getString("NewJFrame.jMenu5.text")); // NOI18N

        jMenuItem13.setText(bundle.getString("NewJFrame.jMenuItem13.text")); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem13);

        jMenuItem14.setText(bundle.getString("NewJFrame.jMenuItem14.text")); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem14);

        jMenuItem15.setText(bundle.getString("NewJFrame.jMenuItem15.text")); // NOI18N
        jMenuItem15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem15);

        jMenuItem5.setText(bundle.getString("NewJFrame.jMenuItem5.text")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem5);

        jMenu4.add(jMenu5);

        jMenuItem4.setText(bundle.getString("NewJFrame.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu4.add(jMenuItem4);

        jMenuBar1.add(jMenu4);

        jMenu6.setText(bundle.getString("NewJFrame.jMenu6.text")); // NOI18N
        jMenu6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jMenu6.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jMenuItem6.setText(bundle.getString("NewJFrame.jMenuItem6.text")); // NOI18N
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
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        logger.info("<--BYE, BYE...");// NOI18N
        dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed
     
    private void openImage(String aF) {   
        String dicomFileName;
        
        /* USE_SYSTEM_FILE_DIALOG 
        if (null != aF) {
            dicomFileName = aF;
        } else {
            FileDialog fd = new FileDialog(this, java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NEWJFRAME.CHOOSE_DICOM_FILE"), FileDialog.LOAD);
            
            fd.setFile(Settings.get(Settings.KEY_FILE_SUFFIX_DICOM, "*.dcm")); // NOI18N
            
            // following doesn't work on windows see JDK-4031440 f**k 
            fd.setFilenameFilter(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".dcm") || name.endsWith(".dicom");
                }
            });
            
            fd.setDirectory(Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home"))); // NOI18N
            fd.setVisible(true);

            if (null != fd.getFile()) 
                dicomFileName = fd.getDirectory() + fd.getFile(); 
            else
                return;
        }
        
        
        /* ELSE*/
        JFileChooser jfc = new JFileChooser();   
        jfc.setCurrentDirectory(new File(Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home")))); // NOI18N
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new FileFilter(){ 
            public  boolean accept(File f) {                                 
                if (f.isDirectory()) 
                    return true;
                else {
                    String ext = "";
                    String s = f.getName();
                    int i = s.lastIndexOf('.');

                    if (i > 0 &&  i < s.length() - 1) {
                        ext = s.substring(i+1).toLowerCase();
                    }    

                    if (ext.equalsIgnoreCase(Settings.DEFAULT_FILE_SUFFIX_DICOM))
                        return true;
                }
                
                return false;
            }
            
            public String getDescription() {return java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("JFC.FILE_DESCRIPTION_DICOM");}                           
        });
        
        
        int result = jfc.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            System.out.println("Selected file: " + (dicomFileName = selectedFile.getAbsolutePath()));
            Settings.set(Settings.KEY_DEFAULT_FOLDER_DICOM,  selectedFile.getPath());
        } else {
            return;
        }
        
        /* ENDIF */
        
        try {                           
            iProvider = ImageProviderFactory.create(dicomFileName);
            logger.info("opened file: " + dicomFileName);            
        } catch (IOException ex) {            
            logger.info("Unable to open file: " + dicomFileName); //NOI18N            
            javax.swing.JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MSG_UNABLETOOPENFILE") + dicomFileName);       
            return;
        } 
        
        initPanels();
    }
        
    private void initPanels() {         
        jPanel1.removeAll();
        jPanel3.removeAll();
        jPanel4.removeAll();
        
        jPanel1.repaint();
        jPanel3.repaint();
        jPanel4.repaint();
        
        IMultiframeImage mi2 = MultiframeImage.create(iProvider);       
        IMultiframeImage mi;                
        
        switch (mi2.getImageType()) {
            case VOLUME:
            case TOMO:{   
                ProgressDialog d = ProgressDialog.create(this, "---");
                MIPProjector prj = new MIPProjector(mi2, 128);
                
                class tmp  implements Runnable {                    
                    
                    public void run() {                        
                        prj.project();
                    }
                } 
                
                tmp t = new tmp();
                prj.addProgressListener(d);
                
                (new Thread(t)).start();
                d.show(true);
                mi = prj.getDst();
                
            } break; 
                
            default:       
            mi = mi2;        
        break;
        }
        
        /**/
         //IMAGE
        iImage = new ImagePanel(); 
        ImageView iv = ImageViewFactory.create(mi);
        ///iv.setImage(mi);
        iImage.setView(iv);
        iImage.setPreferredSize(jPanel1.getSize());
        iImage.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(iImage, BorderLayout.CENTER);
        jPanel1.validate(); 
        
        //CHART
        
        if (mi.getImageType() == ImageType.DYNAMIC) {
            iChart = new ChartPanel();        
            iChart.initChart();
            iChart.setPreferredSize(jPanel3.getPreferredSize());
            jPanel3.add(iChart);  
            iImage.addROIChangeListener(iChart);
        }
                
        if (mi.getImageType() != ImageType.STATIC) {     
            ImageView iv1 = GridImageView.create(mi2, 4, 4) ;
            //iv1.setImage(mi2);
            iGrid = new ImagePanel(); 
            iGrid.setView(iv1);
            iGrid.setPreferredSize(jPanel4.getSize());
            ///iGrid.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));        
            jPanel4.setLayout(new BorderLayout());     
            jPanel4.add(iGrid, BorderLayout.CENTER);        
        }        
        //
               
        iImage.addFrameChangeListener(this);        
        iImage.addWindowChangeListener(this);
        iImage.addZoomChangeListener(this);
        iImage.addROIChangeListener(this);                 
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        openImage(null);
    }//GEN-LAST:event_jMenuItem2ActionPerformed
          
    private void jMenu2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jMenu2ComponentShown
        // TODO add your handling code here:
        if (null == iImage) {
        
        } else {
        
        }
    }//GEN-LAST:event_jMenu2ComponentShown

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if(null != iImage) {
            iImage.reset();        
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        
        ImagePanel pane = null;
               
        switch(jTabbedPane1.getSelectedIndex()) {
            case 0:
                pane = iImage; break;
            case 2:
                pane = iOff; break;
            default: break;    
        }
                
        if (null != pane) {
            ROIListPanel panel = new ROIListPanel(pane);
            JDialog dialog = new JDialog(this, "ROI manager", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setContentPane(panel);
            dialog.validate();
            dialog.pack();
            dialog.setResizable(true);
            dialog.setVisible(true);
            repaint();
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        switch(jTabbedPane1.getSelectedIndex()) {
            case 0:
                if (null != iImage) 
                    iImage.showLUTDialog(); 
                break;
            case 2:
                if (null != iOff) 
                    iOff.showLUTDialog(); 
                break;
            default: break;    
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        iImage.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        iImage.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        iImage.setInterpolationMethod(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        iImage.setFit(ImageView.ZoomFit.HEIGHT);        
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jMenuItem15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        iImage.setFit(ImageView.ZoomFit.VISIBLE);
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        iImage.setFit(ImageView.ZoomFit.WIDTH);
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        ImagePanel pane = null;
               
        switch(jTabbedPane1.getSelectedIndex()) {
            case 0:
                pane = iImage; break;
            case 2:
                pane = iOff; break;
            default: break;    
        }
                
        if (null != pane) {
            CalcPanel panel = new CalcPanel(pane);
            JDialog dialog = new JDialog(this, Dialog.ModalityType.MODELESS);
            dialog.setContentPane(panel);
            dialog.validate();
            dialog.pack();
            dialog.setResizable(true);
            dialog.setVisible(true);
            repaint();
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
        // TODO add your handling code here:
        logger.info(evt);
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        iImage.setFit(ImageView.ZoomFit.PIXELS);
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        AboutDialog dlg = new AboutDialog(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

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
        
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        addjustLAF();
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
        
    public void ProgressChanged(ProgressEvent aE) {
        logger.debug(String.format("%d %% complete", (int)(aE.getProgress() * 100)));
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
        //iw.getImage().getNumFrames(),                                                           
                                                          // iw.getRange(), getImage().getFrame().getRange(),
                                                          //iModel.getTimeSliceVector().getSlice());                
                                                         //   getFrameNumber());
                                                            
        jLabel1.setText(String.format("%d:%d", aE.getFrame() + 1, iw.getImage().getNumFrames())); // NOI18N
        
        String label2text;
        
        switch (iw.getImage().getImageType()) {
            case DYNAMIC: {
                TimeSlice ts = iw.getImage().getTimeSliceVector().getSlice(aE.getFrame());
                label2text = String.format("%s - %s", ts.getFrom().format(), ts.getTo().format());
            } break;
            case WHOLEBODY: {
                label2text = aE.getFrame() == 0 ? "ANT" : "POST";
            } break;
            case TOMO: //fall-through
            case VOLUME:{
                label2text = String.format("%d", aE.getFrame());
            } break;
                
            case STATIC: //fall-through
            default: label2text = "---"; break;

        }
        
        jLabel2.setText(label2text);
                                                 
                
        jLabel3.setText(String.format("%3.0f/%3.0f", iw.getRange().getMin(), iw.getRange().getMax())); // NOI18N
        
    }
  
    public void ROIChanged(ROIChangeEvent anEvt) {
    
    }
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    private final static Logger logger = LogManager.getLogger(NewJFrame.class);
}
