
package com.ivli.roim;

import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.PixelSpacing;
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
