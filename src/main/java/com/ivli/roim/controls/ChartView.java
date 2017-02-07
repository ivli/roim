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
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.ivli.roim.core.Measurement;
import com.ivli.roim.view.ROI;
import com.ivli.roim.events.OverlayChangeEvent;
import com.ivli.roim.events.OverlayChangeListener;
import com.ivli.roim.view.OverlayManager;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ISeries;

/*
  * @author likhachev
  */
public class ChartView extends JPanel implements OverlayChangeListener {                   
    private XYPlot     iPlot;
    private JFreeChart iJfc;    
    private ChartControl iChart;  
      
   // private final static int DEFAULT_DATASET = 1;
    
    public static ChartView create() {
        ChartView ret = new ChartView();
        ret.initChart();        
        return ret;
    }
              
    protected void initChart () {
        if (null != iPlot) {
            ((XYSeriesCollection)iPlot.getDataset()).removeAllSeries();
        } else {
            iPlot = new XYPlot();           
            iPlot.setRenderer(new StandardXYItemRenderer());
            iPlot.setDomainAxis(new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_CHART.TIME_SERIES_VALUES")));
            iPlot.setRangeAxis(0, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_CHART.ROI_INTDEN_VALUES")));           
            iPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            iPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

            iJfc   = new JFreeChart(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI_CHART.CHART_TITLE"), iPlot); 
                      
            iChart = new ChartControl(iJfc);           
           
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
    public void OverlayChanged (OverlayChangeEvent anEvt) {     
        LOG.debug(anEvt);
        
        if (anEvt.getObject() instanceof ROI) {
            XYSeriesCollection col = ((XYSeriesCollection)iPlot.getDataset());  
            final OverlayManager mgr = (OverlayManager)anEvt.getSource();
            
            switch (anEvt.getCode()) {
                case CREATED: {                                     
                    assert (0 > col.indexOf(anEvt.getObject().getName()));
                    
                    final XYSeries s = new XYSeries(anEvt.getObject().getName(), true, false);
                    final ISeries c = ((ROI)anEvt.getObject()).getSeries(Measurement.DENSITY);

                    IMultiframeImage img = mgr.getImage();
                    
                    assert(c.size() == img.getTimeSliceVector().getNumFrames());

                    for (int n = 0; n < c.size(); ++n)                                       
                        s.add(img.getTimeSliceVector().getSlices().get(n) / 1000., c.get(n));
                  
                    ((XYSeriesCollection)iPlot.getDataset()).addSeries(s); 
                    iPlot.getRenderer().setSeriesPaint(col.indexOf(anEvt.getObject().getName()), ((ROI)anEvt.getObject()).getColor());                     
                } break;
                
                case DELETED: {                
                    final int ndx = col.indexOf(anEvt.getObject().getName());
                    col.removeSeries(ndx);                
                } break;    
                
                case MOVED: {//fall-through                                                                      
                    final int ndx = col.indexOf(anEvt.getObject().getName());    
                    ISeries c = ((ROI)anEvt.getObject()).getSeries(Measurement.DENSITY);
                    XYSeries s = col.getSeries(ndx); 
                    s.setNotify(false);
                    s.clear();              
                   
                    for (int n = 0; n < c.size(); ++n) {
                        long dur = mgr.getImage().getTimeSliceVector().getSlices().get(n) / 1000;
                        s.add(dur, c.get(n));
                    }    
                    s.setNotify(true);
                    //s.fireSeriesChanged();
                    
                } break;
                
                case COLOR_CHANGED: {
                    assert (anEvt.getExtra() instanceof java.awt.Color);
                    final int ndx = col.indexOf(anEvt.getObject().getName());
                    iPlot.getRenderer().setSeriesPaint(ndx, ((ROI)anEvt.getObject()).getColor());                                 
                } break;

                case NAME_CHANGED: {
                    assert (anEvt.getExtra() instanceof String);
                    LOG.debug("ROI" + (String)anEvt.getExtra() + "name changed to " + anEvt.getObject().getName());
                    
                    final int ndx = col.indexOf(((String)anEvt.getExtra())); 
                    
                    col.getSeries(ndx).setKey(anEvt.getObject().getName());
                    /* this is a work-around the case when two or more ROI have the same names (user mistakenly renamed) 
                     * when this mistake is corrected it might happen the colors of curves does not match colors of corresponding ROI 
                     */                     
                    iPlot.getRenderer().setSeriesPaint(ndx, ((ROI)anEvt.getObject()).getColor());
                    
                } break;    

                default: 
                    ///throw new java.lang.IllegalArgumentException();    
                    break;
            }  
        }
    }
    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
