
package com.ivli.roim;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author likhachev
 */
public class DICOMImageProvider implements IImageProvider/* implements IImage*/ {
    private static final boolean LOAD_ON_DEMAND = true;
    
    private final ImageLoader iLoader = new ImageLoader(); 
    private final ArrayList<ImageFrame> iFrames;    
    private TimeSliceVector iTimeSlices;     
        
    private DICOMImageProvider() {
        iFrames = new ArrayList();
    }
    
    static DICOMImageProvider New(final String aFile) throws IOException {
        DICOMImageProvider self = new DICOMImageProvider();
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
    
    public TimeSliceVector getTimeSliceVector() {
        return iTimeSlices;
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
      
    public ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, IOException {
        
        if (anIndex > getNumFrames() || anIndex < 0)
            throw new IndexOutOfBoundsException();
        
        ImageFrame f = null;

        try {
            f = iFrames.get(anIndex); 
            
           
        } catch (IndexOutOfBoundsException ex) {
             try {
                f = new ImageFrame(iLoader.readRaster(anIndex));
                
                iFrames.add(anIndex, f);
                
                logger.info("Frame -" + anIndex +                                   // NOI18N
                            ", MIN"   + f.getStats().getMin() +  // NOI18N
                            ", MAX"   + f.getStats().getMax() +  // NOI18N
                            ", DEN"   + f.getStats().getIden()); // NOI18N     
      
            } catch (IOException ioex) {
                logger.error(ioex); 
            } 
        }
        return f;
    }
    
   /*
    public IMultiframeImage image() {
        return new MultiframeImage(this);
    }
  */
    
    private static final Logger logger = LogManager.getLogger(DICOMImageProvider.class);    
 
}


