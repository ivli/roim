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
public class ImageFrame implements java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 042L;
   /*
    private int iMin;
    private int iMax;    
    private double iIden;// = Double.NaN;
    
    private boolean iStatisticsIsValid = false;
    */
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
       /*
        ret.iIden = iIden;
        ret.iMin = iMin;
        ret.iMax = iMax;
        */
        return ret;
    }
    
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
    }
/*       
    public double getMin() {
        return iMin;
    }
    
    public double getMax() {     
        return iMax;
    }
    
    public double getIden() {
        return iIden;
    }
    */
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
       
        return ImageDataType.GRAYS32;
    }
   
    public int[] getPixelData() {
        return iPixels;
    }
    
    public void setPixelData(int aWidth, int aHeight, int[] aPixels) {       
        iWidth  = aWidth;
        iHeight = aHeight;
        
        System.arraycopy(aPixels, 0, iPixels, 0, aWidth * aHeight);  
        //iStatisticsIsValid = false;
    }
      
    public FrameProcessor processor(){
        return new FrameProcessor(this);
    }
    
}
