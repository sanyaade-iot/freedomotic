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
package it.freedomotic.reactions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.thoughtworks.xstream.XStream;

import it.freedomotic.app.Freedomotic;

import it.freedomotic.persistence.FreedomXStream;

import it.freedomotic.util.DOMValidateDTD;
import it.freedomotic.util.Info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.thoughtworks.xstream.XStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Enrico
 */
public class TriggerPersistence {

    private static ArrayList<Trigger> list = new ArrayList<Trigger>();

    public static void saveTriggers(File folder) {
        if (list.isEmpty()) {
            LOG.warning("There are no triggers to persist, " + folder.getAbsolutePath()
                    + " will not be altered.");
            return;
        }

        if (!folder.isDirectory()) {
            LOG.warning(folder.getAbsoluteFile() + " is not a valid trigger folder. Skipped");
            return;
        }

        XStream xstream = FreedomXStream.getXstream();
        deleteTriggerFiles(folder);

        try {
            LOG.config("Saving triggers to file in " + folder.getAbsolutePath());

            for (Trigger trigger : list) {
                if (trigger.isToPersist()) {
                    String uuid = trigger.getUUID();

                    if ((uuid == null) || uuid.isEmpty()) {
                        trigger.setUUID(UUID.randomUUID().toString());
                    }

                    String fileName = trigger.getUUID() + ".xtrg";
                    FileWriter fstream = new FileWriter(folder + "/" + fileName);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(xstream.toXML(trigger)); //persist only the data not the logic
                    //Close the output stream

                    out.close();
                    fstream.close();
                }
            }
        } catch (Exception e) {
            LOG.info(e.getLocalizedMessage());
            LOG.severe(Freedomotic.getStackTraceInfo(e));
        }
    }

    private static void deleteTriggerFiles(File folder) {
        File[] files = folder.listFiles();
        // This filter only returns object files
        FileFilter objectFileFileter =
                new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isFile() && file.getName().endsWith(".xtrg")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
        files = folder.listFiles(objectFileFileter);
        for (File file : files) {
            file.delete();
        }
    }

    public static ArrayList<Trigger> getTriggers() {
        return list;
    }

    public synchronized static void loadTriggers(File folder) {
        XStream xstream = FreedomXStream.getXstream();

        // This filter only returns object files
        FileFilter objectFileFileter =
                new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isFile() && file.getName().endsWith(".xtrg")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };

        File[] files = folder.listFiles(objectFileFileter);

        try {
            StringBuilder summary = new StringBuilder();
            //print an header for the index.txt file
            summary.append("#Filename \t\t #TriggerName \t\t\t #ListenedChannel").append("\n");

            if (files != null) {
                for (File file : files) {
                    Trigger trigger = null;
                    try {
                        //validate the object against a predefined DTD
                        String xml =
                                DOMValidateDTD.validate(file, Info.getApplicationPath() + "/config/validator/trigger.dtd");
                        trigger = (Trigger) xstream.fromXML(xml);
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Trigger file {0} is not well formatted: {1}", new Object[]{file.getPath(), e.getLocalizedMessage()});
                        continue;
                    }

                    //addAndRegister trigger to the list if it is not a duplicate
                    if (!list.contains(trigger)) {
                        if (trigger.isHardwareLevel()) {
                            trigger.setPersistence(false); //it has not to me stored in root/data folder
                            addAndRegister(trigger); //in the list and start listening
                        } else {
                            if (folder.getAbsolutePath().startsWith(Info.getPluginsPath())) {
                                trigger.setPersistence(false);
                            } else {
                                trigger.setPersistence(true); //not hardware trigger and not plugin related
                            }

                            list.add(trigger); //only in the list not registred. I will be registred only if used in mapping
                        }
                    } else {
                        LOG.warning("Trigger '" + trigger.getName() + "' is already in the list");
                    }

                    summary.append(trigger.getUUID()).append("\t\t").append(trigger.getName()).append("\t\t\t")
                            .append(trigger.getChannel()).append("\n");
                }

                //writing a summary .txt file with the list of commands in this folder
                FileWriter fstream = new FileWriter(folder + "/index.txt");
                BufferedWriter indexfile = new BufferedWriter(fstream);
                indexfile.write(summary.toString());
                //Close the output stream
                indexfile.close();
            } else {
                LOG.config("No triggers to load from this folder " + folder.toString());
            }
        } catch (Exception e) {
            LOG.severe("Exception while loading this trigger.\n" + Freedomotic.getStackTraceInfo(e));
        }
    }

    public static synchronized void addAndRegister(Trigger t) {
        int preSize = TriggerPersistence.size();

        if (!list.contains(t)) {
            list.add(t);
            t.register();
        } else {
            //this trigger is already in the list
            int old = list.indexOf(t);
            list.get(old).unregister();
            list.set(old, t);
            t.register();
        }

        int postSize = TriggerPersistence.size();

        if (!(postSize == (preSize + 1))) {
            LOG.severe("Error while while adding and registering trigger '" + t.getName() + "'");
        }
    }

    public static synchronized void add(Trigger t) {
        if (t != null) {
            int preSize = TriggerPersistence.size();

            if (!list.contains(t)) {
                list.add(t);
            } else {
                //this trigger is already in the list
                int old = list.indexOf(t);
                list.get(old).unregister();
                list.set(old, t);
            }

            int postSize = TriggerPersistence.size();

            if (!(postSize == (preSize + 1))) {
                LOG.severe("Error while while adding trigger '" + t.getName() + "'");
            }
        }
    }

    public static synchronized void remove(Trigger t) {
        int preSize = TriggerPersistence.size();

        try {
            t.unregister();
            list.remove(t);
        } catch (Exception e) {
            LOG.severe("Error while while unregistering the trigger '" + t.getName() + "'");
        }

        int postSize = TriggerPersistence.size();

        if (!(postSize == (preSize - 1))) {
            LOG.severe("Error while while removing trigger '" + t.getName() + "'");
        }
    }

    /**
     * Get a trigger by its name
     *
     * @param name
     * @return a Trigger object with the name (ignore-case) as the String in
     * input
     */
    public static Trigger getTrigger(String name) {
        if ((name == null) || (name.trim().isEmpty())) {
            return null;
        }

        for (Trigger trigger : list) {
            if (trigger.getName().equalsIgnoreCase(name.trim())) {
                return trigger;
            }
        }

        LOG.log(Level.WARNING, "Searching for a trigger named ''{0}'' but it doesn''t exist.", name);

        return null;
    }

    public static Trigger getTrigger(Trigger input) {
        if (input != null) {
            for (Iterator it = list.iterator(); it.hasNext();) {
                Trigger trigger = (Trigger) it.next();

                if (trigger.equals(input)) {
                    return trigger;
                }
            }
        }

        return null;
    }

    public static Trigger getTrigger(int i) {
        return list.get(i);
    }

    public static Iterator<Trigger> iterator() {
        return list.iterator();
    }

    public static int size() {
        return list.size();
    }
    private static final Logger LOG = Logger.getLogger(TriggerPersistence.class.getName());
}
