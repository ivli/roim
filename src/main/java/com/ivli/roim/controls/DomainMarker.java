/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author likhachev
 */
class DomainMarker extends ValueMarker implements SeriesChangeListener {        
    private final XYSeries iSeries;

    private ValueMarker iLink;
    
    private static final String LABEL_FORMAT = "%.2f"; //NOI18N
       
    public DomainMarker(XYSeries aSet) {
        this((aSet.getMaxX() - aSet.getMinX()) / 2., aSet);
    }
    
    public DomainMarker(double aV, XYSeries aS) {
        super(aV);                        
        iSeries = aS;       
        iSeries.addChangeListener(this);
        setLabel(String.format(LABEL_FORMAT, aV));
        setLabelAnchor(RectangleAnchor.CENTER);
        setLabelOffset(RectangleInsets.ZERO_INSETS);
        
        setAlpha(1.0f);     
        setPaint(Color.BLACK);        
        setStroke(new BasicStroke(1.0f));
        
        setOutlinePaint(Color.CYAN);
        setOutlineStroke(new BasicStroke(.0f));           
    }
   
    public XYSeries getSeries() {
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
        
    @Override
    public void seriesChanged(SeriesChangeEvent sce) {        
        setValue(getValue());
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
           setLabel(String.format(LABEL_FORMAT, aVal)); //NOI18N
        }        
    }  
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}


