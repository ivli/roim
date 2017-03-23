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

import com.ivli.roim.core.IFrameProvider;
import com.ivli.roim.core.Uid;
import java.awt.Shape;

/**
 *
 * @author likhachev
 */
public abstract class ScreenObject extends Overlay {   
       
    protected transient IImageView iView;    
    
    private int iFrameNumber = IFrameProvider.INVALID_FRAME;
      
    protected ScreenObject(IImageView aView, int aN) {
       super(Uid.getNext());
       iView = aView;
       iFrameNumber = aN;
    }
    
    protected ScreenObject(IImageView aView, int aN, Shape aShape, String aName) {
       super(Uid.getNext(), aShape, aName);
       iView = aView;
       iFrameNumber = aN;
    }
    
    public IImageView getView() {
        return iView;
    }
        
    public int getFrameNumber() {
        return iFrameNumber;
    }
    
    public boolean isSelectable() {return true;}
    public boolean isMovable() {return true;}
    public boolean isPermanent() {return false;}
    public boolean isCloneable() {return false;}
    public boolean isPinnable() {return true;} 
}
