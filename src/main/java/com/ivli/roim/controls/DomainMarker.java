/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.BasicStroke;
import java.awt.Color;
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
        iLink.setValue(XYSeriesUtilities.getNearestY(getValue(), iSeries));
        iLink.setLabelAnchor(RectangleAnchor.BOTTOM);
        iLink.setLabelOffset(RectangleInsets.ZERO_INSETS);
    }
    
    public ValueMarker getLinkedMarker()  {
        return iLink;
    }
    
    public void moveToMaximum() {
        setValue(XYSeriesUtilities.getDomainValueOfMaximum(iSeries));
    }
    
    public void moveToMinimum() {
        setValue(XYSeriesUtilities.getDomainValueOfMinimum(iSeries));
    }
        
    @Override
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSeries) { 
            final Double newY = XYSeriesUtilities.getNearestY(aVal, iSeries);
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
    
    
}


