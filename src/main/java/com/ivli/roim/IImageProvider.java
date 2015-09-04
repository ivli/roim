/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public interface IImageProvider {
   
    int getWidth();  
    int getHeight();
    int getNumFrames() throws java.io.IOException;
    PixelSpacing getPixelSpacing();
    
   // void open(String aFile) throws java.io.IOException;      
    ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, java.io.IOException;
 
}
