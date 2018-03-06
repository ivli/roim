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

import java.util.ServiceLoader;
import com.ivli.roim.io.spi.IImageService;
import com.ivli.roim.core.IImage;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 *
 * @author likhachev
 */
public class ImageServiceProvider {

    static ImageServiceProvider service;

    private final ServiceLoader<IImageService> loader;	
    
    public static synchronized ImageServiceProvider getInstance() {
        if (service == null) {
            service = new ImageServiceProvider();
        }
        return service;
    }
     
    private ImageServiceProvider() {
        loader = ServiceLoader.load(IImageService.class);
    }
                
    /**
     * returns service object for given type of image file
     * @param aFileExt file extension aka MIME type  such as DICOM, DCM, BMP etc  
     * @return service object or null if none installed
     */
    public IImageService service(final String aFileExt) {      
        for (IImageService spi:loader) 
            for (String s:spi.getSuffixes())  
                if (aFileExt.equalsIgnoreCase(s))                     				
                    return spi;
    
        return null;
    }	
    
    
    public ArrayList<SimpleEntry<String, String[]>> getInstalledServices() {    
        ArrayList<SimpleEntry<String, String[]>> ret = new ArrayList<>();
        for (IImageService spi:loader) 
            ret.add(new SimpleEntry<>(spi.getDescription(), spi.getSuffixes()));
        
        return ret;
    }
            
    private String dumpFileInformation(final IImage img) {        
        StringBuilder sb = new StringBuilder();
		                 
        sb.append("\nMODALITY: ");
        sb.append(img.getModality());
        sb.append("\nTYPE: ");
        sb.append(img.getImageType());        
        sb.append("\nPHOTOMETRIC INTERPRETATION: ");
        sb.append(img.getPhotometric());
        sb.append(String.format("\nFRAMES:%d; WIDTH:%d; HEIGHT:%d", img.getNumFrames(), img.getWidth(), img.getHeight()));
        sb.append("\nTIMESLICE VECTOR:");
        sb.append(img.getTimeSliceVector());
        sb.append("\nP-VALUE TRANSFORM:");
        sb.append(img.getModalityTransform());                
        sb.append("\nmin/max:");
        sb.append(img.getMin());
        sb.append(img.getMax());
        
        return sb.substring(0);
    }
	
    /*
    //TODO: implement here a logic of finding a provider according to image file type (file extension???) 
    public static IImageProvider create(final String aFullPath, ProgressListener aPL) throws IOException {
        IImageProvider ret; 
        
        if (new File(aFullPath).isDirectory()) {            
            //ret = DirectoryBuilder.build(aFullPath, aPL);
		    ret = null;      ///TODO:!!!!!
        } else { 
            ret = getInstance().getService(aFullPath);        
        }

        if (null !=ret)
            org.apache.logging.log4j.LogManager.getLogger().info(ret.dumpFileInformation());
        else
            org.apache.logging.log4j.LogManager.getLogger().error("SPI not found for file: " + aFullPath);
        return ret;
    }
   */    
}
