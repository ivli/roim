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
import java.io.IOException;
import java.util.BitSet;

public class MultiframeImage extends IMultiframeImage   {
    protected final IImageProvider iProvider;
     //read from ImageProvider
    protected final IMultiframeImage iParent;
    
    protected final int iWidth;
    protected final int iHeight;
    protected final int iNumFrames;   
    
    protected final ImageType iImageType;
    protected PixelSpacing iPixelSpacing;
    protected SliceSpacing iSliceSpacing;
    protected TimeSliceVector iTimeSliceVector; 
    protected PValueTransform iPVT; 
     //computed
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    
    private final class PixelBuffer {               
        private final BitSet iMask;       
        private final int [][] iBuf;
        
        PixelBuffer(int aW, int aH, int aF){
            iBuf = new int[aF][aW*aH];
            iMask = new BitSet(aF);
        }
        
        boolean isPresent(int aF) {
            return iMask.get(aF);
        }
        
        int[] get(int aF) {
            return iBuf[aF];
        }
        
        void present(int aF) {
            iMask.set(aF, true);
        }
        
        void present() {           
            iMask.set(0, iMask.size(), true);
        }
 
        void copyFrom(int aF, int[] aS) {
            System.arraycopy(aS, 0, iBuf, aF * iWidth * iHeight, iWidth * iHeight);
        }
        
        void copyTo(int aF, int[] aS) {
            System.arraycopy(iBuf, aF * iWidth * iHeight, aS, 0, iWidth * iHeight);
        }
    }
            
    private final PixelBuffer iFrames;
   
    public IMultiframeImage parent() {return iParent;}
    
    private MultiframeImage(IImageProvider aP) {
        iProvider = aP;        
        iParent = null;
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNumFrames = aP.getNumFrames();           
        iImageType = aP.getImageType();
        iPixelSpacing = aP.getPixelSpacing();
        iTimeSliceVector = aP.getTimeSliceVector(); 
        iPVT = aP.getRescaleTransform();
        iFrames = new PixelBuffer(iWidth, iHeight, iNumFrames);
    }
   
    private MultiframeImage(ImageFrame aF) {
        iProvider = null;  
        iParent = null; //TODO: it looks like strayed image, do i have to set parent for it ????? 
        iWidth = aF.getWidth();
        iHeight = aF.getHeight();
        iNumFrames = 1;           
        iPixelSpacing = PixelSpacing.UNITY_PIXEL_SPACING;
        iTimeSliceVector = TimeSliceVector.ONESHOT; 
        iPVT = PValueTransform.DEFAULT;
        iImageType = ImageType.UNKNOWN;
        iFrames = new PixelBuffer(iWidth, iHeight, iNumFrames);         
    }
    
    private MultiframeImage(IMultiframeImage aM, int aX, int aY, int aFrames) {        
        iProvider = null;
        iParent = aM;
        iNumFrames = aFrames;        
        iWidth = aX;// aM.getWidth();
        iHeight = aY;//aM.getHeight();        
        iPixelSpacing = aM.getPixelSpacing();
        iSliceSpacing = aM.getSliceSpacing();
        iTimeSliceVector = aM.getTimeSliceVector();
        iImageType = aM.getImageType();
        iPVT = aM.getRescaleTransform();        
        iFrames = new PixelBuffer(iWidth, iHeight, iNumFrames); 
        iFrames.present(); //have no provider - must not call it
    }
    
    public static IMultiframeImage create(IImageProvider aP) {    
        return new MultiframeImage(aP);
    }
       
    protected void computeStatistics() {                        
        iMin = iProvider.getMin();
        iMax = iProvider.getMax();
        //TODO: handle the situation min/max not present in DICOM - what to do? calculate through entire seri?    
        
    }
    
    public Modality getModality() {
        return iProvider.getModality();
    }
    
    public Photometric getPhotometric() {
        return iProvider.getPhotometric();
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
    public PValueTransform getRescaleTransform() {
        return iPVT;
    }
    
    @Override
    public boolean hasAt(int aFrameNumber) {               
        return (aFrameNumber >= 0 && aFrameNumber < iNumFrames);          
    }
       
    @Override
    public ImageFrame get(int aFrameNumber) throws IndexOutOfBoundsException {                      
        if (!hasAt(aFrameNumber))
            throw new IndexOutOfBoundsException(String.format("requesting frame = %d, of %d", aFrameNumber, iNumFrames));

        final int[] pix = iFrames.get(aFrameNumber);
        
        if (!iFrames.isPresent(aFrameNumber)) {                     
            try{
                iProvider.readFrame(aFrameNumber, pix);                
                iFrames.present(aFrameNumber);
            } catch(IOException ex) {
                //what to do here ???
                throw new IndexOutOfBoundsException();
            }
        }
        
        return new ImageFrame(getWidth(), getHeight(), pix);        
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
    public IMultiframeImage createCompatibleImage(int aNumberOfFrames) {
        //TODO: change type and ... parent too 
        return createCompatibleImage(getWidth(), getHeight(), aNumberOfFrames);             
    }
    
     @Override
    public IMultiframeImage createCompatibleImage(int aX, int aY, int aZ) {
        MultiframeImage ret = new MultiframeImage(this, getWidth(), getHeight(), aZ);        
        ret.iFrames.present();
        return ret;
    }
       
     @Override
    public IMultiframeImage duplicate() { 
        //TODO: parent???
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
        
        ret.iFrames.present();
        return ret;
    }
      
    @Override
    public ImageProcessor processor() {
        return new ImageProcessor(this);     
    }
}

