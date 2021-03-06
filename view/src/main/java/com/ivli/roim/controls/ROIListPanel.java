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

package com.ivli.roim.controls;

import com.ivli.roim.view.IImageView;
import com.ivli.roim.view.Overlay;
import com.ivli.roim.view.ROIManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.SwingUtilities;
import java.awt.Window;
/**
 *
 * @author likhachev
 */
public class ROIListPanel extends JPanel implements TableModelListener {                           
    private final ROITableModel iModel;        
    private final ROIManager iMgr;
    
    public ROIListPanel(ROIManager aMgr) {
        iModel = new ROITableModel(aMgr, -1, true);             
        iMgr = aMgr;
        construct();            
    }
   
    /**/
    public ROIListPanel(IImageView aV) {               
        iModel = new ROITableModel(aV.getROIMgr(), aV.getFrameNumber(), true); 
        iMgr = aV.getROIMgr();
        construct();               
    }
     
    private void construct() {    
        initComponents();
     
        iModel.attach(jTable1);  
        jButtonSelectAll.setEnabled(iModel.getRowCount() > 0);
        iModel.addTableModelListener(this);       
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

        jButtonShowHideCurve.setText(bundle.getString("ROILIST.SHOW_CURVE")); // NOI18N
        jButtonShowHideCurve.setEnabled(false);

        jButtonSelectAll.setText(bundle.getString("ROILIST.SELECT_ALL")); // NOI18N
        jButtonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectAllActionPerformed(evt);
            }
        });

        jButtonSaveLoad.setText(bundle.getString("ROILIST.SAVE_LOAD")); // NOI18N
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
                        .addGap(48, 48, 48)
                        .addComponent(jButtonDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonShowHideCurve)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonSelectAll)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonSelectAll)
                .addGap(18, 18, Short.MAX_VALUE)
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
               
        FileOpenDialog fd = new FileOpenDialog(
                                               java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CHOICE_FILE_TO_OPEN"), 
                                               !sel.isEmpty());     
       
        
        if (!fd.doModal())
            return;
        
        if (!sel.isEmpty()) {                             
            try(FileOutputStream fos = new FileOutputStream(fd.getFile())){
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(sel);            
            } catch (java.io.IOException ex) {
                LOG.catching(ex);
            }   
        } else {        
            try(FileInputStream fis = new FileInputStream(fd.getFile())){
                iMgr.internalize(new ObjectInputStream(fis));                
                iModel.rebuild(iMgr, -1);                
            } catch (java.io.IOException|ClassNotFoundException ex) {
                LOG.catching(ex);
                JOptionPane.showMessageDialog(this, 
                                              "Unable to open file", 
                                              "ERROR", 
                                              JOptionPane.ERROR_MESSAGE);
            }   
        }
    }//GEN-LAST:event_jButtonSaveLoadActionPerformed
    
    @Override
    public void tableChanged(TableModelEvent e) {
        switch (e.getType()) {
            case TableModelEvent.INSERT: 
                jButtonSelectAll.setEnabled(iModel.getRowCount() > 0); 
                break;
            case TableModelEvent.DELETE: { 
                final boolean ena = iModel.getRowCount() > 0;
                jButtonSelectAll.setEnabled(ena); 
                jButtonDelete.setEnabled(jButtonDelete.isEnabled() && ena);               
                jButtonShowHideCurve.setEnabled(jButtonShowHideCurve.isEnabled() && ena);
            } break;
            case TableModelEvent.UPDATE: { 
                
                if (ROITableModel.TABLE_COLUMN_CHECK == e.getColumn()) {                
                    boolean ena = false;

                    for (int r = 0; r < iModel.getRowCount(); ++r )               
                        ena |= (Boolean)iModel.getValueAt(r, ROITableModel.TABLE_COLUMN_CHECK);

                    jButtonDelete.setEnabled(ena);               
                    jButtonShowHideCurve.setEnabled(ena);   
                }  
            } 
            default: break;
        }
    }
   
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonSaveLoad;
    private javax.swing.JButton jButtonSelectAll;
    private javax.swing.JButton jButtonShowHideCurve;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

  
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}



