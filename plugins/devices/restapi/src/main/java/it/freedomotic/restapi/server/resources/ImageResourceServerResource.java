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

package it.freedomotic.restapi.server.resources;

import it.freedomotic.core.ResourcesManager;
import it.freedomotic.restapi.server.FreedomRestServer;
import it.freedomotic.restapi.server.interfaces.ImageResource;
import it.freedomotic.util.Info;
import java.io.File;
import org.restlet.resource.ServerResource;

/**
 *
 * @author gpt
 */

public class ImageResourceServerResource extends ServerResource implements ImageResource {

    private static volatile String imageFilePath;

    //TODO: this is a first implementation. Use a restlet redirector instead.
    @Override
    public void doInit() {
        String fileName = (String) getRequest().getAttributes().get("filename");
        imageFilePath = ResourcesManager.getFile(new File(Info.getResourcesPath()), fileName).getPath();
    }

    @Override
    public String getImagePath() {
        String path = imageFilePath;
        if (imageFilePath.startsWith(Info.getResourcesPath())) {
            path = imageFilePath.substring(Info.getResourcesPath().length());
            path = path.replace('\\', '/');
            System.out.println("RESTAPI path: " + path);
        }
        redirectSeeOther(FreedomRestServer.RESOURCES_PATH + path);
        return path;
    }
}
