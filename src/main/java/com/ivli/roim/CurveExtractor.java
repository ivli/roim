
package com.ivli.roim;

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Measure;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.ImageFrame;

/**
 *
 * @author likhachev
 */
public class CurveExtractor {
    //final IMultiframeImage iImages;

    //public CurveExtractor (IMultiframeImage aI) {
    //    iImages = aI;
    //}
    
    /* */
    public static SeriesCollection extract(ROI aRoi) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(new Measurement(Measurement.DENSITY), "IntDen");
        Series mins    = new Series(new Measurement(Measurement.MINIMUM), "Mins");
        Series maxs    = new Series(new Measurement(Measurement.MAXIMUM), "Maxs");
        
        IMultiframeImage img = aRoi.getManager().getView().getModel();
        
        for (ImageFrame f : img) {                           
            Measure m = measure(f.getRaster(), aRoi.getShape()); 
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
