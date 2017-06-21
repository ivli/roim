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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author likhachev
 */
class GridViewController extends Controller {  
    static final String KCommandShowFrameNumbers = "MNU_CONTEXT_GRIDVIEW.SHOW_FRAME_NUMBERS"; // NOI18N
    static final String KCommandOptimalLayout = "MNU_CONTEXT_GRIDVIEW.LAYOUT_OPTIMAL"; // NOI18N
    
    GridViewController(GridImageView aI) {
        super(aI);
    }

    @Override
    JPopupMenu buildContextPopupMenu() {      
        JPopupMenu mnu = new JPopupMenu("MNU_CONTEXT_GRIDVIEW"); 
        {                   
            {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_CONTEXT_GRIDVIEW.LAYOUT_OPTIMAL"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandOptimalLayout); 
            mnu.add(mi);
            } 
            {
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_CONTEXT_GRIDVIEW.SHOW_FRAME_NUMBERS"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandShowFrameNumbers); 
            mi.setState(((GridImageView)iControlled).isShowFrames());
            mnu.add(mi);
            }                                         
        }
        return mnu;
    }

    @Override
    public void keyPressed(KeyEvent e) { 
        GridImageView v = (GridImageView)iControlled;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_PLUS:
            case KeyEvent.VK_EQUALS:    
                v.setGrid(v.getRows(), v.getCols());                       
                break;
            case KeyEvent.VK_MINUS: /*
                if (iRows > 1 && iCols > 2) {                                                    
                    setGrid(--iRows, iCols);                        
                } else if (1 == iRows && iCols > 1) {
                    setGrid(iRows, --iCols);
                } */break;                        
            default: break;
        }
    }

    JPopupMenu buildObjectSpecificPopupMenu() {
        return buildContextPopupMenu();
    }

    protected boolean handleCustomCommand(ActionEvent aCommand) {
        GridImageView v = (GridImageView)iControlled;
        switch(aCommand.getActionCommand()) {
            case KCommandOptimalLayout:
                return true;
            case KCommandShowFrameNumbers: {
                v.setShowFrames(!v.isShowFrames());
                //v.invalidateBuffer();
                v.repaint();
            } break;
        }
        return false;
    }
}  

