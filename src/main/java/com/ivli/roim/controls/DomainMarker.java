/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author likhachev
 */
public class DomainMarker extends ValueMarker {        
    private final XYSeries iSeries;

    private ValueMarker iLink;
    
    private static final String LABEL_FORMAT = "%.2f"; //NOI18N
    
    public enum MOVETO{
        LEFT, RIGHT, GLOBAL;    
    }
    
    public DomainMarker(XYSeries aSet) {
        this((aSet.getMaxX() - aSet.getMinX()) / 2., aSet);
    }
    
    public DomainMarker(double aV, XYSeries aS) {
        super(aV);                        
        iSeries = aS;       
        setLabel(String.format(LABEL_FORMAT, aV));
        setLabelAnchor(RectangleAnchor.CENTER);
        setLabelOffset(RectangleInsets.ZERO_INSETS);
        
        setAlpha(1.0f);     
        setPaint(Color.BLACK);        
        setStroke(new BasicStroke(1.0f));
        
        setOutlinePaint(Color.CYAN);
        setOutlineStroke(new BasicStroke(.0f));           
    }
   
    public XYSeries getXYSeries() {
        return iSeries;
    }
    
    public void setLinkedMarker(ValueMarker aM) {
        iLink = aM;
        iLink.setValue(XYSeriesUtilities.getNearestY(iSeries, getValue()));
        iLink.setLabelAnchor(RectangleAnchor.BOTTOM);
        iLink.setLabelOffset(RectangleInsets.ZERO_INSETS);
    }
    
    public ValueMarker getLinkedMarker()  {
        return iLink;
    }
    
    public void moveToMaximum(MOVETO aM) {
        double val = Double.NaN;
        switch (aM) {
            case GLOBAL: 
                val = XYSeriesUtilities.getDomainValueOfMaximum(iSeries); break;
            case LEFT:
                val = XYSeriesUtilities.getDomainValueOfMaximum(iSeries, this.getValue(), true); break;
            case RIGHT:
                val = XYSeriesUtilities.getDomainValueOfMaximum(iSeries, this.getValue(), false); break;
        } 
        
        if (Double.isFinite(val))
            setValue(val);
        else
           LOG.debug("!!!Domain value not found");
    }
    
    public void moveToMinimum(MOVETO aM) {
        //setValue(XYSeriesUtilities.getDomainValueOfMinimum(iSeries));
        double val = Double.NaN;
        switch (aM) {
            case GLOBAL: 
                val = XYSeriesUtilities.getDomainValueOfMinimum(iSeries); break;
            case LEFT:
                val = XYSeriesUtilities.getDomainValueOfMinimum(iSeries, this.getValue(), true); break;
            case RIGHT:
                val = XYSeriesUtilities.getDomainValueOfMinimum(iSeries, this.getValue(), false); break;
        }
        
        if (Double.isFinite(val))
            setValue(val);
        else
           LOG.info("!!!Domain value not found");
    }
    
    
    
    public void moveToMedian(MOVETO aM) {
        double medY = (iSeries.getMaxY() - iSeries.getMinY()) / 2.;
        double val = XYSeriesUtilities.getNearestX(iSeries, medY);
        
        if (Double.isFinite(val))
            setValue(val);
        else
           LOG.info("!!!Domain value not found");
    }
    
        
    @Override
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSeries) { 
            final Double newY = XYSeriesUtilities.getNearestY(iSeries, aVal);
            if (null != iLink) {
                iLink.setValue(newY);
                iLink.setLabel(String.format(LABEL_FORMAT, newY));
            }
            setLabel(String.format(LABEL_FORMAT, aVal));    
        } else {
           //super.setValue(aVal);
           setLabel(String.format("%f", aVal)); //NOI18N
        }        
    }  
    
    private static final Logger LOG = LogManager.getLogger();
}


