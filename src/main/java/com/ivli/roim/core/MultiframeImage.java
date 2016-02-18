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

public class MultiframeImage extends IMultiframeImage {
    protected final IImageProvider iProvider;
     //read from ImageProvider
    protected int iWidth;
    protected int iHeight;
    protected int iNumFrames;   
    protected PixelSpacing iPixelSpacing;
    protected TimeSliceVector iTimeSliceVector; 
     //computed
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    protected final java.util.ArrayList<ImageFrame> iFrames; 
    
    public MultiframeImage(IImageProvider aP) {
        iProvider = aP;
        
        iWidth = iProvider.getWidth();
        iHeight = iProvider.getHeight();
        iNumFrames = iProvider.getNumFrames();   
        iMin = Double.NaN;
        iMax = Double.NaN;

        iPixelSpacing = iProvider.getPixelSpacing();
        iTimeSliceVector = iProvider.getTimeSliceVector(); 
       
        iFrames = new java.util.ArrayList<>(iNumFrames);
        
        for (int n=0; n < iNumFrames; ++n)
            iFrames.add(n, null);        
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
    
    /**
     * 
     * @return 
     */    
    @Override
    public IMultiframeImage duplicate() {      
        try {
            return (MultiframeImage)this.clone();        
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("");
        }        
    }
    
    
    protected MultiframeImage(MultiframeImage aM) {
        iProvider = aM.iProvider;
        iFrames = new java.util.ArrayList<>();
    }
       
    public MultiframeImage collapse(TimeSlice aS){   
        int frameTo = aS.getTo().isInfinite() ? getNumFrames() : getTimeSliceVector().frameNumber(aS.getTo());
        int frameFrom = getTimeSliceVector().frameNumber(aS.getFrom());        
               
        java.awt.image.WritableRaster comp = iFrames.get(0).getRaster().createCompatibleWritableRaster();
                
        for (int n = frameFrom; n < frameTo; ++n) {
            final java.awt.image.Raster r = iFrames.get(n).getRaster();
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   comp.setSample(i, j, 0, comp.getSample(i, j, 0) + r.getSample(i, j, 0));           
        
        }
        
        MultiframeImage ret = new MultiframeImage(this.iProvider);
        
        ret.iTimeSliceVector = getTimeSliceVector().slice(aS);
        ret.iFrames.add(new ImageFrame(comp));
        ret.iNumFrames = 1;
        
        return ret; 
    } 
}

