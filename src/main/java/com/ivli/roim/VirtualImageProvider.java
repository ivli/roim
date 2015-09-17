
package com.ivli.roim;

import java.util.ArrayList;

/**
 *
 * @author likhachev
 */
public class VirtualImageProvider implements IImageProvider {
    IImageProvider iParent;
    final ArrayList<ImageFrame> iFrames;    
    TimeSliceVector iTimeSlices;     
         
    public VirtualImageProvider(IImageProvider aP) {
        iParent = aP;
        iFrames = new java.util.ArrayList<>(); 
        iTimeSlices = aP.getTimeSliceVector();
    }

    /*
    public VirtualImageProvider(IImageProvider aP, ImageFrame aF) {
        iParent = aP;
        iFrames = new java.util.ArrayList<>(); 
        iFrames.add(aF);
    }
    */
    
    public int getWidth() {
        return iParent.getWidth();
    } 
    
    public int getHeight() {
        return iParent.getHeight();
    }
    
    public int getNumFrames() throws java.io.IOException {
        return iFrames.size();
    }
    
    public PixelSpacing getPixelSpacing() {
        return iParent.getPixelSpacing();
    }
    
    public TimeSliceVector getTimeSliceVector() {
        return iParent.getTimeSliceVector();
    }
    
    public ImageFrame frame(int anIndex) throws IndexOutOfBoundsException/*, java.io.IOException */{
        return iFrames.get(anIndex);
    }
       
    public IImageProvider slice(TimeSlice aS) {
        throw new UnsupportedOperationException(); //TODO:
        
    }
    
    public IImageProvider collapse(TimeSlice aS) {
        throw new UnsupportedOperationException(); //TODO:
    }
}
