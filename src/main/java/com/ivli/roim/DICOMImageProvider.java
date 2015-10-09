
package com.ivli.roim;

import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.core.TimeSlice;
import com.ivli.roim.core.PixelSpacing;
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
    private static final int     FIRST_FRAME_TO_LOAD = 0;
    
    private final ImageLoader iLoader;// = new ImageLoader(); 
    private final ArrayList<ImageFrame> iFrames;    
    private TimeSliceVector iTimeSlices;     
    private int iWidth;
    private int iHeight;
    
    private DICOMImageProvider() {
        iLoader = new ImageLoader(); 
        iFrames = new ArrayList();
    }
    
    static DICOMImageProvider New(final String aFile) throws IOException {
        DICOMImageProvider self = new DICOMImageProvider();
        self.open(aFile);
        return self;
    }    
      
    public int getWidth() {
        return iWidth;
    }
    
    public int getHeight() {
        return iHeight;
    }  
    
    public int getNumFrames() throws IOException {       
        return iLoader.getNumImages();    
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
        
        ImageFrame f = loadFrame(FIRST_FRAME_TO_LOAD);
        
        iWidth = f.getWidth();
        iHeight = f.getHeight();
    }
      
    
    private ImageFrame loadFrame(int anIndex) throws IndexOutOfBoundsException, IOException {
        
        if (anIndex > getNumFrames() || anIndex < 0)
            throw new IndexOutOfBoundsException();
        
        ImageFrame f = null;

        try {
            f = iFrames.get(anIndex); 
            
           
        } catch (IndexOutOfBoundsException ex) {
             //try { 
                f = new ImageFrame(iLoader.readRaster(anIndex));
                
                iFrames.add(anIndex, f);
                 //record only cache misses
                logger.info("Frame -" + anIndex +                                  
                            ", MIN"   + f.getMin() +  // NOI18N
                            ", MAX"   + f.getMax() +  // NOI18N
                            ", DEN"   + f.getIden()); // NOI18N     
      
            //} catch (IOException ioex) {
            //    logger.error(ioex); 
            //} 
        }
        return f;
    }
    
  
    public ImageFrame frame(int anIndex) throws IndexOutOfBoundsException, IOException {
        return loadFrame(anIndex);
    }
    
    public IImageProvider slice(TimeSlice aS) {
        VirtualImageProvider ret = new VirtualImageProvider(this);
        
        return ret;
    }
    
    public IImageProvider collapse(TimeSlice aS) throws IOException {   
        int frameTo = (-1 == aS.getTo()) ? getNumFrames() : iTimeSlices.frameNumber(aS.getTo());
        int frameFrom = iTimeSlices.frameNumber(aS.getFrom());        
       
        assert (aS.getFrom() >= 0 && aS.getFrom() < getNumFrames() || aS.getTo() > aS.getFrom() || aS.getFrom() < getNumFrames());  
        
        VirtualImageProvider ret = new VirtualImageProvider(this);
        
        ret.iTimeSlices = iTimeSlices.slice(aS);
        
        java.awt.image.WritableRaster comp = iFrames.get(0).getRaster().createCompatibleWritableRaster();
                
        for (int n = frameFrom; n < frameTo; ++n) {
            final java.awt.image.Raster r = iFrames.get(n).getRaster();
            for (int i = 0; i < getWidth(); ++i)
               for (int j = 0; j < getHeight(); ++j) 
                   comp.setSample(i, j, 0, comp.getSample(i, j, 0) + r.getSample(i, j, 0));           
        }
        
        
        return ret; 
    }
    
    private static final Logger logger = LogManager.getLogger(DICOMImageProvider.class);    
 
}


