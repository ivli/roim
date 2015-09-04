
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends /*IImage, */Iterable<ImageFrame> {   
    int getWidth();  
    int getHeight();
    boolean hasAt(int aFrameNumber);
    ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException; 
    ImageFrame image();    
    void extract(Extractor aEx);
    int getCurrent();
    int getNumFrames();      
    PixelSpacing getPixelSpacing();
    
    IMultiframeImage makeCompositeFrame(int aFrom, int aTo) ; 
    
    @Override
    default public java.util.Iterator<ImageFrame> iterator() {    
        return new java.util.Iterator<ImageFrame>() {
            int _next=0;
            @Override
            public boolean hasNext() {    
                return hasAt(_next);
            }

            @Override
            public ImageFrame next() {
                return getAt(_next++);
            }  
        };
    }
}
