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
public abstract class IImageProvider {
   
    public abstract int getWidth();  
    public abstract int getHeight();
    public abstract int getNumFrames() throws java.io.IOException;
    abstract public IMultiframeImage image();
    
    abstract void open(String aFile) throws java.io.IOException;      
    abstract ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, java.io.IOException;
    

}
