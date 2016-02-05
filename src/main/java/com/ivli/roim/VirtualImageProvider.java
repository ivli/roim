
package com.ivli.roim;

import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.TimeSliceVector;

/**
 *
 * @author likhachev
 */
public class VirtualImageProvider extends ImageProvider {
    final ImageProvider iParent;
    TimeSliceVector iTimeSliceVector;
             
    public VirtualImageProvider(ImageProvider aP) {    
        iParent = aP;
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();       
    }
    
    @Override
    public PixelSpacing getPixelSpacing() {
        return iParent.getPixelSpacing();
    }
       
    @Override
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSliceVector;
    }
}
