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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.freedomotic.model.object;

import java.io.Serializable;

/**
 *
 * @author Enrico
 */
public abstract class Behavior
        implements Serializable {

    private static final long serialVersionUID = -4973746059396782383L;
	
	private String name;
    private String description;
    private boolean active;
    private int priority;
    private boolean readOnly;
    public final static String VALUE_OPPOSITE = "opposite";
    public final static String VALUE_NEXT = "next";
    public final static String VALUE_PREVIOUS = "previous";

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadonly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getDescriprion() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Behavior other = (Behavior) obj;

        if ((this.name == null) ? (other.name != null) : (!this.name.equals(other.name))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (11 * hash) + ((this.name != null) ? this.name.hashCode() : 0);

        return hash;
    }
}
