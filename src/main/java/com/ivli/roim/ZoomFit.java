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
package com.ivli.roim;

/**
 *
 * @author likhachev
 */
public class ZoomFit {
    /*
     * no fit - zooming used
     */
    public static final int NONE = 0;    
    /*
     * zoom to fit entire image into view
     */
    public static final int VISIBLE = 1; 
    /*
     * width
     */
    public static final int WIDTH = 2; 
    /*
     * height
     */
    public static final int HEIGHT = 3; 
    /*
     * apply 100% zoom thus image is displayed pixel to pixel no matter how big it is
     */
    public static final int ONE_TO_ONE = 4;    
}