
package com.ivli.roim.view;

import com.ivli.roim.core.SeriesCollection;
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
            case TOMO:
            case TOMO_G:
            case VOLUME:       
            case VOLUME_G:    
                return extract_tomo(anImage, aRoi, anOff);
            case STATIC:
            case DYNAMIC:
            default: 
                return extract_dynamic(anImage, aRoi, anOff);    
        }
    }
    
    private static SeriesCollection extract_tomo(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        //assert("not implemented yet");
        return new SeriesCollection();
    }    
   
    
    private static SeriesCollection extract_dynamic(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(Measurement.DENSITY);
        Series mins    = new Series(Measurement.MINPIXEL);
        Series maxs    = new Series(Measurement.MAXPIXEL);
        
        final TimeSliceVector tsv = anImage.getTimeSliceVector();
        final long smd = tsv.getSmallestDuration();
        Shape roi = aRoi.getShape(); 
        
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            if (null != anOff) {
                final FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);               
            }
            
            /* 
             * normalize result to smallest frame duration
             */
            final double norm = (double)tsv.getFrameDuration(n) / (double)smd;
            
            final Measure m = anImage.get(n).processor().measure(roi); 
            density.add(m.getIden() / norm );   
            mins.add(m.getMin() / norm);
            maxs.add(m.getMax() / norm);
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }       
}
