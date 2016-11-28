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
package com.ivli.roim.algorithm;

import com.ivli.roim.core.IFrameProvider;
import com.ivli.roim.core.InterpolationMethod;
import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.TimeSlice;
import com.amd.aparapi.Kernel;
import com.ivli.roim.core.Measure;
import java.awt.Shape;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 *
 * @author likhachev
 */
public class ImageProcessor {    
    private final IMultiframeImage iImage;
    private int iInterpol;
    
    public ImageProcessor(IMultiframeImage anImage) {
        iImage = anImage;
        iInterpol = InterpolationMethod.INTERPOLATION_NONE;
    }
    
    public void setInterpolation(boolean aI) {
        iInterpol = aI ? InterpolationMethod.INTERPOLATION_BILINEAR : InterpolationMethod.INTERPOLATION_NONE;  
    }
    
    private boolean safeTestArgs(int aFrom, int aTo) {        
        return (iImage.hasAt(aFrom) && iImage.hasAt(aTo));
    }
    
     private void testArgs(int aFrom, int aTo) {       
        if (!safeTestArgs(aFrom, aTo))
            throw new IllegalArgumentException("wrong frame number");        
    }
     
    public void flipVert(int aFrom, int aTo) {    
        if (aTo == IFrameProvider.LAST)
            aTo = iImage.getNumFrames() - 1;
        
        testArgs(aFrom, aTo);
                  
        for (int i = aFrom; i <= aTo; ++i) 
            new FrameProcessor(iImage.get(i), iInterpol).flipVert();
         
    }
    
    public void flipVert() {   
        iImage.forEach(f -> f.processor().flipVert());
        //IntStream.range(0, iImage.getNumFrames())
        //         .parallel()
        //         .forEach(i -> iImage.get(i).processor().flipVert());
    }    
    
    public void flipHorz(int aFrom, int aTo) {    
        if (aTo == IFrameProvider.LAST)
            aTo = iImage.getNumFrames() - 1;
        
        testArgs(aFrom, aTo);
                      
        for (int i = aFrom; i <= aTo; ++i) 
            new FrameProcessor(iImage.get(i), iInterpol).flipHorz();       
    }
      
    public void flipHorz() {
        iImage.forEach((f) -> f.processor().flipHorz());     
    }
            
    public void rotate(final double anAngle, int aFrom, int aTo) {   
        if (aTo == IFrameProvider.LAST)
            aTo = iImage.getNumFrames() - 1;
        
        testArgs(aFrom, aTo);
                    
        for (int i = aFrom; i <= aTo; ++i)
           new FrameProcessor(iImage.get(i), iInterpol).rotate(anAngle);        
    }
    
    public void rotate(final double anAngle) {
        iImage.forEach((f) -> f.processor().rotate(anAngle));
        //IntStream.range(0, iImage.getNumFrames())
        //         .parallel()
        //         .forEach(i -> iImage.get(i).processor().rotate(anAngle));
    }      
        
    public IMultiframeImage collapse(int aFrom, int aTo) {      
        if (aTo == IFrameProvider.LAST)
            aTo = iImage.getNumFrames() - 1;
        
        testArgs(aFrom, aTo);
                
        IMultiframeImage ret = iImage.createCompatibleImage(1);
        
        int[] dst = ret.get(0).getPixelData();
        
        for (int i = aFrom; i <= aTo; ++i) {
            int []src = iImage.get(i).getPixelData();
            
            for (int j = 0; j < dst.length; ++j)
                dst[j] += src[j];
            
        }        
        return ret;
    } 
    
    public IMultiframeImage collapse(TimeSlice anInterval) {        
        int from = iImage.getTimeSliceVector().frameNumber(anInterval.getFrom().toLong());
        int to = iImage.getTimeSliceVector().frameNumber(anInterval.getTo().toLong());        
        return collapse(from, to);
    }
        
    public void map(double aKey) {
        for(ImageFrame f:iImage)
            f.processor().map(aKey);    
    }
    
    public Measure measure(Shape aR){
        Measure ret = new Measure(Integer.MAX_VALUE, Integer.MIN_VALUE, 0L);
        for (ImageFrame f : iImage) 
            ret.combine(f.processor().measure(aR));                   
        return ret;    
    }    
    
    public Measure measure(Shape aR, int aFrom, int aTo){
        if (aTo == IFrameProvider.LAST)
            aTo = iImage.getNumFrames() - 1;
        
        testArgs(aFrom, aTo);
        
        Measure ret = new Measure(Integer.MAX_VALUE, Integer.MIN_VALUE, 0L);
        
        for (int i=0; i <= aTo; ++i) 
            ret.combine(iImage.get(i).processor().measure(aR));                   
        
        return ret;    
    }   
    
    
}
