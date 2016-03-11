/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.MouseEvent;
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
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author likhachev
 */
public class CurvePanel extends ChartPanel {
   
    public CurvePanel(JFreeChart aChart) {
        super(aChart);
    }
    
    public void addMarker(DomainMarker aM) {
        getChart().getXYPlot().addDomainMarker(aM, Layer.FOREGROUND);
        ValueMarker vm = new ValueMarker(.0);
        getChart().getXYPlot().addRangeMarker(vm, Layer.FOREGROUND);
       // aM.setLinkedMarker(vm);        
    }
    
    private ChartEntity findEntity(MouseEvent event) {              
        if (null != getChart()) {
            final Insets insets = getInsets();
            final int x = (int) ((event.getX() - insets.left) / this.getScaleX());
            final int y = (int) ((event.getY() - insets.top) / this.getScaleY());

            if (this.getChartRenderingInfo() != null) {
                EntityCollection entities = this.getChartRenderingInfo().getEntityCollection();

                if (entities != null) 
                    return entities.getEntity(x, y);                                                
            }
        }
        return null;
    }
            
    private ValueMarker findMarker(MouseEvent event) {
        XYPlot plot = getChart().getXYPlot();
        
        final double domainX = plot.getDomainAxis().java2DToValue(event.getX(), 
                                                                  getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                                                                  plot.getDomainAxisEdge());
        final double EPSILON = 10.5;

        java.util.Collection mark = plot.getDomainMarkers(Layer.FOREGROUND);
        if (null == mark || mark.isEmpty())
            return null;
        
        for (Object o : mark) {
            if (o instanceof DomainMarker) {           
                //DomainMarker m = (DomainMarker)o;
                double val = ((DomainMarker)o).getValue();
                if (val >= domainX - EPSILON && val <= domainX + EPSILON) {
                    //getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR)); 
                    return (ValueMarker)o;
                }
            }
        }
        return null;
    }
            
    public void mouseMoved(MouseEvent event) {
        super.mouseMoved(event);

        if (null == sel && null == mr) {                    
            ChartEntity entity = findEntity(event);
            if (entity instanceof XYItemEntity) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));                 
            } else if (entity instanceof PlotEntity) {

                if (null != findMarker(event))
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }              
            
    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);

        ChartEntity entity = findEntity(event);

        if (entity instanceof XYItemEntity) {
            selectXYItem(entity);
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else if (null != (mr = findMarker(event))) {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));                    
        } 
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        sel = null;
        mr = null;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        super.mouseDragged(event);
        XYPlot plot = getChart().getXYPlot();
        
        if (null != sel) {                   
            moveTo(plot.getRangeAxis().java2DToValue(event.getY(), 
                      getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                        plot.getRangeAxisEdge()));
        }  else if (null != mr) {
            final double val = plot.getDomainAxis().java2DToValue(event.getX(), getChartRenderingInfo().getPlotInfo().getDataArea(),                                                                                                                 
                                                             plot.getDomainAxisEdge());
            mr.setValue(val);
            //mr.setLabel(String.format("%.3f", mr.getValue()));
        }
    }
    
    
    private ChartEntity sel = null;
    private XYSeries    ts  = null;
    private XYDataItem  xy  = null;
    private ValueMarker mr = null;
   
    protected void selectXYItem(ChartEntity anEntity) {
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
