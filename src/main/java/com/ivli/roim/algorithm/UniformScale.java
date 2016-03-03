
package com.ivli.roim.algorithm;

import com.ivli.roim.core.ImageFrame;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public class UniformScale {
    
    static int calcScale(int aS, int aF) {
        if (aF > 0)
            return aS / aF;
        else if (aF < 0)
            return aS / aF;
        else
            return aS;
    }    
    
    static ImageFrame scale(ImageFrame aSrc, int aScale) {
        final int width = aSrc.getWidth();
        final int height = aSrc.getHeight() ;
        final int []buf = aSrc.getPixelData();
        
        final int width2  = calcScale(width,  aScale);
        final int height2 = calcScale(height, aScale);      
        
        ImageFrame ret = new ImageFrame(width2, height2);
        
        final int [] buf2 = ret.getPixelData();
                
        for (int i=0; i < height; ++i)
            for(int j=0; j< width; ++j) {
                final int temp = buf[width*i+j];
                buf[width*i+j] = buf[width*(width-i-1)+j];
                buf[width*(width-i-1)+j] = temp;
            }
        
        return ret;
    }
}
