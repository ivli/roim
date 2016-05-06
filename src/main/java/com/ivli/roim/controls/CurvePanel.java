/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author likhachev
 */
public class CurvePanel extends ChartPanel {

    private final static String KCommandMarkerAdd      = "MARKER_CMD_MARKER_ADD"; //NOI18N
    private final static String KCommandMarkerDelete   = "MARKER_CMD_MARKER_DELETE"; //NOI18N
    private final static String KCommandMarkerValueOn  = "MARKER_CMD_TURN_VALUE_MARKER_ON";//NOI18N
    private final static String KCommandMarkerValueOff = "MARKER_CMD_TURN_VALUE_MARKER_OFF";//NOI18N
    private final static String KCommandMarkerLabelOn    = "MARKER_CMD_TURN_LABEL_ON";//NOI18N
    private final static String KCommandMarkerLabelOff   = "MARKER_CMD_TURN_LABEL_OFF";//NOI18N
    private final static String KCommandMarkerMoveToMax  = "MARKER_CMD_MOVE_TO_MAX";//NOI18N
    private final static String KCommandMarkerMoveToMin  = "MARKER_CMD_MOVE_TO_MIN";//NOI18N
    private final static String KCommandMarkerDeleteAll  = "MARKER_CMD_DELETE_ALL";//NOI18N
    
    public CurvePanel(JFreeChart aChart) {
        super(aChart);
    }
    
    public void addMarker(DomainMarker aM) {
        getChart().getXYPlot().addDomainMarker(aM, Layer.FOREGROUND);        
        ValueMarker vm = new ValueMarker(.0);
        getChart().getXYPlot().addRangeMarker(vm, Layer.FOREGROUND);
        aM.setLinkedMarker(vm);        
    }
    
    public void removeMarker(DomainMarker aM) {
        getChart().getXYPlot().removeRangeMarker(aM.getLinkedMarker(), Layer.FOREGROUND);
        getChart().getXYPlot().removeDomainMarker(aM, Layer.FOREGROUND);                 
    }
            
    private ChartEntity findEntity(MouseEvent e) {              
        if (null != getChart()) {
            final Insets insets = getInsets();
            final int x = (int) ((e.getX() - insets.left) / this.getScaleX());
            final int y = (int) ((e.getY() - insets.top) / this.getScaleY());

            if (this.getChartRenderingInfo() != null) {
                EntityCollection entities = this.getChartRenderingInfo().getEntityCollection();

                if (entities != null) 
                    return entities.getEntity(x, y);                                                
            }
        }
        return null;
    }
            
    private ValueMarker findMarker(MouseEvent e) {
        final XYPlot plot = getChart().getXYPlot();
        
        java.util.Collection mark = plot.getDomainMarkers(Layer.FOREGROUND);
        
        if (null == mark || mark.isEmpty())
            return null;
        
        final double domainX = plot.getDomainAxis().java2DToValue(e.getX(), 
                                                                  getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                                                                  plot.getDomainAxisEdge());
        
        final double Epsilon = plot.getDataRange(plot.getDomainAxis()).getLength() * .01d;

        
        
        for (Object o : mark) {
            if (o instanceof DomainMarker) {           
                //DomainMarker m = (DomainMarker)o;
                double val = ((DomainMarker)o).getValue();
                if (val >= domainX - Epsilon && val <= domainX + Epsilon) {
                    //getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR)); 
                    return (ValueMarker)o;
                }
            }
        }
        return null;
    }  
    
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        final XYPlot plot = getChart().getXYPlot();
        
        switch (e.getActionCommand()) {
            case KCommandMarkerAdd:
                //if (null != iMarker && iMarker instanceof DomainMarker) {
                //    DomainMarker m = (DomainMarker)iMarker;
                //    DomainMarker m2 = new DomainMarker(m.getXYSeries());
                //    addMarker(m2);
                //} else 
                if (null != iSeries && iSeries instanceof XYSeries) {                    
                    addMarker(new DomainMarker(iDataItem.getXValue(), iSeries));                                                  
                }   break;
            case KCommandMarkerMoveToMax:                                              
                ((DomainMarker)iMarker).moveToMaximum();                    
            break;
            case KCommandMarkerMoveToMin:                 
                ((DomainMarker)iMarker).moveToMinimum(); 
            break;  
            case KCommandMarkerDelete:
                removeMarker((DomainMarker)iMarker);
                break;
            case KCommandMarkerDeleteAll:
                break;
            default:
                break;
        }
        
        dropSelection();        
    }
    
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (null == iEntity && null == iMarker) {                    
            ChartEntity entity = findEntity(e);
            if (entity instanceof XYItemEntity) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));                 
            } else if (entity instanceof PlotEntity) {

                if (null != findMarker(e))
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                else
                    setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }              
            
    @Override
    public void mousePressed(MouseEvent e) {  
        ChartEntity entity = findEntity(e);

        if (entity instanceof XYItemEntity) {
            selectItem(entity);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (null != (iMarker = findMarker(e))) {                
            if (SwingUtilities.isLeftMouseButton(e))                 
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));                    
       
        } else
        super.mousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (SwingUtilities.isRightMouseButton(e) && (iMarker instanceof DomainMarker || iSeries instanceof XYSeries)) {
            JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MNU_MARKER_OPERATIONS")); 
            if (iSeries instanceof XYSeries) {
                {
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.ADD_MARKER"));
                mi11.addActionListener(this);
                mi11.setActionCommand(KCommandMarkerAdd);
                mnu.add(mi11);
                }
                {
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.DELETE_ALL_MARKERS"));
                mi11.setActionCommand(KCommandMarkerDeleteAll);
                mi11.addActionListener(this);            
                mnu.add(mi11);            
                }
            }
            if (iMarker instanceof DomainMarker)  {
                { 
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN"));
                mi11.setActionCommand(KCommandMarkerMoveToMin);
                mi11.addActionListener(this);               
                mnu.add(mi11);            
                }{
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX"));
                mi11.setActionCommand(KCommandMarkerMoveToMax);
                mi11.addActionListener(this);               
                mnu.add(mi11);            
                }{
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MARKER_DELETE"));
                mi11.setActionCommand(KCommandMarkerDelete);
                mi11.addActionListener(this);               
                mnu.add(mi11);   
                }                
            }
            
            mnu.show(this, e.getX(), e.getY());
        } else {
            super.mouseReleased(e);
            dropSelection();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {        
        final XYPlot plot = getChart().getXYPlot();
        
        if (null != iEntity) {                   
            moveTo(plot.getRangeAxis().java2DToValue(e.getY(), 
                    getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                        plot.getRangeAxisEdge()));
        }  
        if (null != iMarker) {
            iMarker.setValue(plot.getDomainAxis().java2DToValue(e.getX(), 
                            getChartRenderingInfo().getPlotInfo().getDataArea(),                                                                                                                 
                                plot.getDomainAxisEdge()));      
        }
        else
            super.mouseDragged(e);
    }
  
    void dropSelection() {
        iEntity = null;
        iSeries = null;
        iDataItem = null;
        iMarker = null;  
        setCursor(Cursor.getDefaultCursor());
    }
    
    private ChartEntity iEntity = null;
    private XYSeries    iSeries  = null;
    private XYDataItem  iDataItem  = null;
    private ValueMarker iMarker  = null;
   
    protected void selectItem(ChartEntity anEntity) {
        iEntity = anEntity;
        iSeries = ((XYSeriesCollection)((XYItemEntity)iEntity).getDataset()).getSeries(((XYItemEntity)iEntity).getSeriesIndex());        
        iDataItem = iSeries.getDataItem(((XYItemEntity)iEntity).getItem());  
    }
            
    protected void moveTo(double aNewY) {        
        iDataItem.setY(aNewY);
        iSeries.delete(iSeries.indexOf(iDataItem.getX()), iSeries.indexOf(iDataItem.getX()));
        iSeries.add(iDataItem);         
    }   
}
