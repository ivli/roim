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
public class ImageFrame implements java.io.Serializable {
    private static final long serialVersionUID = 042L;
   
    private int iWidth;
    private int iHeight;
    private int []iPixels;        
    private double iMin;
    private double iMax; 
    private double iIden;
    
    public ImageFrame(Raster aRaster) {       
        iWidth = aRaster.getWidth();
        iHeight = aRaster.getHeight();
        iPixels = aRaster.getSamples(0, 0, iWidth, iHeight, 0, (int[])null);
        
        computeStatistics();       
    }
          
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
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
    
    public final int get(int x, int y) {
        return iPixels[y*iWidth+x];//&0xffff;
    }

    public final void set(int x, int y, int value) {
        iPixels[y*iWidth+x] = value;
    }
    
    public final boolean isValidIndex(int aX, int aY) {
        return aX >=0 && aY >= 0 && aX < iWidth && aY < iHeight;
    }
    
    public final int getPixel(int aX, int aY) throws IndexOutOfBoundsException {
        if (!isValidIndex(aX, aY))
            throw new IndexOutOfBoundsException();            
        return get(aX, aY);
    }

    public final void setPixel(int aY, int aX, int aV) throws IndexOutOfBoundsException {
        if (!isValidIndex(aX, aY))
            throw new IndexOutOfBoundsException();  
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
    
    void rotate(double anAngle) {
        AffineTransform r = AffineTransform.getRotateInstance(anAngle * Math.PI/180);
        AffineTransformOp op = new AffineTransformOp(r, null);
        //iRaster = op.filter(iRaster, iRaster.createCompatibleWritableRaster());
        computeStatistics();  
    }
           
    private void computeStatistics() throws ArrayIndexOutOfBoundsException { 
        iMin  = 65535.; 
        iMax  = .0; 
        iIden = .0;
        
        for (int i = 0; i < iWidth; ++i)
            for (int j = 0; j < iHeight; ++j) { 
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
    
    public void extract(Extractor aEx) {
        aEx.apply(this);
    }
        
}
