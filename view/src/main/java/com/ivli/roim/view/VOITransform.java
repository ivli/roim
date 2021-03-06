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

import com.ivli.roim.core.Curve;
import com.ivli.roim.core.ModalityTransform;
import com.ivli.roim.core.Window;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VOITransform implements ImageTransform {        
    private static final double LUT_MIN   = .0;
    private static final double LUT_MAX   = 255.;
    private static final double LUT_RANGE = LUT_MAX - LUT_MIN;    
    private static final int GREYSCALES_MIN = 0;
    private static final int GREYSCALES_MAX = 255;
    private static final int IMAGESPACE_SIZE = 65536;
    private static final int LUT_SIZE = 256;        
    private static final double SIGMOID_SKEW = -8;
    
    private ModalityTransform iPVt;
    private LUTTransform iPlut;
    
    private Window  iWin;    
    private boolean iInverted;            
    private boolean iLinear; 
    
    private final byte []iBuffer; // W/L LUT   
    
    private int []iRGBBuffer; // Presentation LUT
    
    static ForkJoinPool iPool = new ForkJoinPool();
        
    public VOITransform() {
        iInverted = false;
        iLinear = true;
        iPVt = ModalityTransform.DEFAULT;                     
        iPlut = LUTTransform.create(null);
        iWin = new Window(0, IMAGESPACE_SIZE);           
        iBuffer = new byte[IMAGESPACE_SIZE];  
        iRGBBuffer = iPlut.asArray(null);
    }  
     
    public VOITransform(ModalityTransform aPVT, Window aWin, LUTTransform aLUT) {
        iInverted = false;
        iLinear = true;
        iPVt = aPVT;
               
        iPlut = (null != aLUT) ? aLUT : LUTTransform.create(null);
        iWin = aWin;  
                 
        iBuffer = new byte[IMAGESPACE_SIZE];
        iRGBBuffer = iPlut.asArray(null);
    }  
   
    public void setTransform(ModalityTransform aT) {
        if (null != aT)
            iPVt = aT;
        makeLUT();
    }
    
    public final void setLUT(LUTTransform aLUT) { 
        iPlut = (null != aLUT) ? aLUT : LUTTransform.create(null);
        makeLUT();
    }
        
    public void setWindow(Window aW) {              
        iWin = aW;    
        makeLUT();
    }
    
    public Window getWindow() {
        return iWin;
    }
    
    public void setInverted(boolean aI) {       
        iInverted = aI;    
        makeLUT();   
    }
    
    public boolean isInverted() {
        return iInverted;
    }
    
    public void setLinear(boolean aL) {
        iLinear = aL;                    
        makeLUT();            
    }

    public boolean isLinear() {
        return iLinear;
    }
            
    class Transformer extends RecursiveAction {       
         //images having lees pixels than this value will not be processed in multiple threads 
        private static final int iThreshold = 4096*1200;
       
        int[] iSrc;
        int[] iDst;
        int iStart;
        int iLength;       
        
        Transformer (int[] aSrc, int[] aDst, int aStart, int aLength) {
            iSrc = aSrc;
            iDst = aDst;
            iStart = aStart;
            iLength = aLength;
        }
                
        protected void computeDirectly() {          
            for (int i = iStart; i < iStart + iLength; ++i)                         
                iDst[i] = iRGBBuffer[0xff & (int)iBuffer[iSrc[i]]];            
        }
        
        @Override
        public void compute() {
            if (iLength < iThreshold) {
                computeDirectly();
                return;
            }

            int split = iLength / 2;

            invokeAll(new Transformer(iSrc, iDst, iStart, split),
                      new Transformer(iSrc, iDst,  iStart + split, iLength - split 
                      ));        
        }
    }   
                  
    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {        
        final int width = aSrc.getWidth();
        final int height = aSrc.getHeight();   
        
        BufferedImage ret;
        
        if (aDst == null) {
            ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); 
        } else {
            if(aDst.getWidth() != width || aDst.getHeight() != height)
                throw new IllegalArgumentException("Destination image must either be an image of the same dimentions or null");
            if (aDst.getType() != BufferedImage.TYPE_INT_RGB)
                throw new IllegalArgumentException("Destination image must be of TYPE_INT_RGB"); 
             ret = aDst;
        } 
        
        final int[] src = (int[])aSrc.getData().getDataElements(0, 0, width, height, null);                   
        final int[] dst = new int[src.length];        
        
        for (int i = 0; i < src.length; ++i)                         
            dst[i] = iRGBBuffer[0x0ff & (int)iBuffer[0x0ffff & src[i]]];   
        
        ret.getRaster().setDataElements(0, 0, width, height, dst);
        
        return ret;
    }
       
    private void makeLUT() {           
        final double BOT = iWin.getBottom();
        final double TOP = iWin.getTop();
                 
        for (int i = 0; i < iBuffer.length; ++i) {          
            final double PV = iPVt.transform(i);
            
            if (!isLinear()){    
                iBuffer[i] = (byte)(int)(LUT_RANGE /(1 + Math.exp(SIGMOID_SKEW * (PV - iWin.getLevel()) / (iWin.getWidth()))) + LUT_MIN);
            } else {    
                int V; 
                if (PV <= BOT) 
                    V = GREYSCALES_MIN;
                else if (PV > TOP) 
                    V = GREYSCALES_MAX;
                else  
                    V = (int)(LUT_RANGE *((PV - iWin.getLevel()) / iWin.getWidth() + .5) + LUT_MIN);
               
                if (!isInverted())                
                    iBuffer[i] = (byte)V;  
                else
                    iBuffer[i] = (byte)(GREYSCALES_MAX - V);                                                                                                   
            }   
        }
        iRGBBuffer = iPlut.asArray(iRGBBuffer);
    }       
    
    public Curve getLUTCurve() {        
        Curve ret = new Curve();        
         
        for (int i = 0; i < iBuffer.length; ++i)
            ret.add(i, (iBuffer[i] & 0xFF));
                
        return ret;
    }
    
    private static final Logger LOG = LogManager.getLogger();
} 
