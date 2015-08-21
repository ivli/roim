
package com.ivli.roim;


/**
 *
 * @author likhachev
 */
public interface IMultiframeImage extends IImage {   
    boolean hasAt(int aFrameNumber);
    ImageFrame getAt(int aFrameNumber);  
    int getCurrentNo();
    int getNumFrames();    
    Curve makeCurveFromRoi(ROI aRoi);    
}
