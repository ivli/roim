
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class CurveExtractor {
    final IMultiframeImage iImages;
    
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
