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
import com.ivli.roim.io.LutReader;
import java.awt.image.BufferedImage;

/**
 *
 * @author likhachev
 */
public class LUTTransform implements ImageTransform {    
    final int [][] iRGBBuffer;
    
    public LUTTransform(){
        iRGBBuffer = new int[LutReader.TABLE_SIZE][3];
    }
    
    static public LUTTransform create(String aName) {
        LUTTransform self = new LUTTransform();
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
     
    public int[] asArray(int [] anArray) {     
        int []ret;
        
        if (null == anArray)
            ret = new int [LutReader.TABLE_SIZE];
        else
            ret = anArray;
        
        for (int i=0; i < iRGBBuffer.length; ++i)             
            ret[i] = (0xff & (int)iRGBBuffer[i][0]) << 16 | (0xff & (int)iRGBBuffer[i][1]) << 8 | 0xff & (int)iRGBBuffer[i][2];
       
        return ret;
    }

    @Override
    public BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } 
}
