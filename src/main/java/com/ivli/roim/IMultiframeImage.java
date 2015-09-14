
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends Iterable<ImageFrame>, java.io.Serializable {   
   
    int getWidth();  
    int getHeight();
    PixelSpacing getPixelSpacing();    
    
    boolean hasAt(int aFrameNumber);
     //moves cursor to aFrameNumber
    ImageFrame current(int aFrameNumber) throws java.util.NoSuchElementException; 
     //gets frame at aFrameNumber  cursor left untouched 
    ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException; 
     //returns frame at cursor position
    ImageFrame image();    
      //returns cursor position
    int getCurrent();
    int getNumFrames();      
    
    //void extract(Extractor aEx);
    
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
