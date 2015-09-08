
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public interface IImageProvider {   
    int getWidth();  
    int getHeight();
    int getNumFrames() throws java.io.IOException;
    PixelSpacing getPixelSpacing();
    TimeSliceVector getTimeSliceVector();
       
    ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, java.io.IOException;
}
