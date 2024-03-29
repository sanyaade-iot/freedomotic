/**
 *
 * Copyright (c) 2009-2013 Freedomotic team
 * http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This Program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Freedomotic; see the file COPYING.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.freedomotic.model.geometry;

import java.io.Serializable;

/**
 *
 * @author Enrico
 */
public class FreedomEllipse
        implements FreedomShape,
        Serializable {

    private static final long serialVersionUID = -2776037977467820777L;
	
	private FreedomPoint center;
    private int xRadius;
    private int yRadius;

    public FreedomEllipse(FreedomPoint center, int xRadius, int yRadius) {
        this.center = center;
        this.xRadius = xRadius;
        this.yRadius = yRadius;
    }

    public FreedomEllipse(int x, int y, int xRadius, int yRadius) {
        this.center = new FreedomPoint(x, y);
        this.xRadius = xRadius;
        this.yRadius = yRadius;
    }
}
