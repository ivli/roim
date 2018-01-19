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

//import java.io.IOException;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.ivli.roim.io.spi.IImageService;
import com.ivli.roim.algorithm.ImageProcessor;
import com.ivli.roim.io.spi.ImageServiceException;


public class MultiframeImage extends IMultiframeImage   {
    protected final IImageService iService;    
    protected final IMultiframeImage iParent;
    
    protected int iWidth;
    protected int iHeight;
    protected int iNumFrames;   
    
    protected ImageType iImageType;
    protected PixelSpacing iPixelSpacing;
    protected SliceSpacing iSliceSpacing;
    protected TimeSliceVector iTimeSliceVector; 
    protected ModalityTransform iPVT; 
     //computed
    protected Double iMin = Double.NaN;
    protected Double iMax = Double.NaN;
    
    
    private final class PixelBuffer {               
        private final BitSet iMask;       
        private final int [][]iBuf;
        
        PixelBuffer(int aW, int aH, int aF, int [][]aBuf){
            if (null == aBuf)
                iBuf = new int[aF][aW*aH];
            else
                iBuf = aBuf;
            ///TODO: add size checkings
            iMask = new BitSet(aF);
        }
        
        PixelBuffer(final PixelBuffer aSrc) {
            iBuf = new int[aSrc.iBuf.length][aSrc.iBuf[0].length];
            System.arraycopy(aSrc.iBuf, 0, iBuf, 0, aSrc.iBuf.length*aSrc.iBuf[0].length);
            iMask = new BitSet(aSrc.iMask.size());
            iMask.or(aSrc.iMask);
        }
        
        int[][] getData() {
            return iBuf;
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
    }
            
    private PixelBuffer iFrames;
   
    public IMultiframeImage parent() {return iParent;}
    
    private MultiframeImage(IImageService aP) {
        iService = aP;        
        iParent = null;
        IImage image = aP.image();
        iWidth = image.getWidth();
        iHeight = image.getHeight();
        iNumFrames = image.getNumFrames();           
        iImageType = image.getImageType();
        iPixelSpacing = image.getPixelSpacing();
        iTimeSliceVector = image.getTimeSliceVector(); 
        iPVT = image.getRescaleTransform();
        iFrames = new PixelBuffer(iWidth, iHeight, iNumFrames, null);
        
    }
    
    private MultiframeImage(IMultiframeImage aM, int aX, int aY, int aZ, int [][]aBuf) {        
        iService = null;
        iParent = aM;               
        iWidth = aX;// aM.getWidth();
        iHeight = aY;//aM.getHeight();        
        iPixelSpacing = aM.getPixelSpacing();
        iSliceSpacing = aM.getSliceSpacing();
        iNumFrames = aZ; 
        iTimeSliceVector = aM.getTimeSliceVector();
        iImageType = aM.getImageType();
        iPVT = aM.getRescaleTransform();    
        iFrames = new PixelBuffer(iWidth, iHeight, iNumFrames, aBuf); 
        
        iFrames.present(); //have no provider - must not call it
    }
    
    public static IMultiframeImage create(IImageService aP) {    
        return new MultiframeImage(aP);
    }
       
    protected void computeStatistics() {   
        
        iMin = iService.image().getMin();
        iMax = iService.image().getMax();
        //TODO: handle the situation min/max not present in DICOM - what to do? calculate through entire seri?            
    }
    
    public Modality getModality() {
        return iService.image().getModality();
    }
    
    public Photometric getPhotometric() {
        return iService.image().getPhotometric();
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
    public ModalityTransform getRescaleTransform() {
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
                iService.readFrame(aFrameNumber, pix);                
                iFrames.present(aFrameNumber);
            } catch(ImageServiceException ex) {
                //what to do here ???
                LOG.throwing(ex);
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
        return createCompatibleImage(getWidth(), getHeight(), aNumberOfFrames, null);             
    }
    
        @Override   
    public IMultiframeImage createCompatibleImage(int aX, int aY, int aZ, int[][] aBuf) {
        MultiframeImage ret = new MultiframeImage(this, aX, aY, aZ, aBuf);        
        ret.iFrames.present();
        return ret;
    }
    
    public int[][] getAsArray() {
        return iFrames.getData();
    }
    
     @Override
    public IMultiframeImage duplicate() { 
        //TODO: parent???
        MultiframeImage ret = new MultiframeImage(this.iService);              
        
        for (int i = 0; i < getNumFrames(); ++i) {
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
    private final static Logger LOG = LogManager.getLogger();
}

