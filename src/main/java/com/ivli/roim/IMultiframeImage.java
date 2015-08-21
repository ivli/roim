
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
    
    @Override
    default public Iterator<ImageFrame> iterator() {    
        return new Iterator<ImageFrame>() {
            public boolean hasNext() {    
                return hasAt(getCurrentNo() + 1);
            }

            public ImageFrame next() {
                return getAt(getCurrentNo() + 1);
            }  
        };
    }
}
