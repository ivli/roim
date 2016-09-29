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
package com.ivli.roim.view;

import com.ivli.roim.core.IMultiframeImage;
import com.ivli.roim.core.ImageType;
import com.ivli.roim.core.Uid;
import com.ivli.roim.events.ROIChangeListener;
import java.util.ArrayList;

/**
 *
 * @author likhachev
 */
public class ImageViewGroup {    
    IMultiframeImage iImage;
    ROIManager iMgr;
    ArrayList<IImageView> iViews;
    final Uid iUid;
    
    ImageViewGroup(IMultiframeImage anImage) {
        iImage = anImage;
        iViews = new ArrayList<>();
        iUid = new Uid();
        iMgr = new ROIManager(iImage, iUid, iImage.getImageType() == ImageType.STATIC);        
    }
    
    public ROIManager getROIMgr() {
        return iMgr;
    }
    
    public static ImageViewGroup create(IMultiframeImage anImage) {
        ImageViewGroup ret = new ImageViewGroup(anImage);        
        return ret;
    }
    
    public ImageView createView(ImageView.ViewMode aMode) {
        ImageView ret = ImageView.create(iImage, aMode, iMgr);
        iViews.add(ret);
        return ret;
    }   
    
    public ImageView createGridView() {
        ImageView ret = GridImageView.create(iImage, 4, 4, iMgr);
        iViews.add(ret);
        return ret;
    }   
    
    public void addROIChangeListener(ROIChangeListener aL) {        
        iMgr.addROIChangeListener(aL);
    }
    
    public void removeROIChangeListener(ROIChangeListener aL) {        
        iMgr.removeROIChangeListener(aL);
    }
}
