
package com.ivli.roim.controls;

import com.ivli.roim.core.Extractor;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.Raster;

import java.util.HashMap;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
public class HistogramExtractor implements Extractor {
    private final Shape iRoi;
  
    //private final HashMap<Integer, Integer> iHist;
    
    com.ivli.roim.core.Histogram iHist;
    
    public HistogramExtractor(Shape aRoi) {
        iRoi = aRoi;
        //iHist = new HashMap<>();
       
    }
    
    @Override
    public void apply(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        extractOne(aRaster);
        //return null;
    }
    
    XYSeries toSeries(final String aSeriesName) {
        XYSeries ret = new XYSeries(aSeriesName);
        /*
        for (java.util.Map.Entry<Integer, Integer> entry : iHist.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            ret.add(key, value);
        }*/
        for (int i = iHist.iMin; i < iHist.iMax; ++i) {
            
            ret.add(i, iHist.iData[i]);
        }
        
        return ret;
    }
    
    void extractOne(Raster aRaster) throws ArrayIndexOutOfBoundsException {
        final Shape shape = (null != iRoi) ? iRoi : aRaster.getBounds();
        final Rectangle bnds = shape.getBounds();
        int []temp = new int[3];// = new int [aRaster.getNumBands()];
        
        int [] buf = new int [2 << 16];
                
        for (int i=bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j=bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (shape.contains(i, j)) {                   
                    temp = aRaster.getPixel(i, j, temp);
                    /*
                    iHist.putIfAbsent(temp[0], 0);
                    iHist.replace(temp[0], iHist.get(temp[0]).intValue() + 1);*/
                    
                    ++buf[temp[0]]  ;
                }
        iHist = new com.ivli.roim.core.Histogram(0, 2 << 16, buf);
        iHist.compact();
    }

    /*
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
    */
}