/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import com.ivli.roim.Overlay;
import com.ivli.roim.ROI;
import com.ivli.roim.RoiStats;
import static com.ivli.roim.controls.ColorEditor.EDIT;
import java.util.Iterator;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.event.*;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;
import java.awt.Window;

/**
 *
 * @author likhachev
 */
public class ROIListPanel extends javax.swing.JPanel implements TableModelListener {

    public ROIListPanel(Iterator<Overlay> aLit) {
        
        tableModel = new DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("OBJ"), java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NAME"), java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("PIXELS"), java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INTDEN"), java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NULL")
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.awt.Color.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        };
        
        initComponents();
     
        
        /// jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setMinWidth(0);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
            jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
            jTable1.getColumnModel().getColumn(0).setHeaderValue("OBJ"); //NOI18N
            
            jTable1.getColumnModel().getColumn(1).setHeaderValue(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NAME"));
            jTable1.getColumnModel().getColumn(2).setHeaderValue(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("PIXELS"));
            jTable1.getColumnModel().getColumn(3).setHeaderValue(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INTDEN"));
            jTable1.getColumnModel().getColumn(4).setHeaderValue(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("COLOR"));
        }
        
        while (null!=aLit && aLit.hasNext()) {
            Overlay o = aLit.next();
            if (o instanceof ROI) {       
                ROI r = (ROI)o;
                RoiStats s = r.getStats();
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                //jTable1.setModel(new TableModel());
                model.addRow(new Object[]{r, r.getName(), r.getStats().iPixels, r.getStats().iIden, r.getColor()});                
            }
        }
        
        jTable1.getModel().addTableModelListener(this);
        jTable1.setDefaultEditor(Color.class, new ColorEditor());
        jTable1.setDefaultRenderer(Color.class, new MyRenderer());
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        final int row = e.getFirstRow();   
        final int col = e.getColumn();
        
        if (col == 1 || col == 4) {
            TableModel model = (TableModel)e.getSource();

            assert(model.getValueAt(row, 0) instanceof ROI);

            ROI r = (ROI)model.getValueAt(row, 0);
            if (col == 1)
                r.setName((String)model.getValueAt(row, 1));
            else if (col == 4)
                r.setColor((Color)model.getValueAt(row, 4));
            this.getParent().invalidate();
        }
        
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

        jTable1.setModel(tableModel);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
       Window w = SwingUtilities.getWindowAncestor(this);
       w.setVisible(false);

    }//GEN-LAST:event_jButton1ActionPerformed
    
        
    private DefaultTableModel tableModel;
            
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}


final class MyRenderer implements TableCellRenderer {
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column) {
        JButton button = new JButton();
        button.setActionCommand(EDIT);
       // button.addActionListener(this);
        button.setBorderPainted(false);
        Color clr = (Color )value;
        button.setBackground(clr);
        return button;
    }
}

final class ColorEditor extends javax.swing.AbstractCellEditor
                         implements javax.swing.table.TableCellEditor,
                                    java.awt.event.ActionListener {
    Color currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("EDIT");

    public ColorEditor() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                    java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("PICK A COLOR"),
                    true,  //modal
                    colorChooser,
                    this,  //OK button handler
                    null); //no CANCEL button handler
    }

    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            fireEditingStopped(); //Make the renderer reappear.

        } else { //User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (Color)value;
        return button;
    }
}