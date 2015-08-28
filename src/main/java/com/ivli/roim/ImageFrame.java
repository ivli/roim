
package com.ivli.roim;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable, IImage /**/{
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
    
    public ImageFrame image() {
        return this;
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
        
       
       // return new BufferedImage(cm, raster , false, null);
    }
    
    static ColorModel createColorModel(int bits, int dataType) {
        return new ComponentColorModel(
                        ColorSpace.getInstance(ColorSpace.CS_GRAY),
                        new int[] { bits },
                        false, // hasAlpha
                        false, // isAlphaPremultiplied
                        Transparency.OPAQUE,
                        dataType);
    }
     
    public void extract(Extractor aEx) {
        aEx.apply(iRaster);
    }
}
