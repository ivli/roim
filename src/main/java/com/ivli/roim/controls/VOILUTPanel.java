/*
 * Copyright (C) 2016 likhachev
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

import com.ivli.roim.core.Curve;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.core.Histogram;
import com.ivli.roim.view.ImageView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class VOILUTPanel extends JPanel implements WindowChangeListener {        
    private static final int NO_OF_BINS = 256;
    private final LUTControl iLUT;             
    private final ChartPanel iPanel;
    private final String iCurveName;    
   
    private XYSeriesCollection makeLUTCurve() {                    
        double min = iLUT.getView().getMin();
        double max = iLUT.getView().getMax();
       
        Curve c = iLUT.getView().getWindowCurve();
        
        return new XYSeriesCollection(XYSeriesUtilities.getSeriesRebinned(iCurveName, c, NO_OF_BINS, min, max));
    }
        
    private XYSeries makeHistogram() {  
        Histogram hist = new Histogram(NO_OF_BINS);
        if (iLUT.getView() instanceof ImageView) {
            hist = ((ImageView)iLUT.getView()).getFrame().processor().histogram(null, NO_OF_BINS);
        }
        
        final String name = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.HISTOGRAM");
        return XYSeriesUtilities.convert(name, hist);
    }
    
    @Override
    public void windowChanged(WindowChangeEvent anEvt) {            
        iPanel.getChart().getXYPlot().setDataset(0, makeLUTCurve());
        iLUT.windowChanged(anEvt);
    }   
    
    public VOILUTPanel(LUTControl aP) {        
        
        iCurveName = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.VOI_LUT");        
        
        iLUT = LUTControl.create(aP);        
        
        aP.addWindowChangeListener(this);
        
        initComponents();
               
        XYPlot plot = new XYPlot();
   
        plot.setDataset(0, makeLUTCurve());
        plot.setRenderer(0, new XYSplineRenderer());  
        
        ((XYSplineRenderer)plot.getRenderer()).setShapesVisible(false);
        plot.setRangeAxis(0, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.AXIS_LABEL_VOI_CURVE")));                
                 
        XYSeriesCollection col2 = new XYSeriesCollection();
        col2.addSeries(makeHistogram());

        plot.setDataset(1, col2);
        plot.setRenderer(1, new XYBarRenderer());
        plot.setRangeAxis(1, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.AXIS_LABEL_IMAGE_SPACE")));
        plot.mapDatasetToRangeAxis(1, 1);      
        
        plot.setDomainAxis(new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.AXIS_LABEL_IMAGE_HISTOGRAM")));                
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        // change the rendering order so the primary dataset appears "behind" the 
        // other datasets...
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        
        JFreeChart jfc = new JFreeChart(plot); 
        
        jfc.setBorderVisible(true);
        jfc.removeLegend();
        iPanel = new ChartPanel(jfc);            
        iPanel.setSize(jPanel1.getPreferredSize());
        jPanel1.add(iPanel);//, java.awt.BorderLayout.CENTER);              
        iLUT.setSize(jPanel2.getPreferredSize());
        jPanel2.add(iLUT);       
        iLabelMin.setText(String.format("%.0f", iLUT.getView().getMin()));
        iLabelMax.setText(String.format("%.0f", iLUT.getView().getMax()));
               
        validate();  
    }
   
          
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        iLabelMin = new javax.swing.JLabel();
        iLabelMax = new javax.swing.JLabel();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240)));
        jPanel2.setPreferredSize(new java.awt.Dimension(32, 255));
        jPanel2.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("VOILUTPANEL.LABEL_MIN")); // NOI18N

        jLabel2.setText(bundle.getString("VOILUTPANEL.LABEL_MAX")); // NOI18N

        iLabelMin.setText("jLabel3");

        iLabelMax.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(iLabelMin)
                            .addComponent(iLabelMax))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(iLabelMin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(iLabelMax))
                .addContainerGap(43, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel iLabelMax;
    private javax.swing.JLabel iLabelMin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    private final static Logger LOG = LogManager.getLogger();
}
