
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public interface IImage {
    int getWidth();  
    int getHeight();
    PixelSpacing getPixelSpacing();
    
    ImageFrame image();    
    void extract(Extractor aEx);
}
