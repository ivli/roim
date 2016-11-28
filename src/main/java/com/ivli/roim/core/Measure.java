package com.ivli.roim.core;

public class Measure { 
    private int iMin;  //min value 
    private int iMax;  //max value
    private long iIden; //a sum of pixel values aka integral density  
    
    public Measure(int aMin, int aMax, long aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    public Measure() {
        iMin  = 0; 
        iMax  = 0; 
        iIden = 0;
    }
    
    public Measure(Measure aM) {    
        iMin = aM.iMin; 
        iMax = aM.iMax; 
        iIden = aM.iIden;
    }    
       
    public int  getMin() {return iMin;}
    public int  getMax() {return iMax;}
    public long getIden() {return iIden;}    
    public void setMin(int aV) {iMin = aV;}
    public void setMax(int aV) {iMax = aV;}
    public void setIden(long aV) {iIden = aV;}
    
    public void combine(final Measure aM) {
        iMax = Math.max(aM.iMax, iMax);
        iMin = Math.min(aM.iMin, iMax);
        iIden += aM.iIden;    
    }
    
    @Override
    public String toString() {
        return String.format("min=%d, max=%d, iden=%d", getMin(), getMax(), getIden());    
    }
}

