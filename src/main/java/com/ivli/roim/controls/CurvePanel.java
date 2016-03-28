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
    private final static String KCommandMarkerLabelOn  = "MARKER_CMD_TURN_LABEL_ON";//NOI18N
    private final static String KCommandMarkerLabelOff = "MARKER_CMD_TURN_LABEL_OFF";//NOI18N
    private final static String KCommandMarkerMoveToMax  = "MARKER_CMD_MOVE_TO_MAX";//NOI18N
    private final static String KCommandMarkerMoveToMin  = "MARKER_CMD_MOVE_TO_MIN";//NOI18N
    
    public CurvePanel(JFreeChart aChart) {
        super(aChart);
    }
    
    public void addMarker(DomainMarker aM) {
        getChart().getXYPlot().addDomainMarker(aM, Layer.FOREGROUND);        
        ValueMarker vm = new ValueMarker(.0);
        getChart().getXYPlot().addRangeMarker(vm, Layer.FOREGROUND);
        aM.setLinkedMarker(vm);        
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
        
        final double domainX = plot.getDomainAxis().java2DToValue(e.getX(), 
                                                                  getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                                                                  plot.getDomainAxisEdge());
        
        final double Epsilon = plot.getDataRange(plot.getDomainAxis()).getLength() * .01d;

        java.util.Collection mark = plot.getDomainMarkers(Layer.FOREGROUND);
        if (null == mark || mark.isEmpty())
            return null;
        
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
       
    double getDomainValueOfMinimum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MAX_VALUE;
        double valX = Double.NaN;
        
        for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] < valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    double getDomainValueOfMaximum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MIN_VALUE;
        double valX = Double.NaN;
        
       for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] > valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        final XYPlot plot = getChart().getXYPlot();
        
        switch (e.getActionCommand()) {
            case KCommandMarkerAdd:
                if (null != mr && mr instanceof DomainMarker) {
                    DomainMarker m = (DomainMarker)mr;
                    DomainMarker m2 = new DomainMarker(m.getXYSeries());
                    addMarker(m2);
                } else if (null != ts && ts instanceof XYSeries) {
                    addMarker(new DomainMarker(ts));
                }   break;
            case KCommandMarkerMoveToMax: {               
                DomainMarker dm =(DomainMarker)mr;              
                dm.setValue(getDomainValueOfMaximum(dm.getXYSeries()));                    
            } break;
            case KCommandMarkerMoveToMin: {                
                DomainMarker dm =(DomainMarker)mr;              
                dm.setValue(getDomainValueOfMinimum(dm.getXYSeries()));
            } break;                
            default:
                break;
        }
        
        dropSelection();
        
    }
    
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (null == sel && null == mr) {                    
            ChartEntity entity = findEntity(e);
            if (entity instanceof XYItemEntity) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));                 
            } else if (entity instanceof PlotEntity) {

                if (null != findMarker(e))
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }              
            
    @Override
    public void mousePressed(MouseEvent e) {  
        ChartEntity entity = findEntity(e);

        if (entity instanceof XYItemEntity) {
            selectItem(entity);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else if (null != (mr = findMarker(e))) {                
            if (SwingUtilities.isLeftMouseButton(e))                 
                setCursor(new Cursor(Cursor.HAND_CURSOR));                    
       
        } else
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (SwingUtilities.isRightMouseButton(e) && (mr instanceof DomainMarker || ts instanceof XYSeries)) {
            JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MNU_MARKER_OPERATIONS")); 
            {
            JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.ADD_MARKER"));
            mi11.addActionListener(this);
            mi11.setActionCommand(KCommandMarkerAdd);
            mnu.add(mi11);
            }
            if (mr instanceof DomainMarker)  {
                { //KCommandMarkerMoveToMax  = "MARKER_CMD_MOVE_TO_MAX
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN"));
                mi11.addActionListener(this);
                mi11.setActionCommand(KCommandMarkerMoveToMin);
                mnu.add(mi11);            
                }{
                JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX"));
                mi11.addActionListener(this);
                mi11.setActionCommand(KCommandMarkerMoveToMax);
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
        super.mouseDragged(e);
        final XYPlot plot = getChart().getXYPlot();
        
        if (null != sel) {                   
            moveTo(plot.getRangeAxis().java2DToValue(e.getY(), 
                      getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                        plot.getRangeAxisEdge()));
        }  else if (null != mr) {
            final double val = plot.getDomainAxis().java2DToValue(e.getX(), getChartRenderingInfo().getPlotInfo().getDataArea(),                                                                                                                 
                                                                      plot.getDomainAxisEdge());
            mr.setValue(val);
            //mr.setLabel(String.format("%.3f", mr.getValue()));
        }
    }
  
    void dropSelection() {
        sel = null;
        ts = null;
        xy = null;
        mr = null;  
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    private ChartEntity sel = null;
    private XYSeries    ts  = null;
    private XYDataItem  xy  = null;
    private ValueMarker mr = null;
   
    protected void selectItem(ChartEntity anEntity) {
        sel = anEntity;
        ts = ((XYSeriesCollection)((XYItemEntity)sel).getDataset()).getSeries(((XYItemEntity)sel).getSeriesIndex());        
        xy = ts.getDataItem(((XYItemEntity)sel).getItem());  
    }
            
    protected void moveTo(double aNewY) {        
        xy.setY(aNewY);
        ts.delete(ts.indexOf(xy.getX()), ts.indexOf(xy.getX()));
        ts.add(xy);         
    }
   
}
