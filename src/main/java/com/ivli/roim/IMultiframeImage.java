/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public interface IMultiframeImage {   
    boolean hasAt(int aFrameNumber);
    ImageFrame getAt(int aFrameNumber);
    ImageFrame current();
    int getNumFrames();
    int getWidth();
    int getHeight();
    Curve makeCurveFromRoi(ROI aRoi);
    void extract(Extractor aEx);
}
