
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public abstract class IImage {
    public abstract int getWidth();  
    public abstract int getHeight();
    public abstract int getNumFrames();
    abstract public IMultiframeImage image();
    
    abstract void open(String aFile) throws java.io.IOException;      
    abstract ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException;
    
}
