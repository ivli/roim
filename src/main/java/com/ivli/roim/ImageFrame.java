/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
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
    
    private BufferedImage convert(WritableRaster raster) {
        ColorModel cm;
       // if (pmi.isMonochrome()) {
           
            cm = createColorModel(8, DataBuffer.TYPE_USHORT);//TYPE_BYTE);
          //  SampleModel sm = createSampleModel(DataBuffer.TYPE_BYTE, false);
          //  raster = applyLUTs(raster, frameIndex, param, sm, 8);
          //  for (int i = 0; i < overlayGroupOffsets.length; i++) {
          //      applyOverlay(overlayGroupOffsets[i], 
          //              raster, frameIndex, param, 8, overlayData[i]);
       //     }
      //  } else {
      //      cm = createColorModel(bitsStored, dataType);
      //  }
        //WritableRaster r = raster.createCompatibleWritableRaster();
        return new BufferedImage(cm, raster , false, null);
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
