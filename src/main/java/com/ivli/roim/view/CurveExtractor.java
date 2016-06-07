
package com.ivli.roim.view;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Measure;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.FrameOffsetVector;
import com.ivli.roim.core.FrameOffset;
import com.ivli.roim.core.TimeSliceVector;
import java.awt.Rectangle;
/**
 *
 * @author likhachev
 */
public class CurveExtractor {    
    public static SeriesCollection extract(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        switch (anImage.getImageType()) {
            case STATIC:
            case DYNAMIC:
                return extract_dynamic(anImage, aRoi, anOff);
            case TOMO:
            case VOLUME:
            default:    
                return extract_tomo(anImage, aRoi, anOff);
        }
    }
    
    private static SeriesCollection extract_tomo(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(Measurement.DENSITY);
        Series mins    = new Series(Measurement.MINPIXEL);
        Series maxs    = new Series(Measurement.MAXPIXEL);
      
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            Shape roi = aRoi.getShape();            
             
            if (null != anOff) {
                final FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);               
            }
         
            final Measure m = measure(anImage.get(n), roi); 
            density.add(m.getIden());   
            mins.add(m.getMin());
            maxs.add(m.getMax());
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }    
   
    
    private static SeriesCollection extract_dynamic(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(Measurement.DENSITY);
        Series mins    = new Series(Measurement.MINPIXEL);
        Series maxs    = new Series(Measurement.MAXPIXEL);
        
        final TimeSliceVector tsv = anImage.getTimeSliceVector();
        final long smd = tsv.getSmallestDuration();
        
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            Shape roi = aRoi.getShape();            
             
            if (null != anOff) {
                final FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);               
            }
            
            /* 
             * normalize result to smallest frame duration
             */
            final double norm = (double)tsv.getFrameDuration(n) / (double)smd;
            
            final Measure m = measure(anImage.get(n), roi); 
            density.add(m.getIden() / norm );   
            mins.add(m.getMin() / norm);
            maxs.add(m.getMax() / norm);
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }    
   
    private static Measure measure(final ImageFrame aF, final Shape aShape) throws ArrayIndexOutOfBoundsException {          
        double min = Double.MAX_VALUE; 
        double max = Double.MIN_VALUE;
        double sum = .0;
        
        final Rectangle bnds = aShape.getBounds();
        
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (aShape.contains(i, j)) {                   
                    final int temp = aF.get(i, j);

                    if (temp > max) 
                        max = temp;
                    else if (temp < min) 
                        min = temp;
                    sum += temp;
                }

        return new Measure(min, max, sum);
    }   
}
