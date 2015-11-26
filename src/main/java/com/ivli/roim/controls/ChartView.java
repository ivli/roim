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
import com.ivli.roim.core.Series;
import com.ivli.roim.events.ROIChangeEvent;
import com.ivli.roim.events.ROIChangeListener;
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

/**
 *
 * @author likhachev
 */
public class ChartView extends javax.swing.JPanel 
                           implements ROIChangeListener { 
                        
    private XYPlot     iPlot;
    private JFreeChart iJfc;    
    private ChartPanel iChart;  
    
    
    public void initChart () {
        if (null != iPlot) {
            ((XYSeriesCollection)iPlot.getDataset()).removeAllSeries();
        } else {
            iPlot = new XYPlot();
            //plot.setDataset(xyc);
            iPlot.setRenderer(new StandardXYItemRenderer());
            iPlot.setDomainAxis(new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_CHART.TIME_SERIES_VALUES")));
            iPlot.setRangeAxis(0, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("ROI_CHART.ROI_INTDEN_VALUES")));
           // if(iShowHistogram)
           //     plot.setRangeAxis(1, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.HISTOGRAM")));
            iPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            iPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

            iJfc = new JFreeChart(iPlot); 

            iChart = new ChartPanel(iJfc);
            //iChart.setMouseWheelEnabled(true);

            XYSeriesCollection ds = new XYSeriesCollection();
            iPlot.setDataset(ds);

            iChart.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            iChart.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
            setLayout(new BorderLayout()); 
            
            this.add(iChart);
            
            //jPanel3.add(iChart);//, java.awt.BorderLayout.CENTER);              
        }
    }
    
    public void ROIChanged (ROIChangeEvent aE) {

        XYSeriesCollection col = ((XYSeriesCollection)iPlot.getDataset());
        
        switch (aE.getChange()) {
            case Cleared: {
                
                int ndx = col.indexOf(aE.getROI().getName());
                col.removeSeries(ndx); 
               
              } break;
    
            case Changed: {
                int ndx = col.indexOf(aE.getROI().getName());
               
                Series c = aE.getROI().getSeries(Measurement.DENSITY);
                XYSeries s = col.getSeries(ndx); 
                s.clear();
                
                for (int n = 0; n < c.getNumFrames(); ++n) {
                    long dur = aE.getROI().getManager().getImage().getTimeSliceVector().getSlices().get(n) / 1000;
                    s.add(dur, c.get(n));
                }
               } break;
                
            case Created: 
                XYSeries s = new XYSeries(aE.getROI().getName());
                Series c = aE.getROI().getSeries(Measurement.DENSITY);
                
                int x = 0;
                                
                ///java.util.Iterator<Long> tsv = iPanel.iProvider.getTimeSliceVector().iSlices.iterator();
                  //sanity check
                assert(c.getNumFrames() == aE.getROI().getManager().getImage().getTimeSliceVector().getNumFrames());
                
                for (int n = 0; n < c.getNumFrames(); ++n) {
                    long dur = aE.getROI().getManager().getImage().getTimeSliceVector().getSlices().get(n) / 1000;
                    s.add(dur, c.get(n));
                }

                ((XYSeriesCollection)iPlot.getDataset()).addSeries(s);   
                iPlot.getRenderer().setSeriesPaint(col.indexOf(aE.getROI().getName()), aE.getROI().getColor());              
                break;
            case ChangedColor:
                iPlot.getRenderer().setSeriesPaint(col.indexOf(aE.getROI().getName()), aE.getROI().getColor());                                 
                break;
            case Emptied: 
                ((XYSeriesCollection)iPlot.getDataset()).removeAllSeries(); break;
            default: throw new java.lang.IllegalArgumentException();    
        }
   
    }
}
