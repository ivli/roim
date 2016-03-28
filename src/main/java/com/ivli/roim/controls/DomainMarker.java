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
        iLink.setValue(getNearestY(getValue()));
        iLink.setLabelAnchor(RectangleAnchor.BOTTOM);
        iLink.setLabelOffset(RectangleInsets.ZERO_INSETS);
    }
    
    public void moveToMaximum() {
        setValue(getDomainValueOfMaximum(iSeries));
    }
    
    public void moveToMinimum() {
        setValue(getDomainValueOfMinimum(iSeries));
    }
        
    @Override
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSeries) { 
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
    
    
    double getDomainValueOfMinimum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MAX_VALUE;
        double valX = Double.NaN;
        
        for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] < valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    double getDomainValueOfMaximum(XYSeries aS) {
        final double [][] v = aS.toArray();
        double valY = Double.MIN_VALUE;
        double valX = Double.NaN;
        
       for (int i=0; i < v[0].length; ++i) {
            if (v[1][i] > valY) {
                valY = v[1][i];
                valX = v[0][i];
            }            
        }
  
        return valX;
    }
    
    double getNearestY(Double aX) {        
        int i = iSeries.getItemCount();
        
        do{ // bisection
            i = i/2;
        } while(i > 0 && (Double)iSeries.getX(i) > aX);
        
        for (; i < iSeries.getItemCount() - 1; ++i) { 
            Double i1 = (Double)iSeries.getX(i);
            Double i2 = (Double)iSeries.getX(i+1);
            if (aX >=i1 && aX < i2) { //linear fit
                final double x0 = (double)iSeries.getX(i);
                final double y0 = (double)iSeries.getY(i);                    
                return y0 + (aX - x0) * ((double)iSeries.getY(i+1) - y0) / ((double)iSeries.getX(i+1) - x0);
            }
        }
        return Double.NaN;
    }
}

