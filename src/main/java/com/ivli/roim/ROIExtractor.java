
package com.ivli.roim;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */
class ROIExtractor implements Extractor {
    Shape     iRoi; 
    ROIStats  iStats;
    Extractor iExtractor;  
    
    public ROIExtractor(Shape aRoi) {        
        iRoi = aRoi;        
        iExtractor = new SimpleExtractor();        
    }
    
    public void apply(Raster aR) throws ArrayIndexOutOfBoundsException {
        if (null == iRoi)
            iRoi = aR.getBounds();
        iExtractor.apply(aR);        
    }
            
        
    class SimpleExtractor implements Extractor { 
           
        public void apply(Raster aRaster) throws ArrayIndexOutOfBoundsException {
            final Rectangle bnds = iRoi.getBounds();
           // ROIStats ret =  new ROIStats();
            double min = 65535, max = .0, sum = .0, pix = .0;

            double temp[] = new double [aRaster.getNumBands()];

            for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
                for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                    if (iRoi.contains(i, j)) {
                        ++pix;
                        temp = aRaster.getPixel(i, j, temp);
                        if (temp[0] > max) 
                            max = temp[0];
                        else if (temp[0] < min) 
                            min = temp[0];
                        sum += temp[0];
                    }

                      
            iStats = new ROIStats(pix, Double.NaN, min, max, sum);
        }   
    }

    class SubpixelExtractor implements Extractor {        
        Raster scale(Raster aIn, int aScale) {
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

        
        public void apply(Raster aR) throws ArrayIndexOutOfBoundsException {            
           // ROI r = new ROI(iRoi);            
            Raster img = aR;
            /* 
            if (null != iRoi.getManager()) {                
               
                AffineTransform zoom = iRoi.getManager().getComponent().getZoom();           
                r.iShape = zoom.createTransformedShape(iRoi.iShape);
                iRoi = r;
                img = scale(aR, (int)Math.floor(zoom.getScaleX()));
             
            }
            */
            logger.info(img);
            //ROIStats rs = ;
            //super.apply(img);//(iRoi.getStats() = rs);
        }  
    }
    
    private static final Logger logger = LogManager.getLogger(ROIExtractor.class);
}