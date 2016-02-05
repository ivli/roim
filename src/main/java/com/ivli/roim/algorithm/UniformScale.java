
package com.ivli.roim.algorithm;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public class UniformScale {
    static Raster scale(Raster aIn, int aScale) {
            WritableRaster ret = aIn.createCompatibleWritableRaster(aIn.getWidth() * aScale, aIn.getHeight() * aScale);

            double temp[] = new double [aIn.getNumBands()];

            for (int i = 0; i < aIn.getWidth(); ++i)
                for (int j = 0; j < aIn.getHeight(); ++j) {
                    aIn.getPixel(i, j, temp);  
                     //despite it does only make sense for BW images lets use all channels
                    for (double D : temp)
                        D /=(aScale * aScale);


                    for (int m = 0; m < aScale; ++m)
                        for (int n = 0; n < aScale; ++n)
                            ret.setPixel(i * aScale + m, j * aScale + n, temp);
                }
             //TODO: add smoothing filter here

            return ret;
        }
}
