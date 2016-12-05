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

import com.ivli.roim.core.Histogram;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Measure;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Arrays;
/**
 *
 * @author likhachev
 */
public class FrameProcessor {      
    public static final int INTERPOLATION_NONE     = 0;
    public static final int INTERPOLATION_BILINEAR = 1;
    public static final int INTERPOLATION_BICUBIC  = 2;    
    
    private final ImageFrame iFrame;       
    private int iInterpol; 
        
    public FrameProcessor(ImageFrame aF) {
        iFrame = aF;   
        iInterpol = INTERPOLATION_NONE;
    }
    
    public FrameProcessor(ImageFrame aF, int aI) {
        iFrame = aF;   
        iInterpol = aI;
    }
    
    public void setInterpolation(int aI) {
        iInterpol = aI;
    }
       
    protected int[] getPixelsCopy() {
        int[] pixels2 = new int[iFrame.getWidth()*iFrame.getHeight()];
        System.arraycopy(iFrame.getPixelData(), 0, pixels2, 0, iFrame.getWidth()*iFrame.getHeight());
        return pixels2;	
    }
       
    public void add(ImageFrame aF) throws IllegalArgumentException {
        if (iFrame.getWidth() != aF.getWidth() && iFrame.getHeight() != aF.getHeight())
            throw new IllegalArgumentException("Frames must be of identical size"); //NOI18N
        
        for (int i = 0; i < iFrame.getWidth(); ++i)
            for (int j = 0; j < iFrame.getHeight(); ++j) 
                iFrame.set(i, j, iFrame.get(i, j) + aF.get(i, j));        
    }
        
    public void flipVert() {
        final int width  = iFrame.getWidth();
        final int height = iFrame.getHeight() ;
        final int [] buf = iFrame.getPixelData();     
       
        for (int i=0; i < height/2; ++i) {
            final int i1 = width*i;
            final int i2 = width*(height-i-1);
            for(int j=0; j< width; ++j) {
                final int temp = buf[i1+j];
                buf[i1+j] = buf[i2+j];
                buf[i2+j] = temp;                
            }    
        }
    }
    
    public void flipHorz() {
        final int width  = iFrame.getWidth();
        final int height = iFrame.getHeight() ;
        final int [] buf = iFrame.getPixelData();
        
        for (int i=0; i < height; ++i) {
            final int i1 = width*i;
            final int i2 = width*(i+1);
            for(int j=0; j< width/2; ++j) {
                final int temp = buf[i1+j];                
                buf[i1+j] = buf[i2 - j-1];
                buf[i2 - j-1] = temp;
            } 
        }
    }
    
    private double _getInterpolatedPixel(final double aX, final double aY, final int[] aPixels) {
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
    
    private void _rotate(final int[] src, final int [] dst, final int width, final int height, final double anAngle) {        
        final double centerX = (width-1)/2.0;
        final double centerY = (height-1)/2.0;
       
        final double angleRadians = -anAngle/(180.0f/Math.PI);
        final double ca = Math.cos(angleRadians);
        final double sa = Math.sin(angleRadians);
        final double tmp1 = centerY*sa-centerX*ca;
        final double tmp2 = -centerX*sa-centerY*ca;
        
        final double dwidth = width, dheight = height;
        final double xlimit = width-1.0, xlimit2 = width-1.001;
        final double ylimit = height-1.0, ylimit2 = height-1.001;
        
        double tmp3, tmp4, xs, ys;
        int ixs, iys;
        final int background = 0;       
                
        for (int y=0; y<height; ++y) {
            int index = width*y;
            tmp3 = tmp1 - y*sa + centerX;
            tmp4 = tmp2 + y*ca + centerY;
            for (int x=0; x<width; ++x) {
                xs = x*ca + tmp3;
                ys = x*sa + tmp4;
                if ((xs>=-0.01) && (xs<dwidth) && (ys>=-0.01) && (ys<dheight)) {
                    if (INTERPOLATION_BILINEAR == iInterpol) {
                        if (xs<0.0) xs = 0.0;
                        if (xs>=xlimit) xs = xlimit2;
                        if (ys<0.0) ys = 0.0;			
                        if (ys>=ylimit) ys = ylimit2;
                        dst[index++] = (int)(_getInterpolatedPixel(xs, ys, src) + 0.5);
                    } else {
                        ixs = (int)(xs+0.5);
                        iys = (int)(ys+0.5);
                        if (ixs>=width) ixs = width - 1;
                        if (iys>=height) iys = height -1;
                        dst[index++] = src[width*iys+ixs];
                    }
                } else
                    dst[index++] = background;
            }
        }
    }
    
    public void rotate(final double anAngle) {                       
        final int width = iFrame.getWidth();
        final int height = iFrame.getHeight();       
        final int[] src = iFrame.getPixelData(); //source
        final int[] dst = new int[src.length];   //destination
        _rotate(src, dst, width, height, anAngle);
        iFrame.setPixelData(width, height, dst);
    }
  
    public Histogram histogram(Shape aR, Integer aNoOfBins) {             
        final Rectangle r;       
        
        if (null != aR)
            r = aR.getBounds(); 
        else 
            r = new Rectangle(0, 0, iFrame.getWidth(), iFrame.getHeight());          
        
        int min = Integer.MAX_VALUE; 
        int max = Integer.MIN_VALUE;
                
        for (int i = r.x; i < r.x + r.width; ++i) {           
            for (int j = r.y; j < r.y + r.height; ++j) 
                if (null == aR || aR.contains(i,j)) {
                    final int v = iFrame.get(i, j);                      
                    if (v > max) max = v;
                    if (v < min) min = v;
                }
        }                
        
        final int range = max - min + 1;
        final int noOfBins = null != aNoOfBins ? aNoOfBins : range;
        final double step = (double)range / (double)noOfBins;
                
        int bins[] = new int[noOfBins];
        
        for (int i=0; i < bins.length; ++i)
            bins[i] = 0;
        
        for (int i = r.x; i < r.x + r.width; ++i) {                        
            for (int j = r.y; j < r.y + r.height; ++j) 
                if (null == aR || aR.contains(i, j)) {
                    final int bin = (int)Math.floor((double)iFrame.get(i, j) / step);                                      
                    bins[bin] = bins[bin] + 1;                           
                }
        }  
        return new Histogram(bins, step);
    }
    
    public Histogram profile(Shape aR) {
        final Rectangle r;       
        
        if (null != aR)
            r = aR.getBounds(); 
        else 
            r = new Rectangle(0, 0, iFrame.getWidth(), iFrame.getHeight());          
        
        int bins[] = new int[r.width];
              
        Arrays.fill(bins, 0);
        
        for (int i = r.x; i < r.x + r.width; ++i) {    
            int val = 0;
            for (int j = r.y; j < r.y + r.height; ++j) 
                if (null == aR || aR.contains(i, j)) {
                    val += (int)(iFrame.get(i, j));                                              
                }
            bins[i - r.x] = val; 
        }
        
        return new Histogram(bins);    
    }
        
    public long density(Shape aR) {        
        return (long)measure(aR).getIden();    
    } 
    
    public Measure measure(Shape aR) {        
        final Rectangle bounds;        
        
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        long den = 0L;
        
        if (null == aR) {
            bounds = new Rectangle(0, 0, iFrame.getWidth(), iFrame.getHeight());
        } else {
            bounds = aR.getBounds(); 
            if (!new Rectangle(0, 0, iFrame.getWidth(), iFrame.getHeight()).contains(bounds))
                throw new IllegalArgumentException("ROI out of bounds");
        }            
                
        for (int i = bounds.x; i < bounds.x + bounds.width; ++i) {           
            for (int j = bounds.y; j < bounds.y + bounds.height; ++j) 
                if (null == aR || aR.contains(i, j)) {
                    final int v = iFrame.get(i, j);  
                    den += v;  
                    if (v > max) max = v;
                    if (v < min) min = v;
                }
        }        
        return new Measure(min, max, den);    
    }
    
    public static final double NORMAL_KEY = 0.18;
    public static final double LOW_KEY    = 0.09;
    public static final double HIGH_KEY   = 0.72;
    /*
     * luminance mapping
     * aKey value should lie in range 0.09 - 1.0 
     *      0.18 - 0.36 - normal key
     *      0.09 - 0.18 - low key
     *      0.63 - 0.72 - high key
     */
    public void map(double aKey) {
        final double delta = 0.0001;
        
        double L0 = Double.MAX_VALUE;
        double L1 = Double.MIN_VALUE;
        int[] d = iFrame.getPixelData();
        double log = 0;
        double sum = 0;
        for (int i=0;i<d.length; ++i) {
            int p = d[i];
            sum += p;
            log += Math.log(delta + (double)p);
            if (p < L0)
                L0 = p;
            if (p > L1)
                L1 = p;
        }
        
        final double Lw = Math.exp(log) / iFrame.getPixelData().length; //log-average luminance
        
        final double c = aKey/Lw;
        
        for (int i=0;i<d.length;++i) {
            d[i] = (int)((double)d[i] * c) ;
        }
    }
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();        
}
