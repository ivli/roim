
package com.ivli.roim;

import com.ivli.roim.core.PixelSpacing;

/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends Iterable<ImageFrame>, java.io.Serializable {   
   
    int getWidth();  
    int getHeight();
    PixelSpacing getPixelSpacing();    
    int getNumFrames();  
    
    boolean hasAt(int aFrameNumber);
     //gets frame at aFrameNumber  cursor left untouched 
    ImageFrame getAt(int aFrameNumber) throws java.util.NoSuchElementException; 
    
    /*TODO rework to let collection be a collection and iterator be an iterator*/
     //moves cursor to aFrameNumber
    ImageFrame advance(int aFrameNumber) throws java.util.NoSuchElementException; 
    
     //returns frame at cursor position
    ImageFrame image();    
      //returns cursor position
    int getCurrent();
        
    
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
