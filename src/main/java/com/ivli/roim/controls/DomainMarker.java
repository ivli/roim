/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.Color;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author likhachev
 */
public class DomainMarker extends ValueMarker {        
    private final XYSeries iSet;

    private ValueMarker iLink;
    
    private static final String LABEL_FORMAT = "%.2f4 %.2f"; //NOI18N
    
    public DomainMarker(XYSeries aSet) {
        this((aSet.getMaxX() - aSet.getMinX()) / 2., aSet);
    }
    
    public DomainMarker(double aValue, XYSeries aSet) {
        super(aValue);                        
        iSet = aSet;
       
            setLabel(String.format(LABEL_FORMAT, 
                                       aValue, getNearestY(aValue)));
        this.setLabelAnchor(RectangleAnchor.CENTER);
        this.setLabelOffset(RectangleInsets.ZERO_INSETS);
        this.setPaint(Color.RED);
        this.setAlpha(1.f);        
    }
    
    public void setLinkedMarker(ValueMarker aM) {
        iLink = aM;
        iLink.setValue(getNearestY(getValue()));
    }
    
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSet) { 
            final Double newY = getNearestY(aVal);
            if (null != iLink)
                iLink.setValue(newY);
            
            setLabel(String.format(LABEL_FORMAT, aVal, newY));    
        } else {
           //super.setValue(aVal);
           setLabel(String.format("%f", aVal)); //NOI18N
        }
        
    }
    
    Double getNearestY(Double aX) {
        for (int i = 0; i < iSet.getItemCount() - 1; ++i) {
            Double i1 = (Double)iSet.getX(i);
            Double i2 = (Double)iSet.getX(i+1);
            if (aX >=i1 && aX < i2) {
                return (Double)((Double)iSet.getY(i) + (Double)iSet.getY(i+1))/2.;
            }
        }
        return Double.NaN;
        
        
    }
}


