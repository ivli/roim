/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class CurveExtractor {
    IMultiframeImage iImages;
    
    CurveExtractor (IMultiframeImage aI) {
        iImages = aI;
    }
    
    void extract(ROI aRoi) {
        
        for (ImageFrame f : iImages) {            
            aRoi.iCurve.add(Measure.Measure(f.getRaster(), aRoi));           
        }        
    }
    
    void extract(java.util.ArrayList<ROI> aRoiList) {
        
        for (ImageFrame f : iImages) {
            for (ROI r : aRoiList) {
                r.iCurve.add(Measure.Measure(f.getRaster(), r));
            }                        
        }        
    }
    
}
