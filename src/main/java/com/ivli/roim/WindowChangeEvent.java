/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.EventObject;
/**
 *
 * @author likhachev
 */
public final class WindowChangeEvent extends EventObject {   
    private final boolean iRangeChanged;
    private final double  iMin;
    private final double  iMax;
    private final Window  iWindow; 
    
    public WindowChangeEvent(Object aO, Window aW, double aMin, double aMax, boolean aRC) {
        super(aO); 
        iWindow = aW;
        iMin = aMin;
        iMax = aMax;
        iRangeChanged = aRC;      
    }
    
    public WindowChangeEvent(Object aO, Window aW) {
        super(aO); 
        iWindow = aW;
        iMin = 0;
        iMax = 256;
        iRangeChanged = false;      
    }
    
    public final Window getWindow() {return iWindow;}   
    public final double getMin() {return iMin;}
    public final double getMax() {return iMax;}
    public final boolean isRangeChanged() {return iRangeChanged;}
    
}


interface WindowChangeNotifier {
    public void addWindowChangeListener(WindowChangeListener aL);
    public void removeWindowChangeListener(WindowChangeListener aL);    
}
