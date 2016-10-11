/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import com.ivli.roim.view.IImageView;
import com.ivli.roim.view.ImageViewGroup;
import com.ivli.roim.view.Overlay;
import com.ivli.roim.view.ROIManager;
import java.awt.FileDialog;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;

/**
 *
 * @author likhachev
 */
public class ROIListPanel extends JPanel {                           
    private final ROITableModel iModel;    
    private int bi = 0;

    private final ROIManager iMgr;
    
    public ROIListPanel(ImageViewGroup aV) {
        iModel = new ROITableModel(aV.getROIMgr(), -1, true);      
        iMgr = aV.getROIMgr();
        construct();     
    }
    
    public ROIListPanel(IImageView aV) {               
        iModel = new ROITableModel(aV.getROIMgr(), aV.getFrameNumber(), true); 
        iMgr = aV.getROIMgr();
        construct();       
    }
    
    private void construct() {    
        initComponents();
     
        iModel.attach(jTable1);   
        iModel.addTableModelListener((TableModelEvent e) -> {        
            final int row = e.getFirstRow();
            final int col = e.getColumn();
         
            if (col == ROITableModel.TABLE_COLUMN_CHECK) {                
                if ((Boolean)iModel.getValueAt(row, col)) {
                    ++bi;
                } else {
                    --bi;
                }
                
                final boolean b = bi > 0;
                                    
                jButtonDelete.setEnabled(b);
                //jButton6.setEnabled(b);
                jButtonShowHideCurve.setEnabled(b);                  
            }        
        });    
    } 

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonShowHideCurve = new javax.swing.JButton();
        jButtonSelectAll = new javax.swing.JButton();
        jButtonSaveLoad = new javax.swing.JButton();

        jTable1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTable1.setModel(iModel);
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jScrollPane1.setViewportView(jTable1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jButton1.setText(bundle.getString("OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButtonDelete.setText(bundle.getString("MNU_ROI_OPERATIONS.DELETE")); // NOI18N
        jButtonDelete.setEnabled(false);
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonShowHideCurve.setText("Show curve");
        jButtonShowHideCurve.setEnabled(false);

        jButtonSelectAll.setText(bundle.getString("SELECT_ALL")); // NOI18N
        jButtonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectAllActionPerformed(evt);
            }
        });

        jButtonSaveLoad.setText(bundle.getString("ROI_DLG.SAVE_LOADLOAD")); // NOI18N
        jButtonSaveLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveLoadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSaveLoad)
                        .addGap(49, 49, 49)
                        .addComponent(jButtonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShowHideCurve)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSelectAll)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addComponent(jButtonSelectAll)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButtonSaveLoad)
                    .addComponent(jButtonDelete)
                    .addComponent(jButtonShowHideCurve))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Window w = SwingUtilities.getWindowAncestor(this);
        w.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        for (int i = iModel.getRowCount()-1; i >= 0; --i)             
            if((boolean)iModel.getValueAt(i, ROITableModel.TABLE_COLUMN_CHECK)) {
                iMgr.deleteObject((Overlay)iModel.getValueAt(i, ROITableModel.TABLE_COLUMN_OBJECT));
                iModel.removeRow(i);
            }    
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButtonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectAllActionPerformed
        for (int i = 0; i < iModel.getRowCount(); ++i)             
            iModel.setValueAt(!(boolean)iModel.getValueAt(i, ROITableModel.TABLE_COLUMN_CHECK), i, ROITableModel.TABLE_COLUMN_CHECK);        
    }//GEN-LAST:event_jButtonSelectAllActionPerformed

    private void jButtonSaveLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveLoadActionPerformed
        ArrayList<Overlay> sel = new ArrayList<>();
        
        for (int i = iModel.getRowCount() - 1; i >= 0; --i)             
            if((boolean)iModel.getValueAt(i, ROITableModel.TABLE_COLUMN_CHECK))                 
                sel.add((Overlay)iModel.getValueAt(i, ROITableModel.TABLE_COLUMN_OBJECT));
               
        FileDialog fd = new FileDialog((JDialog)null, "Choose", sel.isEmpty()? FileDialog.LOAD:FileDialog.SAVE);     
       
        fd.setVisible(true);

        if (null == fd.getFile()) return;
        
        if (!sel.isEmpty()) {                             
            try(FileOutputStream fos = new FileOutputStream(fd.getFile())){
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(sel);            
            } catch (java.io.IOException ex) {
                LOG.catching(ex);
            }   
        } else {        
            try(FileInputStream fis = new FileInputStream(fd.getFile())){
                ObjectInputStream ois = new ObjectInputStream(fis);
                sel = (ArrayList<Overlay>)ois.readObject();
                for (Overlay o:sel) 
                    iMgr.cloneObject(o);                
                iModel.rebuild(iMgr, -1);
                
            } catch (java.io.IOException|ClassNotFoundException ex) {
                LOG.catching(ex);
            }   

        }
    }//GEN-LAST:event_jButtonSaveLoadActionPerformed
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonSaveLoad;
    private javax.swing.JButton jButtonSelectAll;
    private javax.swing.JButton jButtonShowHideCurve;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}



