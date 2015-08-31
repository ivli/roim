
package com.ivli.roim;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class DICOMImage /*extends IImageProvider implements IImage*/ {
    private static final boolean LOAD_ON_DEMAND = false;
    
    private final ImageLoader iLoader = new ImageLoader(); 
    private final ArrayList<ImageFrame> iFrames;    
    TimeSliceVector iTimeSlices;     
        
    private DICOMImage() {
        iFrames = new ArrayList();
    }
    
    static DICOMImage New(final String aFile) throws IOException {
        DICOMImage self = new DICOMImage();
        self.open(aFile);
        return self;
    }    
      
    public int getWidth() {
        return iFrames.get(0).getWidth();
    }
    
    public int getHeight() {
        return iFrames.get(0).getHeight();
    }  
    
    public int getNumFrames() throws IOException {
        //try {
            return iLoader.getNumImages();
       // } catch (IOException e) {
        //    return -1;
        //}
    }
    
    public PixelSpacing getPixelSpacing() {
        try{
            return iLoader.getPixelSpacing();
        } catch (IOException ex) {
            logger.debug(ex);
            return new PixelSpacing(1.0, 1.0);
        }
    }
    public void open(String aFile) throws IOException {           
        iLoader.open(aFile);
        iTimeSlices = iLoader.getTimeSliceVector();        
       
        iFrames.clear();
        iFrames.ensureCapacity(getNumFrames());
        
        if (!LOAD_ON_DEMAND) 
            for (int i = 0; i < getNumFrames(); ++i)
                loadFrame(i);
        
        loadFrame(0);
    }
      
    ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, IOException {
        
        if (anIndex > getNumFrames() || anIndex < 0)
            throw new IndexOutOfBoundsException();

        try {
                // load and cache image if it is not yet in cache
            if (anIndex >= iFrames.size() || null == iFrames.get(anIndex))                
                iFrames.add(anIndex, new ImageFrame(iLoader.readRaster(anIndex)));
            
            logger.info("Frame -" + anIndex +                                   // NOI18N
                        ", MIN"   + iFrames.get(anIndex).getStats().getMin() +  // NOI18N
                        ", MAX"   + iFrames.get(anIndex).getStats().getMax() +  // NOI18N
                        ", DEN"   + iFrames.get(anIndex).getStats().getIden()); // NOI18N  
                                      
        } catch (IOException ex) {
            logger.error(ex); 
        } 
        
        return iFrames.get(anIndex);
    }
   
    public IMultiframeImage image() {
        return new MultiframeImage(this);
    }
  
    
    private static final Logger logger = LogManager.getLogger(DICOMImage.class);    
 
}


