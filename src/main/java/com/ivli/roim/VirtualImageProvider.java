
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class VirtualImageProvider extends ImageProvider {
    ImageProvider iParent;
    //final ArrayList<ImageFrame> iFrames;    
    //TimeSliceVector iTimeSlices;     
         
    public VirtualImageProvider(ImageProvider aP) {    
        iParent = aP;
        iWidth = aP.getWidth();
        iHeight = aP.getHeight();
        iNoOfFrames = aP.getNumFrames();
        iPixelSpacing = aP.getPixelSpacing();
        //protected TimeSliceVector iTimeSlices; 
        //protected ArrayList<ImageFrame> iFrames;
    }

}
