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
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public interface IImageProvider {                  
    public int getWidth() ;    
    public int getHeight() ;   
    public int getNumFrames();            
    public PixelSpacing getPixelSpacing();       
    public TimeSliceVector getTimeSliceVector();       
    public ImageFrame frame(int anIndex) throws IndexOutOfBoundsException;
     
    
    /*   
    public ImageProvider slice(TimeSlice aS) {
        //VirtualImageProvider ret = new VirtualImageProvider(this);
        
        return null;
    }
       
   
    public ImageProvider collapse(TimeSlice aS){   
        int frameTo = aS.getTo().isInfinite() ? getNumFrames() : getTimeSliceVector().frameNumber(aS.getTo());
        int frameFrom = getTimeSliceVector().frameNumber(aS.getFrom());        
               
        java.awt.image.WritableRaster comp = frame(0).getRaster().createCompatibleWritableRaster();
                
        for (int n = frameFrom; n < frameTo; ++n) {
            final java.awt.image.Raster r = frame(n).getRaster();
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   comp.setSample(i, j, 0, comp.getSample(i, j, 0) + r.getSample(i, j, 0));           
        
        }
        
        VirtualImageProvider ret = new VirtualImageProvider(this);
        
        ret.iTimeSliceVector = getTimeSliceVector().slice(aS);
        ret.iFrames.add(new ImageFrame(comp));
        ret.iNoOfFrames = 1;
        
        return ret; 
    }
    */
}


