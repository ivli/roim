
package com.ivli.roim;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.Serializable;

/**
 *
 * @author likhachev
 * @param <T>
 */
public class Measure implements Serializable {
    double iMin;  //min value 
    double iMax;  //max value
    double iIden; //integral density (a sum of pixel values)  
    
    Measure(double aMin, double aMax, double aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    //Measure(){}    
    
    public double getMin()  {return iMin;}
    public double getMax()  {return iMax;}
    public double getIden() {return iIden;}
    
    static Measure Measure (Raster aRaster, java.awt.Shape aShape) throws ArrayIndexOutOfBoundsException {          
            final Rectangle bnds = aShape.getBounds();
         
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE, sum = .0, pix = .0;

            double temp[] = new double [aRaster.getNumBands()];

            for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
                for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                    if (aShape.contains(i, j)) {
                        ++pix;
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
