/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    ROI             iRoi;
    Extractor iExtractor;  
    
    public ROIExtractor(ROI aRoi) {        
        iRoi = aRoi;        
        iExtractor = new SimpleExtractor();        
    }
    
    public void apply(Raster aRaster) throws ArrayIndexOutOfBoundsException {  
        if (null == iRoi)
            iRoi = new ROI((Shape)aRaster.getBounds(), null, null);
        iExtractor.apply(aRaster);        
    }
    
    class SimpleExtractor implements Extractor { 
    
        @Override
        public void apply(Raster aRaster) throws ArrayIndexOutOfBoundsException {

            final Shape shape = (null != iRoi) ? iRoi.getShape() : aRaster.getBounds();
            final Rectangle bnds = shape.getBounds();

            double min = 65535, max = .0, sum = .0, pix = .0;

            double temp[] = new double [aRaster.getNumBands()];

            for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
                for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) //{ 
                    if (shape.contains(i, j)) {
                        ++pix;
                        temp = aRaster.getPixel(i, j, temp);
                        if (temp[0] > max) 
                            max = temp[0];
                        else if (temp[0] < min) 
                            min = temp[0];
                        sum += temp[0];
                    }

            //iRoi.getStats().iMin = min;
            //iRoi.getStats().iMax = max;
            //iRoi.getStats().iIden = sum;
            iRoi.getStats().iPixels = pix;
            //iRoi.getStats().iBounds = bnds.getWidth() * bnds.getHeight();          
        }   
    }

    class SubpixelExtractor extends SimpleExtractor {        
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

        @Override
        public void apply(Raster aR) throws ArrayIndexOutOfBoundsException {            
            ROI r = new ROI(iRoi);            
            Raster img = aR;
            
            if (null != iRoi.getManager()) {                
             /*   
                AffineTransform zoom = iRoi.getManager().getComponent().getZoom();           
                r.iShape = zoom.createTransformedShape(iRoi.iShape);
                iRoi = r;
                img = scale(aR, (int)Math.floor(zoom.getScaleX()));
             */
            }
            
            logger.info(img);
            super.apply(img);
        }  
    }
    
    private static final Logger logger = LogManager.getLogger(ROIExtractor.class);
}