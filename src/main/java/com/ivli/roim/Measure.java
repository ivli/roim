
package com.ivli.roim;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.io.Serializable;

/**
 *
 * @author likhachev
 * @param <T>
 */
public class Measure <T extends Number> implements Serializable {
    T iMin;  //min value 
    T iMax;  //max value
    T iIden; //integral density (a sum of pixel values)  
    
    Measure(T aMin, T aMax, T aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    Measure(){}    
    
    public T getMin()  {return iMin;}
    public T getMax()  {return iMax;}
    public T getIden() {return iIden;}
    
    static Measure Measure (Raster aRaster, ROI aRoi) throws ArrayIndexOutOfBoundsException {          
            final Rectangle bnds = aRoi.getShape().getBounds();
         
            double min = 65535, max = .0, sum = .0, pix = .0;

            double temp[] = new double [aRaster.getNumBands()];

            for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
                for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                    if (aRoi.getShape().contains(i, j)) {
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
