/**
 *
 * Copyright (c) 2009-2013 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.wilsonkong888.wifi_id;

import it.freedomotic.api.EventTemplate;
import it.freedomotic.api.Protocol;
import it.freedomotic.app.Freedomotic;
import it.freedomotic.exceptions.UnableToExecuteException;
import it.freedomotic.reactions.Command;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.net.*;
import it.freedomotic.events.ProtocolRead;
import it.freedomotic.objects.EnvObjectLogic;
import it.freedomotic.objects.EnvObjectPersistence;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Wifi_id extends Protocol {

    private static final Logger LOG = Logger.getLogger(Wifi_id.class.getName());

    public Wifi_id() {
        super("Wifi_id", "/wifi-id/wifi_id-manifest.xml");
        setPollingWait(configuration.getIntProperty("polling_rate", 5000)); //waits 2000ms in onRun method before call onRun() again
    }

    @Override
    public void onStart() {
        //called when the user starts the plugin from UI
    }

    @Override
    public void onStop() {
        //called when the user stops the plugin from UI
    }

    @Override
    protected void onRun() {
        try {
            //called in a loop while this plugin is running
            //loops waittime is specified using setPollingWait()
            URL url = null;
            InputStream is = null;
            BufferedReader br;
            String line, html = null;
            ProtocolRead event;
            Authenticator.setDefault(new MyAuthenticator());
            //URL url = new URL(configuration.getStringProperty("url", ""));
            //InputStream ins = url.openConnection().getInputStream();
            //BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
            //String str;
            //while((str = reader.readLine()) != null)
            //System.out.println(str);
            try {
                url = new URL(configuration.getStringProperty("url", ""));
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    html = html + line;
                }
            } catch (IOException ioe) {
                setDescription(ioe.getMessage());
            }
            //Freedomotic.logger.config(html);
            html = html.toLowerCase();
            System.out.println("HTML: " + html);
            for (EnvObjectLogic object : EnvObjectPersistence.getObjectByProtocol("wifi_id")) {
                String mac_address = object.getPojo().getPhisicalAddress();
                String name = object.getPojo().getName();
                //Freedomotic.logger.info(mac_address);
                //Freedomotic.logger.info(name);
                if (html.contains(mac_address.toLowerCase())) {
                    // user exist
                    event = new ProtocolRead(this, "wifi_id", mac_address);
                    event.addProperty("wifi_id.present", "true");
                    Freedomotic.sendEvent(event);
                } else {
                    // user not exist
                    event = new ProtocolRead(this, "wifi_id", mac_address);
                    event.addProperty("wifi_id.present", "false");
                    Freedomotic.sendEvent(event);
                }
            }
            is.close();

            //print the string in the freedomotic log using INFO level
            //Freedomotic.logger.info(buffer.toString());
        } catch (IOException ex) {
            setDescription(ex.getMessage());
        }
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        //this method receives freedomotic commands send on channel app.actuators.protocol.arduinousb.in
    }

    @Override
    protected boolean canExecute(Command c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    class MyAuthenticator extends Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String kuser, kpass;
            // I haven't checked getRequestingScheme() here, since for NTLM
            // and Negotiate, the usrname and password are all the same.
            //System.err.println("Feeding username and password for " + getRequestingScheme());
            kuser = configuration.getStringProperty("url_username", "");
            kpass = configuration.getStringProperty("url_password", "");
            return (new PasswordAuthentication(kuser, kpass.toCharArray()));
        }
    }
}
