
package com.ivli.roim;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public class PresentationLut implements Transformation {
    protected String          iName;
    protected IndexColorModel iLUT;
    
    public PresentationLut(String aName) {
        setLUT(aName);
    }
    
    public IndexColorModel getLUT() {
        return iLUT;
    }
    
    public String getLUTName() {
        return iName;
    }
    
    public final void setLUT(String aName) {
        iLUT = LutLoader.open(iName = (null == aName ? Settings.DEFAULT_PRESENTATION_LUT:aName));
    }
    
    @Override
    public BufferedImage transform (BufferedImage aSrc, BufferedImage aDst) {
        
        if (null == aDst)
            aDst = new BufferedImage(aSrc.getWidth(), aSrc.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        WritableRaster wr = aDst.getRaster();

        Raster rs = aSrc.getRaster();

        for (int y=0; y < aDst.getHeight(); y++) {
            for (int x=0; x < aDst.getWidth(); x++) {
               final int ndx=rs.getSample(x, y, 0);
               final int sample=iLUT.getRGB(ndx); 
               final int[] rgb=new int[]{(sample&0x00ff0000)>>16, (sample&0x0000ff00)>>8, sample&0x000000ff};

               wr.setPixel(x, y, rgb);
            }
        }
        return aDst;
    }
  
}
