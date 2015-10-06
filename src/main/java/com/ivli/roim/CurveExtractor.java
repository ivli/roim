
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class CurveExtractor {
    final IMultiframeImage iImages;

    public CurveExtractor (IMultiframeImage aI) {
        iImages = aI;
    }
    
    /* */
    public SeriesCollection extract(ROI aRoi) {
        SeriesCollection c = new SeriesCollection();
       // c.addSeries(new Series(new Measurement(Measurement.DENSITY), "IntDen"));
        Series density = new Series(new Measurement(Measurement.DENSITY), "IntDen");
        
        for (ImageFrame f : iImages) {                           
            Measure m = measure(f.getRaster(), aRoi.getShape()); 
            density.add(m.getIden());            
        } 
        
        c.addSeries(density);
        
        return c;
    }    
   
    Measure measure(java.awt.image.Raster aRaster, java.awt.Shape aShape) throws ArrayIndexOutOfBoundsException {          
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
