
package com.ivli.roim;



public class Measure implements java.io.Serializable {
    private static final long serialVersionUID = 42L;
          
    public static final int MEASUREMENT_ITEM_MIN = 1;
    public static final int MEASUREMENT_ITEM_MAX = 2;
    public static final int MEASUREMENT_ITEM_INTDEN = 4;
        
    private final double iMin;  //min value 
    private final double iMax;  //max value
    private final double iIden; //a sum of pixel values aka integral density  
    
    Measure(double aMin, double aMax, double aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    Measure(Measure aM) {    
        iMin  = aM.iMin; 
        iMax  = aM.iMax; 
        iIden = aM.iIden;
    }    
    
    Measure() {
        this(Double.NaN, Double.NaN, Double.NaN);
    }
    
    public double getMin()  {return iMin;}
    public double getMax()  {return iMax;}
    public double getIden() {return iIden;}
    
    
    }
