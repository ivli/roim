
package com.ivli.roim.controls;

import com.ivli.roim.core.Extractor;
import com.ivli.roim.core.Histogram;

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
    
    Histogram iHist;
    
    public HistogramExtractor(Shape aRoi) {
        iRoi = aRoi;
        iHist = new Histogram();       
    }
    
    @Override
    public void apply(com.ivli.roim.core.ImageFrame aR) throws ArrayIndexOutOfBoundsException {
        extractOne(aR);
        //extractBinned256(aR);
    }
    /*
    XYSeries toSeries(final String aSeriesName) {
        XYSeries ret = new XYSeries(aSeriesName);
              
        for (java.util.Map.Entry<Integer, Integer> entry : iHist.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            ret.add(key, value);
        }
        return ret;
    }
    */
    
    private int getDataTypeMaxValue(com.ivli.roim.core.ImageFrame aR) {
       return 32;//2 << aR.getDataBuffer().getDataTypeSize(aR.getDataBuffer().getDataType()); 
    }
    
    void extractOne(com.ivli.roim.core.ImageFrame aR) throws ArrayIndexOutOfBoundsException {
        final Shape shape = (null != iRoi) ? iRoi : new Rectangle(0,0, aR.getWidth(), aR.getHeight());
        final Rectangle bnds = shape.getBounds();
        ///int []temp = new int[3];// = new int [aRaster.getNumBands()];
        
        int [] buf = new int [2 << 16];
                
        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j=bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (shape.contains(i, j)) {                   
                    int temp = aR.getPixel(i, j);    
                    //final Integer key = temp[0];
                    final Integer val = iHist.get(temp);
                    iHist.put(temp, null != val ? val + 1 : 1);                      
                }
     
    }

    
    void extractBinned256(Raster aR) throws ArrayIndexOutOfBoundsException {
    /*
        final Shape shape = (null != iRoi) ? iRoi : aR.getBounds();
        final Rectangle bnds = shape.getBounds();
        int [] temp = new int [aR.getNumBands()];
                        
        final com.ivli.roim.ROIExtractor rex = new com.ivli.roim.ROIExtractor(iRoi);
        
        rex.apply(aR);
         
        iHist = new com.ivli.roim.core.Histogram(0, getDataTypeMaxValue(aR), new int[256]);
        
        final int step = (int)Math.ceil((rex.iStats.getMax() - rex.iStats.getMin()) / 256.0);
                                
        for (int i=bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j=bnds.y; j < (bnds.y + bnds.height); ++j) 
                if (shape.contains(i, j)) {                   
                    temp = aR.getPixel(i, j, temp);
                    final int index = temp[0]/step;
                    if (index <0 || index >= iHist.iData.length)
                        System.out.format("bad argument: %d", index);
                    else
                        ++iHist.iData[index];                    
                }
     */   
    }
    
}