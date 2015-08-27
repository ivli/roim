
package com.ivli.roim;

import java.util.Iterator;


/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends IImage, Iterable<ImageFrame> {   
    boolean hasAt(int aFrameNumber);
    ImageFrame getAt(int aFrameNumber);  
    int getCurrentNo();
    int getNumFrames(); 
    ImageFrame makeCompositeFrame(int aFrom, int aTo);  
    PixelSpacing getPixelSpacing();
    
    
    @Override
    default public Iterator<ImageFrame> iterator() {    
        return new Iterator<ImageFrame>() {
            int _next=0;
            public boolean hasNext() {    
                return hasAt(_next);
            }

            public ImageFrame next() {
                return getAt(_next++);
            }  
        };
    }
}
