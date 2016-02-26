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
import com.ivli.roim.core.IMultiframeImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author likhachev
 */
public class MIPProjector implements Runnable {
    //private int nProj;
    private double  valDepthCorr = .1; //a value must lie between [0 - 1]
    private boolean iDepthCorr = true;
    final IMultiframeImage iImage;
    

    public MIPProjector(IMultiframeImage anImage) {
        iImage = anImage;    
    }

    
    public void project(IMultiframeImage anI, int aProjections) {
        
        
    }
    
    public IMultiframeImage project2(int aProjections) {       
        IMultiframeImage mip = iImage.createCompatibleImage(aProjections);
        
        final double angStep = 360.0 / aProjections;			
        ExecutorService es = Executors.newCachedThreadPool();
        
        try{
            for (int currProj = 0; currProj < aProjections; ++currProj) 
                es.execute(new Projector(iImage, angStep*currProj, mip.get(currProj)));

                //es.shutdown();
            boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);
        } catch(InterruptedException ex) {
            
        }
        return mip;
    }
    
    class Projector implements Runnable {
    
        private IMultiframeImage iSrc;        
        private double iAngle;
        private ImageFrame iRet;
        
        public Projector(IMultiframeImage aS, double anA, ImageFrame aR) {
            iSrc = aS;
            iAngle = anA;
            iRet = aR;
        }
        
        private void project() {
            IMultiframeImage temp = iSrc.duplicate();
            final int nSlices = iImage.getNumFrames();		                
            final int width = iImage.getWidth();
            final int height = iImage.getHeight();

            double minVol = iImage.getMin();
            double maxVol = iImage.getMax();
            ///ImageFrame frm = mip.get(currProj);
            
            /**/
            for (ImageFrame f : temp) {				                        
                FrameProcessor fp = new FrameProcessor(f);                        
                fp.setInterpolate(true);				
                fp.rotate(iAngle);				
            }            
            
            for (int z = 0; z < nSlices; ++z) {
                ImageFrame modifSliceIp = temp.get(z);

                for (int x = 0; x < width; ++x) {
                    double pixSum = 0.0;               
                    double pixMax = 0.0;					

                    for (int y = 0; y < height; ++y) {
                            final double weightFact = ((double)y + 1.0) * valDepthCorr ;					 
                            final int pixVal = Math.max(0, modifSliceIp.getPixel(x, y));					                    

                            if (iDepthCorr) 
                                pixSum = ((double)pixVal / weightFact);      							
                            else
                                pixSum = (double)pixVal;
                           
                            if (pixSum > pixMax)
                                pixMax = pixSum;
                    }

                    final int pixNormVal = (int)((pixMax / maxVol) * 32767.0);

                    iRet.setPixel(width-x-1, z, pixNormVal);
                }
            }			
    
        }
        
        public void run() {
            
        }
    }
    
    public IMultiframeImage project(int aProjections) {
        final int nSlices = iImage.getNumFrames();		                
        final int width = iImage.getWidth();
        final int height = iImage.getHeight();

        double minVol = iImage.getMin();
        double maxVol = iImage.getMax();

        IMultiframeImage mip = iImage.createCompatibleImage(aProjections);
	
        final double angStep = 360.0 / aProjections;			
        //double angCurr = 0.;
        //final int noOfProj = (int) Math.ceil(360. /angStep);
        
        for (int currProj = 0; currProj < aProjections; ++currProj) {
            //progress = ((double)angCurr / 360.0
            //angCurr < 360.0; angCurr += angStep,
            
            IMultiframeImage temp = iImage.duplicate();

            ImageFrame frm = mip.get(currProj);
            
            /**/
            for (ImageFrame f : temp) {				                        
                FrameProcessor fp = new FrameProcessor(f);                        
                fp.setInterpolate(true);				
                fp.rotate(angStep*currProj);				
            }            
            
            for (int z = 0; z < nSlices; ++z) {
                ImageFrame modifSliceIp = temp.get(z);

                for (int x = 0; x < width; ++x) {
                    double pixSum = 0.0;               
                    double pixMax = 0.0;					

                    for (int y = 0; y < height; ++y) {
                            final double weightFact = ((double)y + 1.0) * valDepthCorr ;					 
                            final int pixVal = Math.max(0, modifSliceIp.getPixel(x, y));					                    

                            if (iDepthCorr) 
                                pixSum = ((double)pixVal / weightFact);      							
                            else
                                pixSum = (double)pixVal;
                           
                            if (pixSum > pixMax)
                                pixMax = pixSum;
                    }

                    final int pixNormVal = (int)((pixMax / maxVol) * 32767.0);

                    frm.setPixel(width-x-1, z, pixNormVal);
                }
            }			
        }
        return mip;
    }
    
    @Override
    public void run() {       						
    }    
        
}
