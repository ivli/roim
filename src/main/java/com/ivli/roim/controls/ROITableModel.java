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

import com.ivli.roim.Overlay;
import com.ivli.roim.ROI;
import java.awt.Color;
import java.awt.Component;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author likhachev
 */
public class ROITableModel extends DefaultTableModel {
    private static final String KCommandInvokeColourPicker = "COMMAND_INVOKE_COLOUR_PICKER_DIALOG"; // NOI18N    
                
    protected final Class[]   iClasses;                                                   
    protected final String[]  iColumns;            
    protected final boolean[] iEditable;
            
    ROITableModel(Iterator<Overlay> aList, boolean aCanEdit) {     
        
        iColumns = new String[]{"OBJ", // NOI18N - holds an object reference  
                                java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_TABLE_HEADER.NAME"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_TABLE_HEADER.PIXELS"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_TABLE_HEADER.DENSITY"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_TABLE_HEADER.COLOUR") 
                               };

        iClasses = new Class [] {java.lang.Object.class,  // a reference to ROI object - hidden
                                 java.lang.String.class,  // name - can be editable
                                 java.lang.Integer.class, // area in pixels
                                 java.lang.Integer.class, // density
                                 java.awt.Color.class     // colour - can be editable
                                };

        iEditable = new boolean [] {false, 
                                    aCanEdit, 
                                    false, 
                                    false, 
                                    aCanEdit
                                   };
       
        
        setDataVector (new Object [][] {}, iColumns); 
        
        while (aList.hasNext()) {
            Overlay o = aList.next();
            if (o instanceof ROI) {       
                final ROI r = (ROI)o;                                
                    addRow(new Object[]{r, r.getName(), r.getAreaInPixels(), r.getDensity(), r.getColor()});                                        
            }
        }
         
        addTableModelListener((TableModelEvent e) -> {
            final int row = e.getFirstRow();
            final int col = e.getColumn();
            
            if (col == 1 || col == 4) {
                final TableModel model = (TableModel)e.getSource();
                
                assert(model.getValueAt(row, 0) instanceof ROI);
                
                final ROI r = (ROI)model.getValueAt(row, 0);
                
                if (col == 1) {
                    r.setName((String)model.getValueAt(row, 1));
                    
                }else if (col == 4) {
                    r.setColor((Color)model.getValueAt(row, 4));        
                }                                
            }
        });
    }
    
    @Override
    public Class getColumnClass(int columnIndex) {
        return iClasses [columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return iEditable [columnIndex];
    }
        
    /**
     * this methods formats table columns and fills the table data
     * @param aTable - table to attach to SIC: you must set an instance of this class as a model to a given table  
     * @param aList - a list of ROI 
     */
    public void attach(javax.swing.JTable aTable) {        
        aTable.setModel(this);       
        aTable.getColumnModel().getColumn(0).setMinWidth(0);
        aTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        aTable.getColumnModel().getColumn(0).setMaxWidth(0);            
        aTable.setAutoCreateRowSorter(true);
        aTable.setDefaultEditor(Color.class, new ColorEditor());
        aTable.setDefaultRenderer(Color.class, new MyRenderer());    
    }

    final class ColorEditor extends javax.swing.AbstractCellEditor
                             implements javax.swing.table.TableCellEditor,
                                        java.awt.event.ActionListener {
        private Color currentColor;
        private final JButton button;
        private final JColorChooser colorChooser;
        private final JDialog dialog;

        public ColorEditor() {
            button = new JButton();
            button.setActionCommand(KCommandInvokeColourPicker);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            //JColorChooser.setDefaultLocale(new Locale("fr", "FR"));
            
                       
            colorChooser = new JColorChooser();
            
            dialog = JColorChooser.createDialog(button,
                        java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("COLOR_CHOOSER_DIALOG.PICK_A_COLOR"),
                        true,  //modal
                        colorChooser,
                        this,  //OK button handler
                        null); //no CANCEL button handler
        }

        public void actionPerformed(ActionEvent e) {
            if (KCommandInvokeColourPicker.equals(e.getActionCommand())) {
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
        @Override
        public Object getCellEditorValue() {
            return currentColor;
        }

        //Implement the one method defined by TableCellEditor.
        @Override
        public Component getTableCellEditorComponent(JTable table,
                                                     Object value,
                                                     boolean isSelected,
                                                     int row,
                                                     int column) {
            currentColor = (Color)value;
            return button;
        }
    }

    final class MyRenderer implements TableCellRenderer {    
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JButton button = new JButton();
            button.setActionCommand(KCommandInvokeColourPicker);      
            button.setBorderPainted(false);            
            button.setBackground((Color)value);
            return button;
        }   
    }
    
    
    
          
}
