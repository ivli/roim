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

import com.ivli.roim.core.Histogram;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JMenuItem;

/**
 *
 * @author likhachev
 */
public class Profile extends Ruler {      
    private boolean    iShow = true;    
    private boolean    iNormalize = false;
    //private Histogram  iHist;
   // private transient ImageFrame iFrame;
      
    Profile(IImageView aV, Handle aB, Handle aE) {
        super(aV, aB, aE); //NOI18N     
       // iFrame = aV.getImage().get(aV.getFrameNumber());        
       // iHist = iFrame.processor().profile(iShape.getBounds()); 
    }
    
    @Override
    public int getStyles() {
        return OVL_VISIBLE|OVL_MOVEABLE|OVL_SELECTABLE|OVL_PINNABLE|OVL_HAVE_MENU|OVL_HAVE_CONFIG; 
    }
    /* */
    @Override
    public void paint(IPainter aP) {
        aP.paint(this);
        aP.paint(this.iBegin);
        aP.paint(this.iEnd);
    } 
   
    @Override
    public void update(OverlayManager aM) {        
       // iHist = iView.getImage().get(iView.getFrameNumber()).processor().profile(iShape);      
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
        // Point p1 = this.iBegin.iPos
        // Point p2 = this.iEnd.iPos
        
        return iView.getImage().get(iView.getFrameNumber()).processor().profile(this.iBegin.iPos, this.iEnd.iPos, 1.);
    } 
         
    private static final String CUST_COMMAND_PROFILE_TOGGLE_SHOW = "CUST_COMMAND_PROFILE_TOGGLE_SHOW";
    
    @Override
    public ArrayList<JMenuItem> makeCustomMenu(Object aVoidStar) {
        JMenuItem ret = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.PROFILE_SHOW"));
        //mi11.addActionListener(this);
        ret.setActionCommand(CUST_COMMAND_PROFILE_TOGGLE_SHOW);
        //aMenu.add(mi11);    
        return new ArrayList<JMenuItem>(){{add(ret);}};
    }    
    
    @Override
    public boolean handleCustomCommand(final String aCommand){
        if (aCommand == CUST_COMMAND_PROFILE_TOGGLE_SHOW) {
            iShow = !iShow;
            return true;
        }
        return false;
    }
}
