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


import com.ivli.roim.InterpolationMethod;
import com.ivli.roim.core.ImageFrame;
/**
 *
 * @author likhachev
 */
public class FrameProcessor {       
    private final ImageFrame iFrame;
       
    private int iInterpol;// 
        
    public FrameProcessor(ImageFrame aF) {
        iFrame = aF;   
        iInterpol = InterpolationMethod.INTERPOLATION_NONE;
    }
    
    public FrameProcessor(ImageFrame aF, int aI) {
        iFrame = aF;   
        iInterpol = aI;
    }
    
    public void setInterpolation(boolean aI) {
        iInterpol = aI ? InterpolationMethod.INTERPOLATION_BILINEAR : InterpolationMethod.INTERPOLATION_NONE;
    }
       
    protected int[] getPixelsCopy() {
        int[] pixels2 = new int[iFrame.getWidth()*iFrame.getHeight()];
        System.arraycopy(iFrame.getPixelData(), 0, pixels2, 0, iFrame.getWidth()*iFrame.getHeight());
        return pixels2;	
    }
       
    public void add(ImageFrame aF) throws IllegalArgumentException {
        if (iFrame.getWidth() != aF.getWidth() && iFrame.getHeight() != aF.getHeight())
            throw new IllegalArgumentException("Frames must be of identical size");
        
        for (int i = 0; i < iFrame.getWidth(); ++i)
            for (int j = 0; j < iFrame.getHeight(); ++j) 
                iFrame.set(i, j, iFrame.get(i, j) + aF.get(i, j));        
    }
    
    public void flipVert() {
        final int width  = iFrame.getWidth();
        final int height = iFrame.getHeight() ;
        final int [] buf = iFrame.getPixelData();
        
        for (int i=0; i < height/2; ++i)
            for(int j=0; j< width; ++j) {
                final int temp = buf[width*i+j];
                buf[width*i+j] = buf[width*(width-i-1)+j];
                buf[width*(width-i-1)+j] = temp;
            }
    }
    
    public void flipHorz() {
       final int width  = iFrame.getWidth();
        final int height = iFrame.getHeight() ;
        final int [] buf = iFrame.getPixelData();
        
        for (int i=0; i < height; ++i)
            for(int j=0; j< width/2; ++j) {
                final int temp = buf[width*i+j];                
                buf[width*i+j] = buf[width*(i+1) - j-1];
               buf[width*(i+1) - j -1] = temp;
            } 
    }
        
    public void rotate(final double anAngle) {               
        int roiX = 0;
        int roiY = 0;
        final int width = iFrame.getWidth();
        final int height = iFrame.getHeight();
        final int roiWidth = width;
        final int roiHeight = height;
        
        final int[] pixels = iFrame.getPixelData(); //source
        final int[] pixels2 = getPixelsCopy(); //destination
               
        double centerX = roiX + (roiWidth-1)/2.0;
        double centerY = roiY + (roiHeight-1)/2.0;
        int xMax = roiX + roiWidth - 1;

        final double angleRadians = -anAngle/(180.0/Math.PI);
        final double ca = Math.cos(angleRadians);
        final double sa = Math.sin(angleRadians);
        final double tmp1 = centerY*sa-centerX*ca;
        final double tmp2 = -centerX*sa-centerY*ca;
        
        final double dwidth = width, dheight = height;
        final double xlimit = width-1.0, xlimit2 = width-1.001;
        final double ylimit = height-1.0, ylimit2 = height-1.001;
        // zero is 32768 for signed images
        int background = 0;//cTable!=null && cTable[0]==-32768?32768:0; 
        double tmp3, tmp4, xs, ys;
        int ixs, iys;
       
        for (int y=roiY; y<(roiY + roiHeight); y++) {
            int index = width*y + roiX;
            tmp3 = tmp1 - y*sa + centerX;
            tmp4 = tmp2 + y*ca + centerY;
            for (int x=roiX; x<=xMax; x++) {
                xs = x*ca + tmp3;
                ys = x*sa + tmp4;
                if ((xs>=-0.01) && (xs<dwidth) && (ys>=-0.01) && (ys<dheight)) {
                    if (InterpolationMethod.INTERPOLATION_BILINEAR == iInterpol) {
                        if (xs<0.0) xs = 0.0;
                        if (xs>=xlimit) xs = xlimit2;
                        if (ys<0.0) ys = 0.0;			
                        if (ys>=ylimit) ys = ylimit2;
                        pixels2[index++] = (short)(getInterpolatedPixel(xs, ys, pixels) + 0.5);
                    } else {
                        ixs = (int)(xs+0.5);
                        iys = (int)(ys+0.5);
                        if (ixs>=width) ixs = width - 1;
                        if (iys>=height) iys = height -1;
                        pixels2[index++] = pixels[width*iys+ixs];
                    }
                } else
                    pixels2[index++] = background;
            }
        }
        iFrame.setPixelData(width, height, pixels2);
    }
   
    private double getInterpolatedPixel(final double aX, final double aY, final int[] aPixels) {
        final int xbase = (int)aX;
        final int ybase = (int)aY;
        final double xFraction = aX - xbase;
        final double yFraction = aY - ybase;
        final int offset = ybase * iFrame.getWidth() + xbase;
        final int lowerLeft  = aPixels[offset];
        final int lowerRight = aPixels[offset + 1];
        final int upperRight = aPixels[offset + iFrame.getWidth() + 1];
        final int upperLeft  = aPixels[offset + iFrame.getWidth()];
        final double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
        final double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
        return lowerAverage + yFraction * (upperAverage - lowerAverage);
    }

}
