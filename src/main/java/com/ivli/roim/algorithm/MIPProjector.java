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
import com.ivli.roim.events.IProgressor;
import com.ivli.roim.events.ProgressNotifier;

/**
 *
 * @author likhachev
 */
public class MIPProjector extends ProgressNotifier implements IProgressor {   
    private final double DEPTH_FACTOR = .1; //must fit into range [0 - 1]
   
    private final IMultiframeImage iImage;
    private IMultiframeImage iRet;
    private int iProjections;
    
    
    public ProgressNotifier getNotifier() {
        return this;
    }
    
    public MIPProjector(IMultiframeImage aSrc) {
        iImage = aSrc; 
        iRet = null;     
        iProjections = 128;
    }
     
    public IMultiframeImage project(int aProjections) {
        final int nSlices = iImage.getNumFrames();		                
        final int width   = iImage.getWidth();
        final int height  = iImage.getHeight();

        double minVol = iImage.getMin();
        double maxVol = iImage.getMax();

        IMultiframeImage mip = iImage.createCompatibleImage(aProjections);
	
        final double angStep = 360.0 / aProjections;			
        
        final double weights[] = new double[height]; 
        
        for (int i=0; i < weights.length; ++i)
            weights[i] = (i + 1) * DEPTH_FACTOR;
        
        for (int currProj = 0; currProj < aProjections; ++currProj) {
            notifyProgressChanged((int)(((angStep*currProj)/360.) * 100.));
            
            final ImageFrame frm = mip.get(currProj);            
            IMultiframeImage temp = iImage.duplicate();

            temp.processor().rotate(angStep*currProj);
                    
            for (int z = 0; z < nSlices; ++z) {
                final ImageFrame cur = temp.get(z);

                for (int x = 0; x < width; ++x) {                               
                    double pixMax = .0;		
                    
                    for (int y = 0; y < height; ++y)                        				                         				                    
                        pixMax = Math.max(pixMax, (cur.get(x, y) / weights[y]));							                                

                    frm.set(width-x-1, z, (int)((pixMax / maxVol) * 32767.));
                }
            }			
        }
        
        mip.processor().flipVert();
        
        return mip;
    }
    
    
    private void project(int currProj, int nSlices, int width, int height, double angStep, double maxVol, double []weights, IMultiframeImage mip) {
       // notifyProgressChanged((int)(((angStep*currProj)/360.) * 100.));
            
            final ImageFrame frm = mip.get(currProj);            
            IMultiframeImage temp = iImage.duplicate();

            temp.processor().rotate(angStep*currProj);
                    
            for (int z = 0; z < nSlices; ++z) {
                final ImageFrame cur = temp.get(z);

                for (int x = 0; x < width; ++x) {                               
                    double pixMax = .0;		
                    
                    for (int y = 0; y < height; ++y)                        				                         				                    
                        pixMax = Math.max(pixMax, (cur.get(x, y) / weights[y]));							                                

                    frm.set(width-x-1, z, (int)((pixMax / maxVol) * 32767.));
                }
            }			
    }
    
    public IMultiframeImage getResult() {
        return iRet;
    }
    
    @Override
    public void run() {  
        iRet = project(iProjections);  
        notifyProgressChanged(100);      
    }    

}
