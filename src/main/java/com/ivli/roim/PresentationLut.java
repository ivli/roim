/*
 * Copyright (C) 2015 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 *
 * @author likhachev
 */
public final class PresentationLut implements com.ivli.roim.core.Transformation {
    protected String          iName;
    protected IndexColorModel iLUT;
    
    public PresentationLut(String aName) {
        open(aName);
    }
          
    public void open(String aName) {
        iName = (null == aName ? Settings.get(Settings.DEFAULT_PRESENTATION_LUT, LutLoader.BUILTIN_LUTS[1]):aName);
        Settings.get(Settings.DEFAULT_PRESENTATION_LUT, iName);
        iLUT = LutLoader.open(iName);
    }
    
    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        
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
