
package com.ivli.roim;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable {
    Raster   iRaster;  
    ROIStats iStats;   
    
    public Raster getRaster() {
        return iRaster;
    }
    
    public ROIStats getStats() {
        return iStats;
    }
    
    public int getWidth() {
        return iRaster.getWidth();
    }
    
    public int getHeight() {
        return iRaster.getHeight();
    }
           
    ImageFrame(Raster aRaster) {
        iRaster = aRaster;
        ROIExtractor ex = new ROIExtractor(iRaster.getBounds());
        ex.apply(aRaster);
        iStats = ex.iStats;
    }
    
    public BufferedImage getBufferedImage() {
        return convert((WritableRaster)iRaster);
    }     
    
    private BufferedImage convert(WritableRaster wr) {
        return new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY)                                                               
                                                        , new int[] {8}
                                                        , false		// has alpha
                                                        , false		// alpha premultipled
                                                        , Transparency.OPAQUE
                                                        , wr.getDataBuffer().getDataType())                                                                                                                                  
                                                    , wr, true, null);
                  
    }  
   
    public void extract(Extractor aEx) {
        aEx.apply(iRaster);
    }
}
