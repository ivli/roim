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
package com.ivli.roim.core;
import com.ivli.roim.io.LutReader;
/**
 *
 * @author likhachev
 */
public class PresentationLUT {
    
    private final int [][] iRGBBuffer;
    
    PresentationLUT(){
        iRGBBuffer = new int[LutReader.TABLE_SIZE][3];
    }
    
    static public PresentationLUT create(String aName) {
        PresentationLUT self = new PresentationLUT();
        self.open(aName);
        return self;
    }
            
    public void open(String aName) {
        java.awt.image.IndexColorModel mdl;
    
        if (null == (mdl = LutReader.open(aName)))
            mdl = LutReader.defaultLUT();            
       
        byte reds[] = new byte[LutReader.TABLE_SIZE];
        byte greens[] = new byte[LutReader.TABLE_SIZE];
        byte blues[] = new byte[LutReader.TABLE_SIZE];
        
        mdl.getReds(reds);
        mdl.getGreens(greens);
        mdl.getBlues(blues);    
        
        for (int i = 0; i < LutReader.TABLE_SIZE; ++i) {
            iRGBBuffer[i][0] = (int)(reds[i]); 
            iRGBBuffer[i][1] = (int)(greens[i]);
            iRGBBuffer[i][2] = (int)(blues[i]);
        }
    }
    
    public int[] translate(int aNdx) {
        return iRGBBuffer[aNdx];
    }
    
}
