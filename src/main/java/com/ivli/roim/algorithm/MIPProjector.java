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

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageFrame;

/**
 *
 * @author likhachev
 */
public class MIPProjector implements Runnable {
    //private int nProj;
    private double  valDepthCorr = .1; //value must lie between [0 - 1]
    private boolean iDepthCorr = true;
    final IMultiframeImage iImage;
    

    public MIPProjector(IMultiframeImage anImage) {
        iImage = anImage;    
    }

    public IMultiframeImage project(int aProjections) {
        final int nSlices = iImage.getNumFrames();		                
        final int width = iImage.getWidth();
        final int height = iImage.getHeight();

        double minVol = iImage.getMin();
        double maxVol = iImage.getMax();

        IMultiframeImage mip = iImage.createCompatibleImage(aProjections);
	
        final double angStep = 360.0 / aProjections;			
        double angCurr = 0.;

        for (int currProj = 0; angCurr < 360.0; angCurr += angStep, ++currProj) {
            //progress = ((double)angCurr / 360.0
            IMultiframeImage temp = iImage.duplicate();

            ImageFrame frm = mip.get(currProj);
            
            /**/
            for (ImageFrame f : temp) {				                        
                FrameProcessor fp = new FrameProcessor(f);                        
                fp.setInterpolate(true);				
                fp.rotate(angCurr);				
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
