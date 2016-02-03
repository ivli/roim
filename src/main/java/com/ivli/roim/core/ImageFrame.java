
package com.ivli.roim.core;

import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

/**
 *
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable {
    private static final long serialVersionUID = 042L;
    
    private Raster iRaster;  
    
    private double iMin;
    private double iMax; 
    private double iIden;
    
    public ImageFrame(Raster aRaster) {
        iRaster = aRaster;        
        computeStatistics();       
    }
    
    public Raster getRaster() {
        return iRaster;
    }
        
    public int getWidth() {
        return iRaster.getWidth();
    }
    
    public int getHeight() {
        return iRaster.getHeight();
    }
     
    public double getMin() {
        return iMin;
    }
    
    public double getMax() {
        return iMax;
    } 
    
    public double getIden() {
        return iIden;
    }

    public BufferedImage getBufferedImage() {        
        WritableRaster wr = iRaster.createCompatibleWritableRaster();
        wr.setRect(iRaster);
        
        return new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),                                                               
                                                         new int[] {8},
                                                         false,		// has alpha
                                                         false,		// alpha premultipled
                                                         Transparency.OPAQUE,
                                                         wr.getDataBuffer().getDataType()),                                                                                                                                                                                         
                                 wr, true, null);
    }     
    
    void rotate(double anAngle) {
        AffineTransform r = AffineTransform.getRotateInstance(anAngle * Math.PI/180);
        AffineTransformOp op = new AffineTransformOp(r, null);
        iRaster = op.filter(iRaster, iRaster.createCompatibleWritableRaster());
        computeStatistics();  
    }
           
    private void computeStatistics() throws ArrayIndexOutOfBoundsException {
        final Rectangle bnds = iRaster.getBounds();
        
        iMin  = 65535; 
        iMax  = .0; 
        iIden = .0;

        double temp[] = new double [iRaster.getNumBands()];

        for (int i = bnds.x; i < (bnds.x + bnds.width); ++i)
            for (int j = bnds.y; j < (bnds.y + bnds.height); ++j) { 

                    temp = iRaster.getPixel(i, j, temp);
                    if (temp[0] > iMax) 
                        iMax = temp[0];
                    else if (temp[0] < iMin) 
                        iMin = temp[0];
                    iIden += temp[0];
        }
    }
           
    public void extract(Extractor aEx) {
        aEx.apply(iRaster);
    }
        
}
