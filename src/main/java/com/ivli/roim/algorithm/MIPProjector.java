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
import com.ivli.roim.events.ProgressNotifier;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import com.ivli.roim.core.MultiframeImage;
import com.ivli.roim.events.ProgressListener;
import java.util.stream.IntStream;

/**
 *
 * @author likhachev
 */
public class MIPProjector extends ProgressNotifier {   
    private final double DEPTH_FACTOR = .1; //real number in range [0 - 1]
    
    private final IMultiframeImage iImage;
            
    public MIPProjector(IMultiframeImage aSrc, ProgressListener aPL) {
        if (null == aSrc)
            throw new IllegalArgumentException("aSrc may not be null");       
        iImage = aSrc;         
        
        if (null != aPL)
            addProgressListener(aPL);
    }
    
    private static boolean PROJECTOR_USE_PARALLEL = false;
    static int progress = 0;
    
    protected void notifyProgressChanged(int aProgress) {
        
        if (!PROJECTOR_USE_PARALLEL)
            super.notifyProgressChanged(aProgress);
        else {
           super.notifyProgressChanged(Math.min(100, progress++)); 
        }        
    }
    
    public IMultiframeImage project(Integer aProjections) {
        
        
        if (PROJECTOR_USE_PARALLEL) {
            ParallelProjector p = new ParallelProjector(aProjections);
            progress = 0;
            p.exec();
            return p.mip;    
        } else {
            return projectSequential(aProjections);
        }        
    }
    
    private IMultiframeImage projectSequential(Integer aProjections) {
        if (null == aProjections)
            aProjections = iImage.getNumFrames();
        if (aProjections <= 0)
            throw new IllegalArgumentException("number of projections must be a natural number");
        		                
        final int width  = iImage.getWidth();
        final int height = iImage.getHeight();      
        final int depth  = iImage.getNumFrames();
        
        final double maxVol = iImage.processor().measure(null).getMax();       	
        final double angStep = 360.0 / aProjections;			
        
        final double weights[] = new double[height]; 
        
        for (int i=0; i < weights.length; ++i)
            weights[i] = (i + 1) * DEPTH_FACTOR;
        
        IMultiframeImage mip = iImage.createCompatibleImage(width, depth, aProjections);
                
        for (int cp = 0; cp < aProjections; ++cp) {
            notifyProgressChanged((int)(((angStep*cp)/360.) * 100.));
            
            final ImageFrame frm = mip.get(cp);            
            IMultiframeImage temp = iImage.duplicate();

            temp.processor().rotate(angStep*cp);
                    
            for (int z = 0; z < depth; ++z) {
                final ImageFrame cur = temp.get(z);
                               
                for (int x = 0; x < width; ++x) {                               
                    double pixMax = .0;		
                    
                    for (int y = 0; y < height; ++y)                        				                         				                    
                        pixMax = Math.max(pixMax, (cur.get(x, y) / weights[y]));							                                

                    frm.setPixel(width-x-1, z, (int)((pixMax / maxVol) * 32767.));
                }
            }            
        }
        
        //mip.processor().flipVert();      
        notifyProgressChanged(100);
        return mip;
    }
 
    class ParallelProjector {          
        final int iProjections;
        final double maxVol;      	
        final double angStep ;
        final double weights[];
        final IMultiframeImage mip;
   
        ParallelProjector(int aProjections) {            
            iProjections = aProjections;
            maxVol  = iImage.processor().measure(null).getMax();       	
            angStep = 360.0 / aProjections;	
            weights = new double[iImage.getHeight()];
            
            for (int i=0; i < weights.length; ++i)
                weights[i] = (i + 1) * DEPTH_FACTOR;
            
            mip = iImage.createCompatibleImage(aProjections);
        }
        
        public void exec(){
            IntStream.range(0, iProjections)
                     .parallel()
                     .forEach(i -> projectOne(i)); 
            mip.processor().flipVert();      
            notifyProgressChanged(100);
        }
        
        private void projectOne(int currProj) {        
            final ImageFrame frm = mip.get(currProj);            
            final IMultiframeImage temp = iImage.duplicate();
            final int width = iImage.getWidth();
            final int height = iImage.getHeight();
            
            temp.processor().rotate(angStep*currProj);
            
            notifyProgressChanged((int)(((angStep*currProj)/360.) * 100.));
            
            for (int z = 0; z < iImage.getNumFrames(); ++z) {
                final ImageFrame cur = temp.get(z);

                for (int x = 0; x < width; ++x) {                               
                    double pixMax = .0;		
                    
                    for (int y = 0; y < height; ++y)                        				                         				                    
                        pixMax = Math.max(pixMax, (cur.get(x, y) / weights[y]));							                                

                    frm.set(width-x-1, z, (int)((pixMax / maxVol) * 32767.));
                }
            }            
        }   
        
    }  
    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
