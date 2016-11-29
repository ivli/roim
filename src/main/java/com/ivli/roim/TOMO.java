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

import com.ivli.roim.algorithm.MIPProjector;
import com.ivli.roim.controls.FileOpenDialog;
import com.ivli.roim.controls.FrameControl;
import com.ivli.roim.controls.LUTControl;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFactory;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Modality;
import com.ivli.roim.events.ProgressEvent;
import com.ivli.roim.events.ProgressListener;

import com.ivli.roim.view.ImageView;
import com.ivli.roim.view.Settings;
import com.ivli.roim.view.ViewMode;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 *
 * @author likhachev
 */
public class TOMO extends javax.swing.JFrame implements PropertyChangeListener {

    /**
     * Creates new form TOMO
     */
    public TOMO() {
        initComponents();
        jMenuItem3.setEnabled(false);
        ///jProgressBar1.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setMaximumSize(new java.awt.Dimension(2147483647, 32));
        jPanel2.setMinimumSize(new java.awt.Dimension(32, 32));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jMenu1.setText(bundle.getString("DYNAMIC.jMenu1.text")); // NOI18N

        jMenuItem1.setText(bundle.getString("DYNAMIC.jMenuItem2.text")); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText(bundle.getString("DYNAMIC.jMenuItem1.text")); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("DYNAMIC.jMenu2.text")); // NOI18N

        jMenuItem3.setText(bundle.getString("DYNAMIC.jMenuItem7.text")); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {                       
            jProgressBar1.setValue((Integer) evt.getNewValue());             
        }         
    }
    
    class SwingImageLoader extends SwingWorker<IMultiframeImage, Void> implements ProgressListener {    
        final String iFileName;
        
        SwingImageLoader(final String aFileName) {
            iFileName = aFileName;
        }
        
        @Override
        protected IMultiframeImage doInBackground() throws Exception {
            IMultiframeImage img = ImageFactory.create(iFileName, this);
            //MIPProjector mip = new MIPProjector(img, this);            
            return img;//mip.project(img.getNumFrames());
        }

        @Override
        public void done() {            
            setCursor(null); //turn off the wait cursor
            setProgress(100);
            try {
                imageLoaded(get());
            } catch (InterruptedException|ExecutionException ex) {
                LOG.catching(ex);
            }
            jProgressBar1.setValue(0); 
           //jProgressBar1.setVisible(false); 
        }
        
        @Override
        public void ProgressChanged(ProgressEvent anEvt) {
            setProgress(anEvt.getProgress());             
        }
    }
    
    final boolean MAKE_MIP_PROJECTION = true;
    
    private void imageLoaded(IMultiframeImage aImg) {
        if (MAKE_MIP_PROJECTION && (aImg.getModality().isTomographic() ||  aImg.getImageType().isTomographic())) { 
            SwingMIPProjector l = new SwingMIPProjector(aImg, Math.min(64, aImg.getNumFrames()), true);                 
            l.addPropertyChangeListener(this);             
            l.execute();
        } else {
            initPanels(aImg);
        }
    }
    
    class SwingMIPProjector extends SwingWorker<IMultiframeImage, Void> implements ProgressListener {    
        final IMultiframeImage iSource;
        final int iNoOfProjections;
        final boolean iCoarse;
        
        SwingMIPProjector(final IMultiframeImage aSource, int aNoOfProjections, boolean aCoarse) {
            iSource = aSource;
            iNoOfProjections = aNoOfProjections;
            iCoarse = aCoarse;
        }
                
        @Override
        protected IMultiframeImage doInBackground() throws Exception { 
            final long start = System.currentTimeMillis();
            IMultiframeImage ret =  new MIPProjector(iSource, this).project(iNoOfProjections);
            
            LOG.debug("MIP took" + (System.currentTimeMillis() - start));
            return ret;
        }

        @Override
        public void done() {            
            setCursor(null); //turn off the wait cursor
            setProgress(100);
            try {
                initPanels(get());
            } catch (InterruptedException|ExecutionException ex) {
                LOG.catching(ex);
            }
            jProgressBar1.setValue(0); 
            //jProgressBar1.setVisible(false); 
        }
        
        @Override
        public void ProgressChanged(ProgressEvent anEvt) {
            setProgress(anEvt.getProgress());             
        }
    }
        
    private void initPanels(IMultiframeImage dcm) {
        
        jPanel1.removeAll();
        ImageView image = ImageView.create(dcm, ViewMode.DEFAULT); 
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(image, BorderLayout.CENTER);
        jPanel1.add(LUTControl.create(image), BorderLayout.LINE_END);
        
        switch (dcm.getImageType().getTypeName()){
            case ImageType.NM_DYNAMIC:
            case ImageType.NM_GATED:
                jPanel1.add(FrameControl.create(image), BorderLayout.PAGE_END);
            break;
            default:
            break;
        }

        jPanel1.validate();
        jPanel1.repaint();
        jMenuItem3.setEnabled(true);    
    }
    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        FileOpenDialog dlg = new FileOpenDialog(this, 
                                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CHOICE_FILE_TO_OPEN"), 
                                                "dcm", //NOI18N 
                                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("DICOM_FILE_TITLE"),
                                                Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home")),
                                                true );                                               
        if(dlg.DoModal()) {                     
            SwingImageLoader l = new SwingImageLoader(dlg.getFileName());
            l.addPropertyChangeListener(this);             
            l.execute();
            jProgressBar1.setVisible(true);
            jProgressBar1.setString("Loading image(s)" + dlg.getFileName());
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        LOG.info("<--BYE, BYE...");// NOI18N
        dispose();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
/*        ROIListPanel panel = new ROIListPanel((IImageView)(jPanel1.getComponent(0)));
        JDialog dialog = new JDialog(this, "ROI manager", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(panel);
        dialog.validate();
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);
        repaint();
        final ProgressListener pl = this;
        new Thread(
                new Runnable() {                    
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i <= 100; ++i) {
                                Thread.sleep(100);
                                pl.ProgressChanged(new ProgressEvent(this, i));
                            }
                        } catch (InterruptedException ex) {
                            
                        }        
                     } 
                }).start();   */ 
    }//GEN-LAST:event_jMenuItem3ActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TOMO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TOMO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TOMO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TOMO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TOMO().setVisible(true);
            }
        });
    }
    
    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables

   
}
