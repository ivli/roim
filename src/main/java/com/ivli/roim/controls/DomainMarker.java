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
    
    private static final String LABEL_FORMAT = "%.2f"; //NOI18N
    
    public DomainMarker(XYSeries aSet) {
        this((aSet.getMaxX() - aSet.getMinX()) / 2., aSet);
    }
    
    public DomainMarker(double aValue, XYSeries aSet) {
        super(aValue);                        
        iSet = aSet;       
        setLabel(String.format(LABEL_FORMAT, aValue));
        setLabelAnchor(RectangleAnchor.CENTER);
        setLabelOffset(RectangleInsets.ZERO_INSETS);
        setPaint(Color.RED);
        setAlpha(1.f);        
    }
    
    public void setLinkedMarker(ValueMarker aM) {
        iLink = aM;
        iLink.setValue(getNearestY(getValue()));
        iLink.setLabelAnchor(RectangleAnchor.BOTTOM);
        iLink.setLabelOffset(RectangleInsets.ZERO_INSETS);
    }
    
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSet) { 
            final Double newY = getNearestY(aVal);
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
    
    double getNearestY(Double aX) {        
        int i = iSet.getItemCount();
        
        do{ // bisection
            i = i/2;
        } while(i > 0 && (Double)iSet.getX(i) > aX);
        
        for (; i < iSet.getItemCount() - 1; ++i) { 
            Double i1 = (Double)iSet.getX(i);
            Double i2 = (Double)iSet.getX(i+1);
            if (aX >=i1 && aX < i2) { //linear fit
                final double x0 = (double)iSet.getX(i);
                final double y0 = (double)iSet.getY(i);                    
                return y0 + (aX - x0) * ((double)iSet.getY(i+1) - y0) / ((double)iSet.getX(i+1) - x0);
            }
        }
        return Double.NaN;
    }
}


