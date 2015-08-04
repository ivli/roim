/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author likhachev
 */
public abstract class ImageBase  {
    public abstract RoiStats getImageStats();
    public abstract BufferedImage getBufferedImage();
    
   
    public abstract void   loadFrame(int anIndex) throws IndexOutOfBoundsException;
    public abstract void   open(String aFile) throws IOException;   
    public          int    getNoOfFrames() throws IOException {return iLoader.getNumImages();}
    public abstract int    getWidth();    
    public abstract int    getHeight(); 
   
    public abstract double getMinimum();
    public abstract double getMaximum();
    public abstract double getDensity();
    
    
    public abstract void extract(Extractor anExtractor);
    protected ImageLoader iLoader = new ImageLoader();
    
    protected ImageBase(){}
}
