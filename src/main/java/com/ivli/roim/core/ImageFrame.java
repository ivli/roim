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

/**
 * 
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 042L;
   
    private int iWidth;
    private int iHeight;
        
    //private double iMin = Double.NaN;
    //private double iMax = Double.NaN; 
    private Range iRange = new Range(Double.NaN, Double.NaN);
    private double iIden = Double.NaN;
    
    private final int []iPixels;        
    
    /*
    public ImageFrame(ImageFrame aI) {       
        iWidth = aI.getWidth();
        iHeight = aI.getHeight();
        iPixels = new int[iWidth*iHeight];
        System.arraycopy(aI.getPixelData(), 0, iPixels, 0, iWidth*iHeight);
        //iMin = aI.iMin;
        //iMax = aI.iMax;
        iRange = new Range(aI.iRange);
        iIden = aI.iIden;
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
        ImageFrame ret = new ImageFrame(iWidth, iHeight); 
        System.arraycopy(getPixelData(), 0, ret.iPixels, 0, iWidth*iHeight);
       
        ret.iRange = new Range(iRange);
        ret.iIden = iIden;
        return ret;
    }
    
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
    }
       
    public Range getRange() {
        if (null == iRange || Double.isNaN(iIden))
            computeStatistics();
        return iRange;
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

    public final void setPixel(int aX, int aY, int aV) throws IndexOutOfBoundsException {
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
   
    private void computeStatistics() throws IndexOutOfBoundsException { 
        double Min = Double.MAX_VALUE; 
        double Max = Double.MIN_VALUE; 
        iIden = .0;
        
        for (int i = 0; i < iPixels.length; ++i) {       
            final int temp = iPixels[i];
            if (temp > Max) 
                Max = temp;
            if (temp < Min) 
                Min = temp;
            iIden += temp;        
        }
        
        iRange = new Range(Min, Max);
    }
         
    public int[] getPixelData() {
        return iPixels;
    }
    
    public void setPixelData(int aWidth, int aHeight, int[] aPixels) {       
        iWidth  = aWidth;
        iHeight = aHeight;
        
        System.arraycopy(aPixels, 0, iPixels, 0, aWidth * aHeight);  
        
        iIden = Double.NaN;
    }
    
    public void extract(Extractor aEx) {
        aEx.apply(this);
    }        
}
