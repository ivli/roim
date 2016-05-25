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

import com.ivli.roim.io.IImageProvider;
import com.ivli.roim.algorithm.ImageProcessor;

public class MultiframeImage extends IMultiframeImage   {
    protected final IImageProvider iProvider;
     //read from ImageProvider
    protected final int iWidth;
    protected final int iHeight;
    protected final int iNumFrames;   
    
    protected ImageType iImageType;
    protected PixelSpacing iPixelSpacing;
    protected SliceSpacing iSliceSpacing;
    protected TimeSliceVector iTimeSliceVector; 
    protected PValueTransform iPVT; 
     //computed
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    //protected java.util.ArrayList<ImageFrame> iFrames; 
    private final class Buffer {
        private final boolean[] iMask;         
        private final int [][] iBuf;
        
        Buffer(int aW, int aH, int aF) {
            iBuf = new int[aF][aW*aH];
            iMask = new boolean[aF];
        }
        
        boolean isPresent(int aF) {
            return iMask[aF];
        }
        
        void present(int aF) {
            iMask[aF] = true;
        }
        
        void all() {
            for(int i = 0; i < iMask.length; ++i)
                iMask[i] = true;            
        }
        
        int[] get(int aF) {
            return iBuf[aF];
        }
        
        void copyFrom(int aF, int[] aS) {
            System.arraycopy(aS, 0, iBuf, aF * iWidth * iHeight, iWidth * iHeight);
        }
        
        void copyTo(int aF, int[] aS) {
            System.arraycopy(iBuf, aF * iWidth * iHeight, aS, 0, iWidth * iHeight);
        }
    }
            
    private final Buffer iFrames;
    
    public MultiframeImage(IImageProvider aP) {
        iProvider = aP;        
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNumFrames = aP.getNumFrames();           
        iImageType = aP.getImageType();
        iPixelSpacing = aP.getPixelSpacing();
        iTimeSliceVector = aP.getTimeSliceVector(); 
        iPVT = aP.getTransform();
        iFrames = new Buffer(iWidth, iHeight, iNumFrames);
    }
   
    public MultiframeImage(ImageFrame aF) {
        iProvider = null;  
        iWidth = aF.getWidth();
        iHeight = aF.getHeight();
        iNumFrames = 1;           
        iPixelSpacing = PixelSpacing.UNITY_PIXEL_SPACING;
        iTimeSliceVector = TimeSliceVector.ONESHOT; 
        iPVT = PValueTransform.DEFAULT_TRANSFORM;
        iFrames = new Buffer(iWidth, iHeight, iNumFrames);    
    }
    
    private MultiframeImage(IMultiframeImage aM, int aFrames) {        
        iProvider = null;
        iNumFrames = aFrames;        
        iWidth = aM.getWidth();
        iHeight = aM.getHeight();        
        iPixelSpacing = aM.getPixelSpacing();
        iSliceSpacing = aM.getSliceSpacing();
        iTimeSliceVector = aM.getTimeSliceVector();
        iPVT = aM.getTransform();
        iFrames = new Buffer(iWidth, iHeight, iNumFrames); 
        iFrames.all(); //have no provider - must not call it
    }
    
    /*  */  
    protected void computeStatistics() {                        
        iMin = iProvider.getMin();
        iMax = iProvider.getMax();
    }
   
    public ImageDataType getImageDataType() {
        return ImageDataType.GRAYS32;
    }
     
    @Override
    public ImageType getImageType() {        
        return iImageType;
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
    public int getNumFrames() {       
        return iNumFrames;    
    }
    
    @Override
    public PixelSpacing getPixelSpacing() {
        return iPixelSpacing;
    }
    
    public SliceSpacing getSliceSpacing() {
        return iSliceSpacing;
    }
     
    @Override
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSliceVector;
    }
    
    @Override
    public PValueTransform getTransform() {
        return iPVT;
    }
    
    @Override
    public boolean hasAt(int aFrameNumber) {               
        return (aFrameNumber >=0 && aFrameNumber < iNumFrames);          
    }
       
    @Override
    public ImageFrame get(int aFrameNumber) throws IndexOutOfBoundsException {                      
        if (!hasAt(aFrameNumber))
            throw new IndexOutOfBoundsException();
                
        if (!iFrames.isPresent(aFrameNumber)) {
            int[] t = iFrames.get(aFrameNumber);
            iProvider.readFrame(aFrameNumber, t);            
        }
        
        return new ImageFrame(getWidth(), getHeight(), iFrames.get(aFrameNumber));        
    }          
    
     @Override             
    public double getMin() { 
        if (Double.isNaN(iMin))
            computeStatistics();
        return iMin;
    }  
    
     @Override
    public double getMax() { 
        if (Double.isNaN(iMax))
            computeStatistics();
        return iMax;
    }  
        
     @Override
    public IMultiframeImage createCompatibleImage(int aNumberOfFRames) {
        //TODO: change type and ... 
        MultiframeImage ret = new MultiframeImage(this, aNumberOfFRames); 
        /*
        ret.iNumFrames = aI;
        ret.iWidth = iWidth;
        ret.iHeight = iHeight;
        ret.iMin = ret.iMax = Double.NaN;
        ret.iPixelSpacing = iPixelSpacing;
        ret.iSliceSpacing = iSliceSpacing;
        ret.iTimeSliceVector = iTimeSliceVector;
        
        ret.iFrames = new java.util.ArrayList<>(iNumFrames);
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, new ImageFrame(iWidth, iHeight, new int[iWidth*iHeight]));          
        */
        
        ret.iFrames.all();
        
        return ret;
    }
       
     @Override
    public IMultiframeImage duplicate() {      
        MultiframeImage ret = new MultiframeImage(this.iProvider);
        /*
        for(int n = 0; n < getNumFrames(); ++n)
           ret.iFrames.add(n, get(n).duplicate());
       
        int i1 = ret.iFrames.iBuf.length;
        int i2 = ret.iFrames.iBuf[0].length;
        for (int i=0; i < ret.iFrames.iBuf.length; ++i)
            System.arraycopy(iFrames.iBuf[i], 0, ret.iFrames.iBuf[i], 0, ret.iFrames.iBuf[i].length);
        ret.iFrames.all();
        */
        
        for (int i=0; i < getNumFrames(); ++i) {
            ImageFrame f = get(i);
            System.arraycopy(iFrames.iBuf[i], 0, ret.iFrames.iBuf[i], 0, ret.iFrames.iBuf[i].length);            
        }
        
        ret.iFrames.all();
        return ret;
    }
    
    /*    
    public MultiframeImage collapse(TimeSlice aS){   
        int frameTo = aS.getTo().isInfinite() ? getNumFrames() : getTimeSliceVector().frameNumber(aS.getTo());
        int frameFrom = getTimeSliceVector().frameNumber(aS.getFrom());        
         
        ImageFrame sum = iFrames.get(frameFrom).duplicate();
        
        FrameProcessor fp = new FrameProcessor(sum);
        
        for (int n = frameFrom + 1; n < frameTo; ++n)            
            fp.add(iFrames.get(n));
       
        MultiframeImage ret = new MultiframeImage(this.iProvider);
        
       
        ret.iTimeSliceVector = getTimeSliceVector().slice(aS);
        ret.iFrames.add(new ImageFrame(comp));
        ret.iNumFrames = 1;
        
        return ret; 
    } 
    */
    @Override
    public ImageProcessor processor() {
        return new ImageProcessor(this);     
    }
}

