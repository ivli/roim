
package com.ivli.roim;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.Raster;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
public class HistogramExtractor implements Extractor {
    Shape iRoi;
    public final XYSeries iHist = new XYSeries(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("HISTOGRAMEXTRACTOR.IHISTOGRAM"));
    
    public HistogramExtractor(Shape aRoi){iRoi = aRoi;}
    
    @Override
    public void apply(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        extractOne(aRaster);
        //return null;
    }
    
    void extractOne(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        final Shape shape = (null != iRoi) ? iRoi : aRaster.getBounds();
        final Rectangle bnds = shape.getBounds();
        double temp[] = new double [aRaster.getNumBands()];
        
        for (int i=bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j=bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (shape.contains(i, j)) {                   
                    temp = aRaster.getPixel(i, j, temp);
                    /**/
                    final int ndx = iHist.indexOf(temp[0]);
                    if (ndx < 0) 
                        iHist.add(temp[0], 1);
                    else {                   
                        final Number val = iHist.getY(ndx);
                        iHist.update((Number)temp[0], ((Double)val)+1); 
                    } 
                }
    }

    
    void extractBinned256(Raster aRaster) throws ArrayIndexOutOfBoundsException {
    
        final Shape shape = (null != iRoi) ? iRoi : aRaster.getBounds();
        final Rectangle bnds = shape.getBounds();
        double temp [] = new double [aRaster.getNumBands()];
        
        final ROIExtractor rex = new ROIExtractor(iRoi);
        
        rex.apply(aRaster);
        
        final double step = (rex.iStats.getMax() - rex.iStats.getMin()) / 256.0;
                                
        for (int i=bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j=bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (shape.contains(i, j)) {                   
                    temp = (aRaster.getPixel(i, j, temp));
                    /**/
                    final Double binNo = Math.floor(temp[0]/step);
                    final int ndx = iHist.indexOf(binNo);
                    if (ndx < 0) 
                        iHist.add(binNo, (Double)1.0);
                    else {                   
                        final Number val = iHist.getY(ndx);
                        iHist.update(binNo, ((Double)val)+1); 
                    } 
                }
        
    }
}