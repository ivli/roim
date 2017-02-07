/*
 * Copyright (C) 2015 likhachev
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

import java.awt.geom.Rectangle2D;

import com.ivli.roim.core.Histogram;
import com.ivli.roim.core.ISeries;
import com.ivli.roim.core.ISeriesProvider;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Measurement;
import com.ivli.roim.core.Scalar;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author likhachev
 */
public class Profile extends ScreenObject implements Overlay.IHaveCustomMenu {      
    private boolean    iShow = true;    
    private boolean    iNormalize = false;
    private Histogram  iHist;
    private transient ImageFrame iFrame;
    private int iFrameNumber;
    
    
    public Profile(Rectangle2D aS, IImageView aV) {
        super(aV, aS, "PROFILE"); //NOI18N 
        iFrameNumber = aV.getFrameNumber();
        iFrame = aV.getFrame();        
        iHist = iFrame.processor().profile(iShape.getBounds()); 
    }
          
    @Override
    public void paint(AbstractPainter aP) {
        if (aP.getView() == getView())
        aP.paint(this);
    } 
 
    @Override
    public void update(OverlayManager aM) {        
        iHist = iFrame.processor().profile(iShape.getBounds());      
    }            
    
    public int getFrameNumber() {
        return iFrameNumber;
    }
    
    public boolean normalize() {
        return iNormalize = !iNormalize; 
    }
   
    public boolean isShowHistogram() {
        return iShow; 
    }
    
    public void showHistogram(boolean aS) {
         iShow = aS; 
    }
    
    public Histogram getHistogram() {
         return iHist;
    } 
    
     
    static final String CUST_COMMAND_PROFILE_TOGGLE_SHOW = "CUST_COMMAND_PROFILE_TOGGLE_SHOW";
    
    public ArrayList<JMenuItem> makeCustomMenu(Object aVoidStar) {
        JMenuItem ret = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.PROFILE_SHOW"));
        //mi11.addActionListener(this);
        ret.setActionCommand(CUST_COMMAND_PROFILE_TOGGLE_SHOW);
        //aMenu.add(mi11);    
        return new ArrayList<JMenuItem>(){{add(ret);}};
    }    
    
    public boolean handleCustomCommand(final String aCommand){
        if (aCommand == CUST_COMMAND_PROFILE_TOGGLE_SHOW) {
            iShow = !iShow;
            return true;
        }
        return false;
    }

  
}
