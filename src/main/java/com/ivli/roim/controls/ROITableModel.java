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

import com.ivli.roim.core.Measurement;
import com.ivli.roim.view.Overlay;
import com.ivli.roim.view.ROI;
import com.ivli.roim.view.ROIManager;
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
    private static final String KCOMMAND_INVOKE_COLOUR_PICKER = "ROI_TABLE_MODEL.KCOMMAND_INVOKE_COLOUR_PICKER"; // NOI18N     
              
    public final static int TABLE_COLUMN_OBJECT = 0;
    public final static int TABLE_COLUMN_CHECK  = 1;
    public final static int TABLE_COLUMN_NAME   = 2;
    public final static int TABLE_COLUMN_PIXELS = 3;
    public final static int TABLE_COLUMN_COUNTS = 4;
    public final static int TABLE_COLUMN_COLOR  = 5;
    
    private final static String DEFAULT_COLUMN_NAMES[] = {"OBJ", //NOI18N - placeholder for a reference to the object   
                                " ", //NOI18N
                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_TABLE_HEADER.NAME"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_TABLE_HEADER.PIXELS"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_TABLE_HEADER.DENSITY"), 
                                java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_TABLE_HEADER.COLOUR"),                               
                               };
    
    private static final Class DEFAULT_COLUMN_CLASSES[] = {java.lang.Object.class, // a reference to ROI object - hidden
                                 java.lang.Boolean.class, // add to a list of curves
                                 java.lang.String.class,  // name - editable
                                 java.lang.Integer.class, // area in pixels
                                 java.lang.Integer.class, // density TOD: perhaps oughta be configurable ???
                                 java.awt.Color.class     // colour - editable                                 
                                };
    
   
    private static final boolean DEFAULT_PRIVILEGES_READONLY[] = {false,false, false,false,false,false};    
    private static final boolean DEFAULT_PRIVILEGES_EDITABLE[] = {false, true,true,false,false,true};  
          
    private final boolean iEditable;
    
    ROITableModel(ROIManager aMgr, int aActiveFrame, boolean aEditable) {          
        iEditable = aEditable;// ? DEFAULT_PRIVILEGES_EDITABLE : DEFAULT_PRIVILEGES_READONLY;
        setDataVector (new Object [][] {}, DEFAULT_COLUMN_NAMES); 
        rebuild(aMgr, aActiveFrame);
        /*
        Iterator<Overlay> aList = aMgr.getObjects();
        
        while (aList.hasNext()) {
            Overlay o = aList.next();
            if (o instanceof ROI) {       
                final ROI r = (ROI)o;                   
                addRow(new Object[]{o, false, r.getName(), r.getAreaInPixels(), 
                    // use average value for multiframes
                    -1 == aActiveFrame ? r.getSeries(Measurement.DENSITY).processor().avg() : r.getSeries(Measurement.DENSITY).get(aActiveFrame), 
                    r.getColor()});                                        
            }
        }
        */
        
        addTableModelListener((TableModelEvent e) -> {
            final int row = e.getFirstRow();
            final int col = e.getColumn();
            
            if (col == TABLE_COLUMN_NAME || col == TABLE_COLUMN_COLOR) {
                final TableModel model = (TableModel)e.getSource();
                
                assert(model.getValueAt(row, TABLE_COLUMN_OBJECT) instanceof ROI);
                
                final ROI r = (ROI)model.getValueAt(row, TABLE_COLUMN_OBJECT);
                
                if (col == TABLE_COLUMN_NAME) {
                    r.setName((String)model.getValueAt(row, col));
                    
                } else if (col == TABLE_COLUMN_COLOR) {
                    r.setColor((Color)model.getValueAt(row, col));        
                }                                
            }
        });
    }
    
    void rebuild(ROIManager aMgr, int aActiveFrame) {    
        Iterator<Overlay> aList = aMgr.getObjects();
        
        while (aList.hasNext()) {
            Overlay o = aList.next();
            if (o instanceof ROI) {       
                final ROI r = (ROI)o;                   
                addRow(new Object[]{o, false, r.getName(), r.getAreaInPixels(), 
                    // use average value for multiframes
                    -1 == aActiveFrame ? r.getSeries(Measurement.DENSITY).processor().avg() : r.getSeries(Measurement.DENSITY).get(aActiveFrame), 
                    r.getColor()});                                        
            }
        }
    }
    
    @Override
    public Class getColumnClass(int columnIndex) {
        return DEFAULT_COLUMN_CLASSES[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return iEditable ? DEFAULT_PRIVILEGES_EDITABLE[columnIndex] : DEFAULT_PRIVILEGES_READONLY[columnIndex];
    }
        
    /**
     * this methods formats table columns and fills the table data
     * @param aTable - table to attach to SIC: you must set an instance of this class as a model to a given table      
     */
    public void attach(javax.swing.JTable aTable) {        
        aTable.setModel(this); 
        aTable.setAutoCreateRowSorter(true);
        aTable.setDefaultEditor(Color.class, new ColorEditor());
        aTable.setDefaultRenderer(Color.class, new MyRenderer());  
        ///aTable.getTableHeader().
        ///aTable.s
        
        //unconditionally make "OBJ" column invisible
        aTable.getColumnModel().getColumn(TABLE_COLUMN_OBJECT).setMinWidth(0);
        aTable.getColumnModel().getColumn(TABLE_COLUMN_OBJECT).setPreferredWidth(0);
        aTable.getColumnModel().getColumn(TABLE_COLUMN_OBJECT).setMaxWidth(0);            
        
        if (!iEditable) {
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).setMinWidth(0);
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).setPreferredWidth(0);
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).setMaxWidth(0);   
        } else {            
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).setPreferredWidth(16);
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).setResizable(false); 
            aTable.getColumnModel().getColumn(TABLE_COLUMN_CHECK).sizeWidthToFit();
        }
        
        aTable.getColumnModel().getColumn(TABLE_COLUMN_NAME).sizeWidthToFit();
        aTable.getColumnModel().getColumn(TABLE_COLUMN_PIXELS).sizeWidthToFit();
        
                       
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
            button.setActionCommand(KCOMMAND_INVOKE_COLOUR_PICKER);
            button.addActionListener(this);
            button.setBorderPainted(false);

            //Set up the dialog that the button brings up.
            //JColorChooser.setDefaultLocale(new Locale("fr", "FR"));
            
                       
            colorChooser = new JColorChooser();
            
            dialog = JColorChooser.createDialog(button,
                        java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("COLOR_CHOOSER_DIALOG.PICK_A_COLOR"),
                        true,  //modal
                        colorChooser,
                        this,  //OK button handler
                        null); //no CANCEL button handler
        }

        public void actionPerformed(ActionEvent e) {
            if (KCOMMAND_INVOKE_COLOUR_PICKER.equals(e.getActionCommand())) {
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
            button.setActionCommand(KCOMMAND_INVOKE_COLOUR_PICKER);      
            button.setBorderPainted(false);            
            button.setBackground((Color)value);
            return button;
        }   
    }
}
