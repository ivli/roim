/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim.core;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.DataBuffer;
/**
 * 
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 042L;
   
    private int iWidth;
    private int iHeight;
        
    private double iMin = Double.NaN;
    private double iMax = Double.NaN; 
    private double iIden = Double.NaN;
    
    private int []iPixels;        
    
    /* 
    public ImageFrame(Raster aRaster) {       
        iWidth = aRaster.getWidth();
        iHeight = aRaster.getHeight();
        iPixels = aRaster.getSamples(0, 0, iWidth, iHeight, 0, (int[])null);
        
        computeStatistics();       
    }
    */
    
    public ImageFrame(int aWidth, int aHeight, int[] aPixels) {       
        iWidth  = aWidth;
        iHeight = aHeight;
        iPixels = aPixels;      
    }
    
    public ImageFrame(int aWidth, int aHeight) {       
        iWidth = aWidth;
        iHeight = aHeight;
        iPixels = new int[aWidth * aHeight];         
    }
       
    public ImageFrame duplicate() {       
        ImageFrame ret = new ImageFrame(iWidth, iHeight, iPixels);    
        ret.iMin = iMin;
        ret.iMax = iMax;
        ret.iIden = iIden;
        return ret;
    }
    
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
    }
     
    public double getMin() {
        if (Double.isNaN(iMin))
            computeStatistics();
        return iMin;
    }
    
    public double getMax() {
        if (Double.isNaN(iMax))
            computeStatistics();
        return iMax;
    } 
    
    public double getIden() {
        if (Double.isNaN(iIden))
            computeStatistics();
        return iIden;
    }
    
    public final int get(int aX, int aY) {
        return iPixels[aY*iWidth+aX];
    }

    public final void set(int aX, int aY, int aV) {
        iPixels[aY*iWidth+aX] = aV;
    }
    
    public final boolean isValidIndex(int aX, int aY) {
        return aX >=0 && aY >= 0 && aX < iWidth && aY < iHeight;
    }
    
    public final int getPixel(int aX, int aY) throws IndexOutOfBoundsException {
        if (!isValidIndex(aX, aY))
            throw new IndexOutOfBoundsException(String.format("bad index x=%d (%d), y=%d (%d)", aX, iWidth, aY, iHeight));            
        
        return get(aX, aY);
    }

    public final void setPixel(int aY, int aX, int aV) throws IndexOutOfBoundsException {
        if (!isValidIndex(aX, aY))
            throw new IndexOutOfBoundsException(String.format("bad index x=%d (%d), y=%d (%d)", aX, iWidth, aY, iHeight));  
        set(aX, aY, aV);
    }
    
    public ImageDataType getImageDataType() {
        /*
        switch (iRaster.getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE: return ImageDataType.GRAYS8;
            case DataBuffer.TYPE_SHORT: return ImageDataType.GRAYS16;
            case DataBuffer.TYPE_INT: //fall through
            default: return ImageDataType.GRAYS32;
        } */
        return ImageDataType.GRAYS32;
    }
    
    public BufferedImage getBufferedImage() {               
        WritableRaster wr = Raster.createBandedRaster(DataBuffer.TYPE_INT, iWidth, iHeight, 1, new java.awt.Point());
        wr.setDataElements(0, 0, iWidth, iHeight, iPixels);
       
        return new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),                                                               
                                                         new int[] {8},
                                                         false,		// has alpha
                                                         false,		// alpha premultipled
                                                         Transparency.OPAQUE,
                                                         wr.getDataBuffer().getDataType()),                                                                                                                                                                                         
                                 wr, true, null);
        
    }     
                  
    private void computeStatistics() throws IndexOutOfBoundsException { 
        iMin  = 65535.; 
        iMax  = .0; 
        iIden = .0;
        
        for (int i = 0; i < iWidth; i++)
            for (int j = 0; j < iHeight; j++) { 
                final double temp = (double)getPixel(i, j);
                if (temp > iMax) 
                    iMax = temp;
                else if (temp < iMin) 
                    iMin = temp;
                iIden += temp;
        }
    }
         
    public int[] getSamples() {
        return iPixels;
    }
    
    public void setPixelData(int aWidth, int aHeight, int[] aPixels) {       
        iWidth  = aWidth;
        iHeight = aHeight;
        iPixels = aPixels;   
        iMin = Double.NaN;
        iMax = Double.NaN; 
        iIden = Double.NaN;
    }
    
    public void extract(Extractor aEx) {
        aEx.apply(this);
    }        
}
