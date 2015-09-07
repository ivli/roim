/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;


import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.ivli.roim.*;

/**
 *
 * @author likhachev
 */
public class VOILUTPanel extends javax.swing.JPanel {
    
    private final   LUTControl iLUT;
    private boolean iShowHistogram = true;
    
    com.ivli.roim.ImageFrame iImage;
    
    ChartPanel iChart;
    
    public VOILUTPanel(LUTControl aP, ImageFrame aS) {
        initComponents();
        iLUT = new LUTControl(aP);
        
        if (null != (iImage = aS))
            jCheckBox3.setSelected(iShowHistogram);
        else             
            jCheckBox3.setEnabled(false);
                
                
        XYSeries s = new XYSeries(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.VOI_LUT"));
        XYSeriesCollection xyc = new XYSeriesCollection(aP.makeXYSeries(s));
        
        
        if(iShowHistogram && null != aS) {
            com.ivli.roim.HistogramExtractor hx = new HistogramExtractor(null);
            aS.extract(hx);
            hx.iHist.remove(0); ///correction for background        
            xyc.addSeries(hx.iHist);
        }
        
        final XYPlot plot = new XYPlot();
        plot.setDataset(xyc);
        plot.setRenderer(new StandardXYItemRenderer());
        plot.setDomainAxis(new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.IMAGE_VALUES")));
        plot.setRangeAxis(0, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.DISPLAY_VALUES")));
        if(iShowHistogram)
            plot.setRangeAxis(1, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.HISTOGRAM")));
        plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        ///NumberAxis rangeAxis = ;
        //((NumberAxis) plot.getDomainAxis()).setStandardTickUnits(NumberAxis.createIntegerTickUnits());  
        //JFreeChart jfc = new JFreeChart("VOILUTPanel.CHART_CAPTION", JFreeChart.DEFAULT_TITLE_FONT, plot, false); 
        JFreeChart jfc = new JFreeChart(plot); 
        //ChartPanel cp = new ChartPanel(createChart(aP.makeXYSeries(s)));
        
        iChart = new ChartPanel(jfc);
        //iChart.setMouseWheelEnabled(true);
                
        iChart.setSize(jPanel1.getPreferredSize());
        jPanel1.add(iChart);//, java.awt.BorderLayout.CENTER);
              
        iLUT.setSize(jPanel2.getPreferredSize());
        jPanel2.add(iLUT);
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
        jCheckBox1 = new javax.swing.JCheckBox();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 350, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle"); // NOI18N
        jCheckBox1.setText(bundle.getString("INVERTED")); // NOI18N

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText(bundle.getString("LINEAR")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText(bundle.getString("LOGARITHMIC")); // NOI18N

        jButton1.setText(bundle.getString("LUT")); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 240, 240)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );

        jCheckBox3.setText(bundle.getString("SHOWHISTOGRAM")); // NOI18N
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jCheckBox1)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBox3))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton2)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addGap(79, 79, 79)
                                .addComponent(jCheckBox3)))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        iShowHistogram = !iShowHistogram;
        
        if(iShowHistogram) {
            com.ivli.roim.HistogramExtractor hx=new com.ivli.roim.HistogramExtractor(null);
            iImage.extract(hx);
            hx.iHist.remove(0); ///correction for background        
            //xyc.addSeries(hx.iHist);
            hx.iHist.remove(0); ///correction for background        
            ((XYSeriesCollection)iChart.getChart().getXYPlot().getDataset()).addSeries(hx.iHist);
                        
            iChart.getChart().getXYPlot().setRangeAxis(1, new NumberAxis(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("VOILUTPANEL.HISTOGRAM")));
        } else {
            ((XYSeriesCollection)iChart.getChart().getXYPlot().getDataset()).removeSeries(1);
            iChart.getChart().getXYPlot().setRangeAxis(1, null);
        }
            
    }//GEN-LAST:event_jCheckBox3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables

}
