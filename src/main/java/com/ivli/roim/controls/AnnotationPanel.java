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
package com.ivli.roim.controls;

import javax.swing.table.AbstractTableModel;
import javax.swing.SwingUtilities;
import com.ivli.roim.core.Filter;
import com.ivli.roim.core.Measurement;
/**
 *
 * @author likhachev
 */
public class AnnotationPanel extends javax.swing.JPanel {
    Object rowData[][];
    final String columnNames[] = {java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ANNOTATION_PANEL.NAME"), 
                                  java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ANNOTATION_PANEL.DISPLAY")};
                                
    
    private class PropertyTableModel extends AbstractTableModel {
        @Override
        public int getColumnCount() {
          return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
          return columnNames[column];
        }

        @Override
        public int getRowCount() {
          return rowData.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
          return rowData[row][column];
        }

        @Override
        public Class getColumnClass(int column) {
          return (getValueAt(0, column).getClass());
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            rowData[row][column] = value;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
          return (column != 0);
        }
    }    
        
   private final String[] str = Measurement.getAllMeasurements();
   private final com.ivli.roim.view.Annotation.Static iAnno;
    /**
     * Creates new form AnnotationPanel
     * @param anA
     */
    public AnnotationPanel(com.ivli.roim.view.Annotation.Static anA) {
        iAnno = anA;
        rowData = new Object[str.length][2];
                
        ///com.ivli.roim.core.Filter [] fs = anA.getFilters();
        for(int n=0; n < str.length; ++n ) {
            rowData[n][0] = str[n];
            rowData[n][1] = false;
            
            for (com.ivli.roim.core.Filter f : iAnno.getFilters())
                if (f.getMeasurement().getName().equals(str[n])) {
                    rowData[n][1] = true;
                    break;
                }            
        }
        
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        jTable1.setModel(new PropertyTableModel());
        jScrollPane1.setViewportView(jTable1);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle"); // NOI18N
        jButton1.setText(bundle.getString("OK")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jCheckBox1.setText(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MULTILINE"));
        jCheckBox1.setSelected(iAnno.isMultiline());
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(83, 83, 83))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jCheckBox1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        java.util.ArrayList<com.ivli.roim.core.Filter> f = new java.util.ArrayList<>();
        
        int cnt = 0;
        for(int n=0; n < jTable1.getRowCount(); ++n ) {          
            if (true == (boolean)jTable1.getModel().getValueAt(n, 1)) 
                cnt++;//f.add(Filter.getFilter((String)rowData[n][0]));
        }
        
        com.ivli.roim.core.Filter []fi = new com.ivli.roim.core.Filter[cnt];
        
        for(int n=0; n < jTable1.getRowCount(); ++n ) {
            if (true == (boolean)jTable1.getModel().getValueAt(n, 1))
                fi[n] = Filter.getFilter((String)rowData[n][0]);
        }
              
        iAnno.setFilters(fi);
        iAnno.setMultiline(jCheckBox1.isSelected());
        SwingUtilities.getWindowAncestor(this).setVisible(false);        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
