
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class VirtualImageProvider implements IImageProvider{
    IImageProvider iParent;
    java.util.ArrayList<ImageFrame> iFrames;

    public VirtualImageProvider(IImageProvider aP) {
        iParent = aP;
        iFrames = new  java.util.ArrayList<>(); 
    }

    public VirtualImageProvider(IImageProvider aP, ImageFrame aF) {
        iParent = aP;
        iFrames = new java.util.ArrayList<>(); 
        iFrames.add(aF);
    }
    
    public void addFrame(ImageFrame aF) {iFrames.add(aF);}

    public int getWidth() {return iParent.getWidth();} 
    public int getHeight() {return iParent.getHeight();}
    public int getNumFrames() throws java.io.IOException {return iFrames.size();}
    public PixelSpacing getPixelSpacing() {return iParent.getPixelSpacing();}

    public ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException/*, java.io.IOException */{
        return iFrames.get(anIndex);
    }
       
}
