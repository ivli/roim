
package com.ivli.roim;



public class Measure implements java.io.Serializable {
    private static final long serialVersionUID = 42L;
          
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
    
    static Measure New (java.awt.image.Raster aRaster, java.awt.Shape aShape) throws ArrayIndexOutOfBoundsException {          
            final java.awt.Rectangle bnds = aShape.getBounds();
         
            double min = Double.MAX_VALUE; 
            double max = Double.MIN_VALUE;
            double sum = .0;//, pix = .0;
            
            double temp[] = new double [aRaster.getNumBands()];

            for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
                for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                    if (aShape.contains(i, j)) {
                       /// ++pix;
                        temp = aRaster.getPixel(i, j, temp);
                        
                        if (temp[0] > max) 
                            max = temp[0];
                        else if (temp[0] < min) 
                            min = temp[0];
                        sum += temp[0];
                    }
                      
            return new Measure(min, max, sum);
        }   
    }
