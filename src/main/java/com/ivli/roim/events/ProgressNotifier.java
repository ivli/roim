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
package com.ivli.roim.events;

import javax.swing.event.EventListenerList;

/**
 *
 * @author likhachev
 */
public class ProgressNotifier {           
    private final EventListenerList iListeners;  
    
    protected ProgressNotifier() {
         iListeners = new EventListenerList();   
    }
    
    protected void notifyProgressChanged(int aProgress) {
        final ProgressEvent evt = new ProgressEvent(this, aProgress);
        
        for (ProgressListener l : iListeners.getListeners(ProgressListener.class))
            l. ProgressChanged(evt);
    }
    
    public void addProgressListener(ProgressListener aL) {
         iListeners.add(ProgressListener.class, aL);
    }
    
    public void removeProgressListener(ProgressListener aL) {
        iListeners.remove(ProgressListener.class, aL);
    }
}
