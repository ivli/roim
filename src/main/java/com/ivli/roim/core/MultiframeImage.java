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

import com.ivli.roim.algorithm.ImageProcessor;

public class MultiframeImage extends IMultiframeImage implements Cloneable, java.io.Serializable  {
    protected final IImageProvider iProvider;
     //read from ImageProvider
    protected int iWidth;
    protected int iHeight;
    protected int iNumFrames;   
    protected ImageType iImageType;
    protected PixelSpacing iPixelSpacing;
    protected SliceSpacing iSliceSpacing;
    protected TimeSliceVector iTimeSliceVector; 
     //computed
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    protected java.util.ArrayList<ImageFrame> iFrames; 
    
    public MultiframeImage(IImageProvider aP) {
        iProvider = aP;        
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNumFrames = aP.getNumFrames();           
        iImageType = aP.getImageType();
        iPixelSpacing = aP.getPixelSpacing();
        iTimeSliceVector = aP.getTimeSliceVector(); 
       
        iFrames = new java.util.ArrayList<>(iNumFrames);
        
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, null);        
    }
   
    public MultiframeImage(ImageFrame aF) {
        iProvider = null;  
        iWidth = aF.getWidth();
        iHeight = aF.getHeight();
        iNumFrames = 1;   
        
        iPixelSpacing = PixelSpacing.UNITY_PIXEL_SPACING;
        iTimeSliceVector = TimeSliceVector.ONESHOT; 
       
        iFrames = new java.util.ArrayList<>(iNumFrames);
        
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, null);      
    }
    
    private MultiframeImage(IMultiframeImage aM, int anImages) {        
        iProvider = null;
        iNumFrames = anImages;
        
        iWidth = aM.getWidth();
        iHeight = aM.getHeight();
        
        iPixelSpacing = aM.getPixelSpacing();
        iSliceSpacing = aM.getSliceSpacing();
        iTimeSliceVector = aM.getTimeSliceVector();
        
        iFrames = new java.util.ArrayList<>(iNumFrames);
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, new ImageFrame(iWidth, iHeight));  
  
    }
        
    protected void computeStatistics() {        
        iMin = Double.MAX_VALUE;
        iMax = Double.MIN_VALUE;
        
        for(int i=0; i < getNumFrames(); ++i) {
            ImageFrame f = get(i);
            if (f.getMin() < iMin)
                iMin = f.getMin();
            else if (f.getMax() > iMax)
                iMax = f.getMax();
        }               
    }
    
    public ImageDataType getImageDataType() {
        return ImageDataType.GRAYS32;
    }
     
    @Override
    public ImageType getImageType() {
         //TODO: implement reading from DICOM and sanity check
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
    public boolean hasAt(int aFrameNumber) {               
        return (aFrameNumber >=0 && aFrameNumber < iNumFrames);          
    }
       
     @Override
    public ImageFrame get(int aFrameNumber) throws java.util.NoSuchElementException {       
        ImageFrame ret = null;
        
        try {
            ret = iFrames.get(aFrameNumber);                            
        } catch (IndexOutOfBoundsException ex) {        
            iFrames.add(aFrameNumber, (ret = iProvider.get(aFrameNumber)));
        } finally {
            if (null == ret)
                iFrames.add(aFrameNumber, (ret = iProvider.get(aFrameNumber)));
        }
    
        return ret;         
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
    public IMultiframeImage createCompatibleImage(int aI) {
        //TODO: change type and ... 
        MultiframeImage ret = new MultiframeImage(this, aI); 
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
        return ret;
    }
       
     @Override
    public IMultiframeImage duplicate() {      
        MultiframeImage ret = new MultiframeImage(this);

        for(int n = 0; n < getNumFrames(); ++n)
           ret.iFrames.add(n, iFrames.get(n).duplicate());
       
        return ret;
    }
       
    public MultiframeImage collapse(TimeSlice aS){   
        int frameTo = aS.getTo().isInfinite() ? getNumFrames() : getTimeSliceVector().frameNumber(aS.getTo());
        int frameFrom = getTimeSliceVector().frameNumber(aS.getFrom());        
        
        ImageFrame sum = iFrames.get(frameFrom).duplicate();
        
        com.ivli.roim.algorithm.FrameProcessor fp = new com.ivli.roim.algorithm.FrameProcessor(sum);
        
        for (int n = frameFrom + 1; n < frameTo; ++n)            
            fp.add(iFrames.get(n));
       
        MultiframeImage ret = new MultiframeImage(this.iProvider);
        
        /* 
        ret.iTimeSliceVector = getTimeSliceVector().slice(aS);
        ret.iFrames.add(new ImageFrame(comp));
        ret.iNumFrames = 1;
        */
        return ret; 
    } 
    
    @Override
    public ImageProcessor processor() {
        return new ImageProcessor(this);     
    }
}

