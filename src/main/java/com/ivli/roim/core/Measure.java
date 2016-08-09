package com.ivli.roim.core;

public class Measure { 
    private final int iMin;  //min value 
    private final int iMax;  //max value
    private final long iIden; //a sum of pixel values aka integral density  
    
    public Measure(int aMin, int aMax, long aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    public Measure(Measure aM) {    
        iMin = aM.iMin; 
        iMax = aM.iMax; 
        iIden = aM.iIden;
    }    
   
    public int getMin() {return iMin;}
    public int getMax() {return iMax;}
    public long getIden() {return iIden;}
}

