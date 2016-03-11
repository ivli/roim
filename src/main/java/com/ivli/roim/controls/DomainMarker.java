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
    
    public DomainMarker(XYSeries aSet) {
        this((Double)aSet.getY(0), aSet);
    }
    
    public DomainMarker(double aValue, XYSeries aSet) {
        super(aValue);                        
        iSet = aSet;
        int ndx = iSet.indexOf(aValue);
        setLabel(String.format("%f, %d", //NOI18N
                                   aValue, ndx));
        this.setLabelAnchor(RectangleAnchor.CENTER);
        this.setLabelOffset(RectangleInsets.ZERO_INSETS);
        this.setPaint(Color.RED);
        this.setAlpha(1.f);        
    }
    
    public void setLinkedMarker(ValueMarker aM) {
        iLink = aM;
        iLink.setValue((Double)iSet.getY((int)getValue()));
    }
    
    public void setValue(double aVal) {
        super.setValue(aVal);
        
        if (null != iSet) { 
            final int index = (int)aVal;
            
           /// if(index >= 0 && index < iSet.getItemCount()) {
             
                //if (null != iLink)
                //    iLink.setValue((Double)iSet.getY(iSet.getIndex((int)getValue())));
            
                setLabel(String.format("%f, %d", //NOI18N
                                       aVal, index));//(Double)iSet.getY((int)getValue())));
           // }
        } else {
           //super.setValue(aVal);
           setLabel(String.format("%f", aVal)); //NOI18N
        }
        
    }
}


