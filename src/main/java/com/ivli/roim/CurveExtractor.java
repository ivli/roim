
package com.ivli.roim;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Measure;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.FrameOffsetVector;
import com.ivli.roim.core.FrameOffset;
/**
 *
 * @author likhachev
 */
public class CurveExtractor {
    
    public static SeriesCollection extract(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(Measurement.DENSITY);
        Series mins    = new Series(Measurement.MINPIXEL);
        Series maxs    = new Series(Measurement.MAXPIXEL);
        
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            Shape roi = aRoi.getShape();            
             
            if (null != anOff) {
                FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);
               
            }
            
            ImageFrame f = anImage.get(n);
            Measure m = measure(f, roi); 
            density.add(m.getIden());   
            mins.add(m.getMin());
            maxs.add(m.getMax());
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }    
   
    private static Measure measure(ImageFrame aF, java.awt.Shape aShape) throws ArrayIndexOutOfBoundsException {          
        final java.awt.Rectangle bnds = aShape.getBounds();

        double min = Double.MAX_VALUE; 
        double max = Double.MIN_VALUE;
        double sum = .0;//, pix = .0;

       // double temp[] = new double [aRaster.getNumBands()];

        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                if (aShape.contains(i, j)) {
                   /// ++pix;
                    final int temp = aF.getPixel(i, j);

                    if (temp > max) 
                        max = temp;
                    else if (temp < min) 
                        min = temp;
                    sum += temp;
                }

        return new Measure(min, max, sum);
        }   
}
