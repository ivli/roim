
package com.ivli.roim;

import com.ivli.roim.core.IImageProvider;

/**
 *
 * @author likhachev
 */
public class VirtualImageProvider extends ImageProvider {
    IImageProvider iParent;
    //final ArrayList<ImageFrame> iFrames;    
    //TimeSliceVector iTimeSlices;     
         
    public VirtualImageProvider(IImageProvider aP) {    
        iParent = aP;
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNoOfFrames = aP.getNumFrames();
        iPixelSpacing = aP.getPixelSpacing();
        //protected TimeSliceVector iTimeSlices; 
        //protected ArrayList<ImageFrame> iFrames;
    }

}
