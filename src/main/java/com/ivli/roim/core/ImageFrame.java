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

import com.ivli.roim.algorithm.FrameProcessor;

/**
 * 
 * @author likhachev
 */
public class ImageFrame implements java.io.Serializable, IImage, Cloneable {
    private static final long serialVersionUID = 042L;
   
    private int iMin;
    private int iMax;    
    private double iIden;// = Double.NaN;
    
    private boolean iStatisticsIsValid = false;
    
    private int iWidth;
    private int iHeight;
    private final int []iPixels;        
        
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
       
        ret.iIden = iIden;
        ret.iMin = iMin;
        ret.iMax = iMax;
        
        return ret;
    }
    
    @Override
    public int getWidth() {
        return iWidth;
    }
    
    @Override
    public int getHeight() {
        return iHeight;
    }
       
    @Override
    public double getMin() {
        if (!iStatisticsIsValid)
            computeStatistics();
        return iMin;
    }
    
    @Override
    public double getMax() {
        if (!iStatisticsIsValid)
            computeStatistics();
        return iMax;
    }
    
    public double getIden() {
       if (!iStatisticsIsValid)
            computeStatistics();
        return iIden;
    }
    
    public final int get(int aX, int aY) {
        return iPixels[aY*iWidth+aX];
    }

    public final void set(int aX, int aY, int aV) {
        iPixels[aY*iWidth+aX] = aV;
        iStatisticsIsValid = false;        
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
        iStatisticsIsValid = false;
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
   
    private void computeStatistics() {        
        iMin = Integer.MAX_VALUE;
        iMax = Integer.MIN_VALUE;
        iIden = .0;
        
        for (int i = 0; i < iPixels.length; ++i) {       
            final int temp = iPixels[i];
            if (temp > iMax) 
                iMax = temp;
            if (temp < iMin) 
                iMin = temp;
            iIden += temp;        
        }  
        iStatisticsIsValid = true;
    }
         
    public int[] getPixelData() {
        return iPixels;
    }
    
    public void setPixelData(int aWidth, int aHeight, int[] aPixels) {       
        iWidth  = aWidth;
        iHeight = aHeight;
        
        System.arraycopy(aPixels, 0, iPixels, 0, aWidth * aHeight);  
        iStatisticsIsValid = false;
    }
      
    public FrameProcessor processor(){
        return new FrameProcessor(this);
    }

    @Override
    public int getNumFrames() {
        return 1;
    }

    @Override
    public ImageType getImageType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PixelSpacing getPixelSpacing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SliceSpacing getSliceSpacing() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TimeSliceVector getTimeSliceVector() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PValueTransform getRescaleTransform() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
