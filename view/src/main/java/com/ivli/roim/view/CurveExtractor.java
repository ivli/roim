/*
 * Copyright (C) 2016 likhachev
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

package com.ivli.roim.view;

import com.ivli.roim.core.SeriesCollection;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Measure;
import com.ivli.roim.core.Series;
import com.ivli.roim.core.FrameOffsetVector;
import com.ivli.roim.core.FrameOffset;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.TimeSliceVector;
/**
 *
 * @author likhachev
 */
public class CurveExtractor {    
    public static SeriesCollection extract(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        switch (anImage.getImageType().getTypeName()) {            
            case ImageType.NM_TOMO:
            case ImageType.NM_TOMO_G:
            case ImageType.NM_VOLUME:       
            case ImageType.NM_VOLUME_G:    
                return extract_tomo(anImage, aRoi, anOff);
            case ImageType.NM_STATIC:
            case ImageType.NM_DYNAMIC:
            default: 
                return extract_dynamic(anImage, aRoi, anOff);    
        }
    }
    
    private static SeriesCollection extract_tomo(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        //assert("not implemented yet");
        return new SeriesCollection();
    }    
   
    
    private static SeriesCollection extract_dynamic(IMultiframeImage anImage, ROI aRoi, FrameOffsetVector anOff) {
        SeriesCollection c = new SeriesCollection();
       
        Series density = new Series(Measurement.DENSITY);
        Series mins    = new Series(Measurement.MINPIXEL);
        Series maxs    = new Series(Measurement.MAXPIXEL);
        
        final TimeSliceVector tsv = anImage.getTimeSliceVector();
        final long smd = tsv.getSmallestDuration();
        Shape roi = aRoi.getShape(); 
        
        for (int n = 0; n < anImage.getNumFrames(); ++n) {  
            if (null != anOff) {
                final FrameOffset off = anOff.get(n);
                if (off != FrameOffset.ZERO)
                   roi = AffineTransform.getTranslateInstance(off.getX(), off.getY()).createTransformedShape(roi);               
            }
            
            /* 
             * normalize result to smallest frame duration
             */
            final double norm = (double)tsv.getFrameDuration(n) / (double)smd;
            
            final Measure m = anImage.get(n).processor().measure(roi); 
            density.add(m.getIden() / norm );   
            mins.add(m.getMin() / norm);
            maxs.add(m.getMax() / norm);
        } 
        
        c.addSeries(density);
        c.addSeries(mins);
        c.addSeries(maxs);
        
        return c;
    }       
}
