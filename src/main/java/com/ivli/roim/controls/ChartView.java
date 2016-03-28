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


import java.awt.BorderLayout;
import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Series;
import com.ivli.roim.ROI;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
/**
 *
 * @author likhachev
 */
public class ChartView extends javax.swing.JPanel 
                           implements ROIChangeListener { 
                        
    private XYPlot     iPlot;
    private JFreeChart iJfc;    
    private CurvePanel iChart;  
    
    
    public void initChart () {
        if (null != iPlot) {
            ((XYSeriesCollection)iPlot.getDataset()).removeAllSeries();
        } else {
            iPlot = new XYPlot();           
            iPlot.setRenderer(new StandardXYItemRenderer());
            iPlot.setDomainAxis(new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_CHART.TIME_SERIES_VALUES")));
            iPlot.setRangeAxis(0, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_CHART.ROI_INTDEN_VALUES")));           
            iPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            iPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

            iJfc   = new JFreeChart(iPlot); 
            iChart = new CurvePanel(iJfc);           
           
            iPlot.setDataset(new XYSeriesCollection());

            iChart.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            iChart.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            setLayout(new BorderLayout()); 
            
            this.add(iChart);            
        }
        iChart.setMouseZoomable(false);
        iChart.setMouseWheelEnabled(true);
    }
    
    @Override
    public void ROIChanged (ROIChangeEvent aE) {
        XYSeriesCollection col = ((XYSeriesCollection)iPlot.getDataset());
        
        switch (aE.getChange()) {
            case ROIChangeEvent.ROIDELETED: {                
                int ndx = col.indexOf(aE.getObject().getName());
                col.removeSeries(ndx);                
            } break;    
            case ROIChangeEvent.ROIMOVED: //fall-through
            case ROIChangeEvent.ROICHANGED: {                
                if (aE.getObject() instanceof ROI) {
                    int ndx = col.indexOf(aE.getObject().getName());    

                    Series c = ((ROI)aE.getObject()).getSeries(Measurement.DENSITY);
                    XYSeries s = col.getSeries(ndx); 
                    s.clear();

                    for (int n = 0; n < c.getNumFrames(); ++n) {
                        long dur = aE.getObject().getManager().getImage().getTimeSliceVector().getSlices().get(n) / 1000;
                        s.add(dur, c.get(n));
                    }
                }
            } break;
                
            case ROIChangeEvent.ROICREATED: {                
                final XYSeries s = new XYSeries(aE.getObject().getName(), false, false);
                final Series c = ((ROI)aE.getObject()).getSeries(Measurement.DENSITY);
               
                assert(c.getNumFrames() == aE.getObject().getManager().getImage().getTimeSliceVector().getNumFrames());
                
                for (int n = 0; n < c.getNumFrames(); ++n)   {                  
                    Double x = aE.getObject().getManager().getImage().getTimeSliceVector().getSlices().get(n) / 1000.;
                    Double y = c.get(n);
                    s.add(x, y);
                }

                ((XYSeriesCollection)iPlot.getDataset()).addSeries(s);   
                iPlot.getRenderer().setSeriesPaint(col.indexOf(aE.getObject().getName()), ((ROI)aE.getObject()).getColor());  
                
                iChart.addMarker(new DomainMarker(s));
            } break;
            
            case ROIChangeEvent.ROICHANGEDCOLOR: {
                assert (aE.getExtra() instanceof java.awt.Color);
                final int ndx = col.indexOf(aE.getObject().getName());
                if (ndx >=0)
                    iPlot.getRenderer().setSeriesPaint(ndx, ((ROI)aE.getObject()).getColor());                                 
            } break;
                        
            case ROIChangeEvent.ROICHANGEDNAME: {
                assert (aE.getExtra() instanceof String);
                final int ndx = col.indexOf((String)aE.getExtra());  
                XYSeries s = col.getSeries(ndx); 
                s.setKey(aE.getObject().getName());
            } break;    
            
            case ROIChangeEvent.ROIALLDELETED: {
                ((XYSeriesCollection)iPlot.getDataset()).removeAllSeries(); 
            } break;     
            default: 
                throw new java.lang.IllegalArgumentException();    
        }   
    }
}
