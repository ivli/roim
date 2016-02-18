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
import com.ivli.roim.core.MultiframeImage;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.ImageFrame;

/**
 *
 * @author likhachev
 */
public class MIPProjector implements Runnable {
    private Integer nProj;
    private Double  valDepthCorr = .1; //must have value between [0 - 1]
    private boolean iDepthCorr = true;
    final IMultiframeImage img;
          IMultiframeImage projImg;

    public MIPProjector(IMultiframeImage anImage, Integer aProjections) {
        img = anImage;
        nProj = aProjections;
    }

    @Override
    public void run() {       						
        final int nSlices = img.getNumFrames();		                
        final int width  = img.getWidth();
        final int height = img.getHeight();

        double minVol = img.getMin();
        double maxVol = img.getMax();

        final int imageType = img.get(0).getRaster().getDataBuffer().getDataType();

        projImg = img.createCompatibleImage(nProj);

        //final ImageStack projStack = projImg.getStack();		

        final double angStep = 360.0 / nProj;			
        double angCurr = 0.;

        for (int currProj = 1; angCurr < 360.0; angCurr += angStep, currProj++) {

            //IJ.showStatus("Building projection # " + currProj + " ...");
            //IJ.showProgress((double)angCurr / 360.0);

            //ImageProcessor projSliceIp = projStack.getProcessor(currProj);

            IMultiframeImage tempStack = img.duplicate();

            for (int sectCurr = 0; sectCurr < nSlices; sectCurr++) {				                        
                FrameProcessor fp = new FrameProcessor(tempStack.get(sectCurr));                        
                fp.setInterpolate(true);				
                fp.rotate(angCurr);				
            }
          for (int z = 0; z < nSlices; z++) {
                ImageFrame modifSliceIp = tempStack.get(z);

                for (int x = 0; x < width; x++) {
                    double pixSum = 0.0;               
                    double pixMax = 0.0;					

                    for (int y = 0; y < height; y++) {
                            final double weightFact = ((double)y + 1.0) * valDepthCorr ;					 
                            final int pixVal = Math.max(0, modifSliceIp.getPixel(x, y));					                    

                            if (img.getImageDataType() == ImageDataType.GRAYS16) {
                                    if (iDepthCorr) 
                                            pixSum = ((double)pixVal / weightFact);      							
                                    else
                                            pixSum = (double)pixVal;
                            } else {						
                                    if (iDepthCorr)
                                            pixSum = ((double)(0xff & pixVal) / weightFact);
                                    else
                                            pixSum = (double)(0xff & pixVal);
                            }

                            if (pixSum > pixMax)
                                    pixMax = pixSum;
                    }

                    final short pixNormVal = (short)((pixMax / maxVol) * 32767.0);

                    projImg.get(currProj).setPixel(width-x-1, z-1, pixNormVal);
                }
            }			
        }
    }
        
}
