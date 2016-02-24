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
import com.ivli.roim.core.ImageFrame;
/**
 *
 * @author likhachev
 */
public class FrameProcessor {    
    public enum InterpolationMethod {
        NONE, BICUBIC, BILINEAR
    }       
    
    private ImageFrame iFrame;
    private int[] pixels;    
    private int width;
    private int height;
    
    private InterpolationMethod iInterpol = InterpolationMethod.NONE;
        
    public FrameProcessor(ImageFrame aF) {
        iFrame = aF;
        width  = iFrame.getWidth();
        height = iFrame.getHeight();
        pixels = iFrame.getSamples();
    }
    
    public void setInterpolate(boolean aI) {
        iInterpol = aI? InterpolationMethod.BICUBIC : InterpolationMethod.NONE;
    }
       
    protected short[] getPixelsCopy() {
        short[] pixels2 = new short[width*height];
        System.arraycopy(pixels, 0, pixels2, 0, width*height);
        return pixels2;	
    }
       
    public void add(ImageFrame aF) throws IllegalArgumentException {
        if (iFrame.getWidth() != aF.getWidth() && iFrame.getHeight() != aF.getHeight())
            throw new IllegalArgumentException("Frames must be of identical size");
        
        for (int i = 0; i < iFrame.getWidth(); ++i)
            for (int j = 0; j < iFrame.getHeight(); ++j) 
                iFrame.set(i, j, iFrame.get(i, j) + aF.get(i, j));
        
    }
    
    public void rotate(double anAngle) {       
        short[] pixels2 = getPixelsCopy();

        int roiX = 0;
        int roiY = 0;
        int roiWidth = width;
        int roiHeight = height;
        
  
        //ImageProcessor ip2 = null;
        //if (interpolationMethod==BICUBIC)
        //        ip2 = new ShortProcessor(getWidth(), getHeight(), pixels2, null);
        
        double centerX = roiX + (roiWidth-1)/2.0;
        double centerY = roiY + (roiHeight-1)/2.0;
        int xMax = roiX + roiWidth - 1;

        double angleRadians = -anAngle/(180.0/Math.PI);
        double ca = Math.cos(angleRadians);
        double sa = Math.sin(angleRadians);
        double tmp1 = centerY*sa-centerX*ca;
        double tmp2 = -centerX*sa-centerY*ca;
        double tmp3, tmp4, xs, ys;
        int index, ixs, iys;
        double dwidth=width, dheight=height;
        double xlimit = width-1.0, xlimit2 = width-1.001;
        double ylimit = height-1.0, ylimit2 = height-1.001;
        // zero is 32768 for signed images
        int background = 0;//cTable!=null && cTable[0]==-32768?32768:0; 

       
        for (int y=roiY; y<(roiY + roiHeight); y++) {
            index = y*width + roiX;
            tmp3 = tmp1 - y*sa + centerX;
            tmp4 = tmp2 + y*ca + centerY;
            for (int x=roiX; x<=xMax; x++) {
                xs = x*ca + tmp3;
                ys = x*sa + tmp4;
                if ((xs>=-0.01) && (xs<dwidth) && (ys>=-0.01) && (ys<dheight)) {
                    if (InterpolationMethod.BILINEAR == iInterpol) {
                        if (xs<0.0) xs = 0.0;
                        if (xs>=xlimit) xs = xlimit2;
                        if (ys<0.0) ys = 0.0;			
                        if (ys>=ylimit) ys = ylimit2;
                        pixels[index++] = (short)(getInterpolatedPixel(xs, ys, pixels2)+0.5);
                    } else {
                        ixs = (int)(xs+0.5);
                        iys = (int)(ys+0.5);
                        if (ixs>=width) ixs = width - 1;
                        if (iys>=height) iys = height -1;
                        pixels[index++] = pixels2[width*iys+ixs];
                    }
                } else
                    pixels[index++] = (short)background;
            }
        }
    }
   

    private double getInterpolatedPixel(double x, double y, short[] pixels) {
        int xbase = (int)x;
        int ybase = (int)y;
        double xFraction = x - xbase;
        double yFraction = y - ybase;
        int offset = ybase * width + xbase;
        int lowerLeft = pixels[offset]&0xffff;
        int lowerRight = pixels[offset + 1]&0xffff;
        int upperRight = pixels[offset + width + 1]&0xffff;
        int upperLeft = pixels[offset + width]&0xffff;
        double upperAverage = upperLeft + xFraction * (upperRight - upperLeft);
        double lowerAverage = lowerLeft + xFraction * (lowerRight - lowerLeft);
        return lowerAverage + yFraction * (upperAverage - lowerAverage);
    }

}
