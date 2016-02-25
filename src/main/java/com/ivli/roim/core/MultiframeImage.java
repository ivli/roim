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

public class MultiframeImage extends IMultiframeImage implements Cloneable {
    protected final IImageProvider iProvider;
     //read from ImageProvider
    protected int iWidth;
    protected int iHeight;
    protected int iNumFrames;   
    protected PixelSpacing iPixelSpacing;
    protected TimeSliceVector iTimeSliceVector; 
     //computed
    protected Double iMin;
    protected Double iMax;
    
    protected java.util.ArrayList<ImageFrame> iFrames; 
    
    public MultiframeImage(IImageProvider aP) {
        iProvider = aP;        
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNumFrames = aP.getNumFrames();   
        iMin = Double.NaN;
        iMax = Double.NaN;

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
        iMin = Double.NaN;
        iMax = Double.NaN;

        iPixelSpacing = PixelSpacing.UNITY_PIXEL_SPACING;
        iTimeSliceVector = null;//TimeSliceVector.ONESHOT; 
       
        iFrames = new java.util.ArrayList<>(iNumFrames);
        
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, null);      
    }
    
    protected MultiframeImage(MultiframeImage aM) {
        iProvider = aM.iProvider;
        iFrames = new java.util.ArrayList<>();
    }
     
    protected void computeStatistics() {        
        iFrames.stream().forEach((f) -> {
            if (f.getMin() < iMin)
                iMin = f.getMin();
            else if (f.getMax() > iMax)
                iMax = f.getMax();
        });               
    }
    
    public ImageDataType getImageDataType() {
        return iFrames.get(0).getImageDataType();
    }
     
    @Override
    public ImageType getImageType() {
         //TODO: implement reading from DICOM and sanity check
        return getNumFrames() > 1 ? ImageType.DYNAMIC : ImageType.STATIC;
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
        ImageFrame ret = iFrames.get(aFrameNumber);
        if (null == ret)
            iFrames.add(aFrameNumber, (ret = iProvider.frame(aFrameNumber)));
        return ret;         
    }          
    
     @Override             
    public double getMin() { 
        if (Double.isFinite(iMin))
            computeStatistics();
        return iMin;
    }  
    
     @Override
    public double getMax() { 
        if (Double.isFinite(iMax))
            computeStatistics();
        return iMax;
    }  
        
     @Override
    public IMultiframeImage createCompatibleImage(int aI) {
        //TODO: change type and ... 
        MultiframeImage ret = new MultiframeImage(this);        
        return ret;
    }
       
     @Override
    public IMultiframeImage duplicate() {      
        MultiframeImage ret;
        try {
             ret = (MultiframeImage)this.clone();  
             ret.iFrames = (java.util.ArrayList<ImageFrame>)this.iFrames.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("");
        }        
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
}

