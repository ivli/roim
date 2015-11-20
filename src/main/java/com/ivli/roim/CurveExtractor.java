
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
       
        Series density = new Series(new Measurement(Measurement.DENSITY), "IntDen");
        Series mins    = new Series(new Measurement(Measurement.MINIMUM), "Mins");
        Series maxs    = new Series(new Measurement(Measurement.MAXIMUM), "Maxs");
        
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            Shape roi = aRoi.getShape();
            
             
            if (null != anOff) {
                FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);
               
            }
            
            ImageFrame f = anImage.get(n);
            Measure m = measure(f.getRaster(), roi); 
            density.add(m.getIden());   
            mins.add(m.getMin());
            maxs.add(m.getMax());
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }    
   
    private static Measure measure(java.awt.image.Raster aRaster, java.awt.Shape aShape) throws ArrayIndexOutOfBoundsException {          
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
