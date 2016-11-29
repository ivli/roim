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
package com.ivli.roim.io;

import com.ivli.roim.core.ImageDataType;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Modality;
import com.ivli.roim.core.PValueTransform;
import com.ivli.roim.core.Photometric;
import com.ivli.roim.core.PixelSpacing;
import com.ivli.roim.core.SliceSpacing;
import com.ivli.roim.core.TimeSliceVector;
import com.ivli.roim.events.ProgressEvent;
import com.ivli.roim.events.ProgressListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 *
 * @author likhachev
 */
public class DirectoryBuilder  {
    private ProgressListener iPL;
    
    public DirectoryBuilder(ProgressListener aPL) {
        iPL = aPL;
    }
                
    public IImageProvider build(final String aDir, Modality aM) throws IOException {
        if (null != iPL)
            iPL.ProgressChanged(new ProgressEvent(this, 0));
        
        File dir = new File(aDir); 
        
        if (!dir.isDirectory()) {
            throw new UnsupportedOperationException("Not supported yet.");        
        }
      
        File[] lst = dir.listFiles();
        ArrayList<String> sa = new ArrayList<>();
        
        int step = 0;
       
        for (File fi:lst) {         
            try{
                DCMImageProvider dc = new DCMImageProvider(fi);

                if (null == aM) 
                    aM = dc.getModality();

                if (null != iPL) 
                    iPL.ProgressChanged(new ProgressEvent(this, (int)((double)step++ * 100./(double)lst.length))); //ugly but workin'            

                if (dc.getModality() == aM)
                    sa.add(fi.getAbsolutePath());
                
            } catch (IOException ex){
               
                if (ex.getMessage().equals("Not a DICOM Stream"))                 
                    LOG.info("skipping file (not a DICOM): " + fi.getAbsolutePath());
                 else
                    LOG.catching(ex);
            }            
        }
        /* sort files semantically to avoid following situation          
         * 1.3.46.670589.28.2.15.4.9169.24064.3.1400.109.1205337200.dcm
         * 1.3.46.670589.28.2.15.4.9169.24064.3.1400.11.1205337200.dcm         
         */
        sa.sort((l, r) -> {if (l.length() != r.length())
                               return l.length() - r.length();
                           else
                               return l.compareToIgnoreCase(r);
                           });
        
        LOG.debug("filelist: " + sa.stream().map(e -> e + "\n").collect(Collectors.toList()));
                
        IImageProvider ret = new IImageProvider() {
            final int iNoOfFrames = sa.size();
            
            DCMImageProvider dc = new DCMImageProvider(sa.get(0));
            
            public String getFileName() {
                return aDir;
            }
            
            public String dumpFileInformation() {
                StringBuilder sb = new StringBuilder();
                sb.append("\n-----------------------------------");
                sb.append("\nFILE: ");
                sb.append(getFileName());       
                sb.append("\nMODALITY: ");
                sb.append(getModality());
                sb.append("\nTYPE: ");
                sb.append(getImageType());        
                sb.append("\nPHOTOMETRIC INTERPRETATION: ");
                sb.append(getPhotometric());
                sb.append(String.format("\nFRAMES:%d; WIDTH:%d; HEIGHT:%d", getNumFrames(), getWidth(), getHeight()));
                sb.append("\nTIMESLICE VECTOR:");
                sb.append(getTimeSliceVector());
                sb.append("\nP-VALUE TRANSFORM:");
                sb.append(getRescaleTransform());                
                sb.append("\nmin/max:");
                sb.append(getMin());
                sb.append(getMax());
                sb.append("\n-----------------------------------\n");
                return sb.substring(0);
            }
            
            public Photometric getPhotometric() {
                return dc.getPhotometric();
            }
   
            public Modality getModality() {
                return dc.getModality();
            }
            
            @Override
            public int[] readFrame(int anIndex, int[] aBuffer) throws IndexOutOfBoundsException, IOException {
                dc = new DCMImageProvider(sa.get(anIndex));
                return dc.readFrame(0, aBuffer);
            }

            @Override
            public int getWidth() {
                return dc.getWidth();
            }

            @Override
            public int getHeight() {
                return dc.getHeight();
            }

            @Override
            public int getNumFrames() {
                return iNoOfFrames;
            }

            @Override
            public ImageDataType getImageDataType() {
                return dc.getImageDataType();
            }

            @Override
            public ImageType getImageType() {
                return dc.getImageType();
            }

            @Override
            public PixelSpacing getPixelSpacing() {
                return dc.getPixelSpacing();
            }

            @Override
            public SliceSpacing getSliceSpacing() {
                return dc.getSliceSpacing();
            }

            @Override
            public TimeSliceVector getTimeSliceVector() {
                return dc.getTimeSliceVector();
            }

            @Override
            public double getMin() {
                return dc.getMin();
            }

            @Override
            public double getMax() {
                return dc.getMax();
            }

            @Override
            public PValueTransform getRescaleTransform() {
                return dc.getRescaleTransform();
            }

        };
        if (null != iPL)
            iPL.ProgressChanged(new ProgressEvent(this, 100));
        return ret;
    }
    
     private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
