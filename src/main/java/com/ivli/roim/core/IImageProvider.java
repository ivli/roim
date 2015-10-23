
package com.ivli.roim.core;

import java.io.IOException;

/**
 *
 * @author likhachev
 */
public interface IImageProvider {   
    int getWidth();  
    int getHeight();
    int getNumFrames() throws IOException;
    PixelSpacing getPixelSpacing();
    TimeSliceVector getTimeSliceVector();
    
    IImageProvider slice(TimeSlice aS) throws IOException;
    IImageProvider collapse(TimeSlice aS) throws IOException;
    
    ImageFrame frame(int anIndex) throws IndexOutOfBoundsException, IOException;
}
