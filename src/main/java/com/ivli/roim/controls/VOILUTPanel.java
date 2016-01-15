/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;



import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;

import com.ivli.roim.core.*;
import com.ivli.roim.ImageView;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class VOILUTPanel extends JPanel implements WindowChangeListener {    
    private final LUTControl iLUT;
    private boolean iShowHistogram = true;
    private HistogramExtractor iHEx = null;
    private final ImageView iView;    
    private final ChartPanel iPanel;
    
        
    public void windowChanged(WindowChangeEvent anEvt) {
        XYSeriesCollection voiCurve = new XYSeriesCollection(iLUT.makeXYSeries(new XYSeries(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("VOILUTPANEL.VOI_LUT"))));    
        iPanel.getChart().getXYPlot().setDataset(0, voiCurve);
    }   
    
    public VOILUTPanel(LUTControl aP, ImageView aV) {
        initComponents();
        
        iLUT = new LUTControl(aP);
                        
        if (null != (iView = aV)) {
            jCheckBox2.setSelected(iShowHistogram);       
        } else             
            jCheckBox2.setEnabled(iShowHistogram = false);
       
              
        XYSeriesCollection voiCurve = new XYSeriesCollection(iLUT.makeXYSeries(new XYSeries(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("VOILUTPANEL.VOI_LUT"))));
  
        XYPlot plot = new XYPlot();
   
        plot.setDataset(0, voiCurve);
        plot.setRenderer(0, new XYSplineRenderer());     
        ((XYSplineRenderer)plot.getRenderer()).setShapesVisible(false);
        plot.setRangeAxis(0, new NumberAxis("VOILUTPANEL.AXIS_LABEL_VOI_CURVE"));                
        
        if(iShowHistogram) {    
            if (null == iHEx) {
                iHEx = new HistogramExtractor(null);
                aV.getImage().extract(iHEx);
            }
             
            XYSeriesCollection col2 = new XYSeriesCollection();
            col2.addSeries(iHEx.toSeries(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("VOILUTPANEL.HISTOGRAM")));
            
            plot.setDataset(1, col2);
            plot.setRenderer(1, new XYBarRenderer());
            plot.setRangeAxis(1, new NumberAxis("VOILUTPANEL.AXIS_LABEL_IMAGE_HISTOGRAM"));
            plot.mapDatasetToRangeAxis(1, 1);
        }
    
        plot.setDomainAxis(new NumberAxis("VOILUTPANEL.AXIS_LABEL_IMAGE_SPACE"));                
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);
        // change the rendering order so the primary dataset appears "behind" the 
        // other datasets...
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        
        JFreeChart jfc = new JFreeChart(plot); 
                
        iPanel = new ChartPanel(jfc);
        //iChart.setMouseWheelEnabled(true);
                
        iPanel.setSize(jPanel1.getPreferredSize());
        jPanel1.add(iPanel);//, java.awt.BorderLayout.CENTER);
              
        iLUT.setSize(jPanel2.getPreferredSize());
        jPanel2.add(iLUT);
                
        //aP.addWindowChangeListener(iLUT);
        iLUT.addWindowChangeListener(this);
        
        super.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {}

            public void ancestorRemoved(AncestorEvent event){        
                iLUT.removeWindowChangeListener(VOILUTPanel.this);            
            }

            public void ancestorMoved(AncestorEvent event){}         
            });
        
        
              
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
        jCheckBox2 = new javax.swing.JCheckBox();

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
            .addGap(0, 264, Short.MAX_VALUE)
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
            .addGap(0, 253, Short.MAX_VALUE)
        );

        jCheckBox2.setText("<LUT_PANEL.SHOWHISTOGRAM>");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jCheckBox2)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        iShowHistogram = !iShowHistogram;
    }//GEN-LAST:event_jCheckBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    private final static Logger logger = LogManager.getLogger(VOILUTPanel.class);
}
