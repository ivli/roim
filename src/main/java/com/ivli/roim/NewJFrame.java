package com.ivli.roim;

import java.awt.*;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JDialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.controls.*;
import com.ivli.roim.Events.*;
import org.apache.logging.log4j.Level;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class NewJFrame extends javax.swing.JFrame implements FrameChangeListener, WindowChangeListener, ZoomChangeListener, ROIChangeListener {

    private final XYPlot    iPlot;
    private final JFreeChart iJfc;
    
    public NewJFrame() {         
        logger.info("-->Entering application."); // NOI18N
        initComponents();
        
        iPlot = new XYPlot();
        //plot.setDataset(xyc);
        iPlot.setRenderer(new StandardXYItemRenderer());
        iPlot.setDomainAxis(new NumberAxis("ROI_CHART.TIME_SERIES_VALUES"));
        iPlot.setRangeAxis(0, new NumberAxis("ROI_CHART.ROI_INTDEN_VALUES"));
       // if(iShowHistogram)
       //     plot.setRangeAxis(1, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.HISTOGRAM")));
        iPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        iPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        
        iJfc = new JFreeChart(iPlot); 
      
        iChart = new ChartPanel(iJfc);
        //iChart.setMouseWheelEnabled(true);
         
        XYSeriesCollection ds = new XYSeriesCollection();
        iPlot.setDataset(ds);
        iChart.setSize(jPanel3.getPreferredSize());
        iChart.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        jPanel3.add(iChart);//, java.awt.BorderLayout.CENTER);
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

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

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText(bundle.getString("NewJFrame.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("NewJFrame.jLabel2.text")); // NOI18N

        jLabel3.setText(bundle.getString("NewJFrame.jLabel3.text")); // NOI18N

        jLabel4.setText(bundle.getString("NewJFrame.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel4)
                .addGap(60, 60, 60)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
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

        jMenuItem5.setText(bundle.getString("NewJFrame.jMenuItem5.text")); // NOI18N
        jMenuItem5.setActionCommand(bundle.getString("NewJFrame.jMenuItem5.actionCommand")); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem7.setText(bundle.getString("NewJFrame.jMenuItem7.text")); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem6.setText(bundle.getString("NewJFrame.jMenuItem6.text")); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem9.setText(bundle.getString("NewJFrame.jMenuItem9.text")); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);

        jMenuItem8.setText(bundle.getString("NewJFrame.jMenuItem8.text")); // NOI18N
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenuItem8.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jMenuItem8PropertyChange(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem4.setText(bundle.getString("NewJFrame.jMenuItem4.text")); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
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
     
    private void openImage(String aF) throws IOException {   
        final String dicomFileName;
        
        if (null != aF) {
            dicomFileName = aF;
        } else {
            FileDialog fd = new FileDialog(this,java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NEWJFRAME.CHOOSE_DICOM_FILE"), FileDialog.LOAD);
            fd.setDirectory("D:\\images\\"); // NOI18N
            fd.setFile("*.dcm"); // NOI18N
            fd.setVisible(true);

            if (null != fd.getFile()) 
                dicomFileName = fd.getDirectory() + fd.getFile(); 
            else
                return;
        }
         
        iChart.removeAll();
        jPanel1.removeAll();
        iPanel = null;
        
        iPanel = new JMedPane();
        
        try{
            iPanel.open(dicomFileName);
        }catch (IOException ex) {            
            logger.info("Unable to open file " + dicomFileName); //NOI18N            
            javax.swing.JOptionPane.showMessageDialog(null, "Unable to open file " + dicomFileName);
            return;
        }
        
        //iPanel.setMinimumSize(new Dimension(jPanel1.getWidth()-50, jPanel1.getHeight()));
        iPanel.setPreferredSize(jPanel1.getSize());
        iPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        //iPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
             
        
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(iPanel, BorderLayout.CENTER);
        
        jPanel1.validate(); 
        
        iPanel.iView.addFrameChangeListener(this);
        iPanel.iView.addZoomChangeListener(this);
        iPanel.iView.addWindowChangeListener(this);
        iPanel.iView.addROIChangeListener(this);  
    }
    
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        try {
            openImage(null);//"d:\\images\\H2_res.dcm"); // NOI18N           
        } catch (Exception e) {                   
            logger.error(e);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed
    
        
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if(null != iPanel) 
            iPanel.resetView();
        jPanel1.repaint();
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    //static boolean b = true; 
    
    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        FileDialog fd = new FileDialog(this, java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NEWJFRAME.CHOOSE_LUT_FILE"), FileDialog.LOAD);
        fd.setDirectory(Settings.DEFAULT_FOLDER_LUT); 
        fd.setFile(Settings.FILE_SUFFIX_LUT); 
        fd.setVisible(true);
        String cm ;        
        if (null != fd.getFile() && null != (cm = fd.getDirectory() + fd.getFile())) {
            iPanel.setLUT(cm);  
            jPanel1.repaint();
            //iLut.setLUT(cm);
            //iLut.repaint();
            logger.info("LUT changed"); // NOI18N               
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed
                
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        VOILUTPanel panel = new VOILUTPanel(iPanel.iLut, iPanel.iView.getImage().image());       
        JDialog dialog = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);
        
        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);   
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        ROIListPanel panel = new ROIListPanel(null != iPanel?iPanel.getOverlaysList():null);
        JDialog dialog = new JDialog(this, Dialog.ModalityType.APPLICATION_MODAL);        
        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);     
        repaint();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        iPanel.fitWidth();
        iPanel.repaint();
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem8PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jMenuItem8PropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8PropertyChange

    private void jMenu2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jMenu2ComponentShown
        // TODO add your handling code here:
        if (null == iPanel)
            jMenuItem8.setEnabled(false);
        else
            jMenuItem8.setEnabled(true);
    }//GEN-LAST:event_jMenu2ComponentShown

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
        // TODO add your handling code here:
        logger.info(evt);
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        FileDialog fd = new FileDialog(this, "NEWJFRAME.CHOOSE_ROILIST_FILE", FileDialog.SAVE);
        fd.setDirectory(Settings.DEFAULT_FOLDER_ROILIST); 
        fd.setFile(Settings.FILE_SUFFIX_ROILIST); 
        fd.setVisible(true);
        String ff ;        
        if (null != fd.getFile() && null != (ff = fd.getDirectory() + fd.getFile()))         
            iPanel.iView.getROIMgr().externalize(ff);
        
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
       
        FileDialog fd = new FileDialog(this, "NEWJFRAME.CHOOSE_ROILIST_FILE", FileDialog.LOAD);
        fd.setDirectory(Settings.DEFAULT_FOLDER_ROILIST); 
        fd.setFile(Settings.FILE_SUFFIX_ROILIST); 
        fd.setVisible(true);
        String ff ;        
        if (null != fd.getFile() && null != (ff = fd.getDirectory() + fd.getFile())) {
            iPanel.iView.getROIMgr().internalize(ff);
            iPanel.repaint();
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // NOI18N
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }
        
    public void zoomChanged(ZoomChangeEvent aE) {
        jLabel1.setText(String.format("%3.0f", aE.getZoom() * 100.0)); // NOI18N
    }
    
    public void windowChanged(WindowChangeEvent aE) {
        jLabel2.setText(String.format("%3.0f/%3.0f", aE.getWindow().getLevel(), aE.getWindow().getWidth())); // NOI18N
        
    }
    
    public void frameChanged(FrameChangeEvent aE) {
       jLabel4.setText(String.format("%d:%d", aE.getFrame() + 1, aE.getTotal())); // NOI18N
    }
    
    
  
    
    public void ROIChanged(ROIChangeEvent aE) {

        XYSeriesCollection col = ((XYSeriesCollection)iPlot.getDataset());
        
        switch (aE.getChange()) {
            case Cleared: {
                
                int ndx = col.indexOf(aE.getROI().getName());
                col.removeSeries(ndx); 
               
              } break;
    
            case Changed: 
                int ndx = col.indexOf(aE.getROI().getName());
               
                col.removeSeries(ndx); //no break - fall through creation case
               
            case Created: 
                XYSeries s = new XYSeries(aE.getROI().getName());
                Series c = aE.getROI().getCurve();
                
                int x = 0;
                
                
                java.util.Iterator<Long> tsv = iPanel.iProvider.getTimeSliceVector().iSlices.iterator();
                
                
                for (Measure m : c)
                    s.add(tsv.next() / 1000, m.iIden);

                ((XYSeriesCollection)iPlot.getDataset()).addSeries(s);   
                iPlot.getRenderer().setSeriesPaint(col.indexOf(aE.getROI().getName()), aE.getROI().getColor());
              
            break;
            
            default: throw new java.lang.IllegalArgumentException();    
        }
   
    }
    
    JMedPane iPanel;
    ChartPanel iChart;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    private final static Logger logger = LogManager.getLogger(NewJFrame.class);

}
