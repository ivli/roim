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
package com.ivli.roim.algorithm;

/**
 * A direction in the plane. As a convenience, directions provide unit vector
 * components (manhattan metric) for both the conventional plane and screen
 * coordinates (y axis reversed).
 * 
 * @author Tom Gibara
 * 
 */

public enum Direction {

	// statics

	E(1, 0), NE(1, 1),

	N(0, 1), NW(-1, 1),

	W(-1, 0), SW(-1, -1),

	S(0, -1), SE(1, -1);

	// fields

	/**
	 * The horizontal distance moved in this direction within the plane.
	 */

	public final int planeX;

	/**
	 * The vertical distance moved in this direction within the plane.
	 */

	public final int planeY;

	/**
	 * The horizontal distance moved in this direction in screen coordinates.
	 */

	public final int screenX;

	/**
	 * The vertical distance moved in this direction in screen coordinates.
	 */

	public final int screenY;

	/**
	 * The euclidean length of this direction's vectors.
	 */

	public final double length;

	// constructor

	private Direction(int x, int y) {
		planeX = x;
		planeY = y;
		screenX = x;
		screenY = -y;
		length = x != 0 && y != 0 ? Math.sqrt(2.0) / 2.0 : 1.0;
	}

}


