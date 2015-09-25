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
    
    Series extract(ROI aRoi) {
        Series c = new Series(aRoi.getName());
        
        for (ImageFrame f : iImages) {             
            c.add(Measure.New(f.getRaster(), aRoi.getShape()));           
        } 
        
        return c;
    }    
    
}
