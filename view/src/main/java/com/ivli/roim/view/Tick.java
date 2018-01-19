/*
 * Copyright (C) 2017 likhachev
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

import java.awt.geom.Point2D;

/**
 *
 * @author likhachev
 */
  public class Tick {
        protected Point2D iPos;
        
        Tick (Point2D aP) {
            iPos=aP;
        }
        
        Point2D getPos() {
            return iPos;
        }
        
        void move(double adX, double adY) {
            iPos.setLocation(iPos.getX() + adX, iPos.getY() + adY);
        }
      
        void paint(AbstractPainter aP) {               
            aP.paint(this);    
        }     
    }
       
