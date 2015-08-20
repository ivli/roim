
package com.ivli.roim;


/**
 *
 * @author likhachev
 */



public abstract class IMultiframeImage extends IImage {   
    public abstract boolean hasAt(int aFrameNumber);
    public abstract ImageFrame getAt(int aFrameNumber);
    public abstract ImageFrame image();
    public abstract int getNumFrames();
    //public abstract int getWidth();
    //public abstract int getHeight();
    public abstract Curve makeCurveFromRoi(ROI aRoi);
    
}
