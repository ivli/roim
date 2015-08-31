
package com.ivli.roim;

import java.io.IOException;

/**
 *
 * @author likhachev
 */
public interface IImage {
    int getWidth();  
    int getHeight();
   /// PixelSpacing getPixelSpacing();
    
    ImageFrame image();    
    void extract(Extractor aEx);
}
