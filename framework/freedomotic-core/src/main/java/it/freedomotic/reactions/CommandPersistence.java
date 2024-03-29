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
package it.freedomotic.reactions;

import it.freedomotic.app.Freedomotic;

import it.freedomotic.persistence.FreedomXStream;

import it.freedomotic.util.DOMValidateDTD;
import it.freedomotic.util.Info;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.CannotResolveClassException;

/**
 *
 * @author Enrico
 */
public class CommandPersistence {

    private static Map<String, Command> userCommands = new HashMap<String, Command>();
    private static Map<String, Command> hardwareCommands = new HashMap<String, Command>();

    public static void add(Command c) {
        if (c != null) {
            if (!userCommands.containsKey(c.getName().trim().toLowerCase())) {
                userCommands.put(c.getName(),
                        c);
                LOG.log(Level.FINE, "Added command ''{0}'' to the list of user commands", c.getName());
            } else {
                LOG.log(Level.CONFIG, "Command ''{0}'' already in the list of user commands. Skipped", c.getName());
            }
        } else {
            LOG.warning("Attempt to add a null user command to the list. Skipped");
        }
    }

    public static void remove(Command input) {
        userCommands.remove(input.getName());
    }

    public static int size() {
        return userCommands.size();
    }

    public static Iterator<Command> iterator() {
        return userCommands.values().iterator();
    }

    public static Command getCommand(String name) {
        Command command = userCommands.get(name.trim());

        if (command != null) {
            return command;
        }

        Command hwCommand = getHardwareCommand(name);

        return hwCommand;
    }

    public static Collection<Command> getHardwareCommands() {
        return hardwareCommands.values();
    }

    public static Collection<Command> getUserCommands() {
        return userCommands.values();
    }

    public static Command getHardwareCommand(String name) {
        Command command = hardwareCommands.get(name.trim());

        if (command == null) {
            LOG.log(Level.SEVERE,"Missing command ''{0}" + "''. "
                    + "Maybe the related plugin is not installed or cannot be loaded", name);
        }

        return command;
    }

    public static void loadCommands(File folder) {
        XStream xstream = FreedomXStream.getXstream();
        File[] files = folder.listFiles();

        // This filter only returns object files
        FileFilter objectFileFileter =
                new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isFile() && file.getName().endsWith(".xcmd")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                };

        files = folder.listFiles(objectFileFileter);

        if (files != null) {
            FileWriter fstream = null;

            try {
                StringBuilder summary = new StringBuilder();
                //print an header for the index.txt file
                summary.append("#Filename \t\t #CommandName \t\t\t #Destination").append("\n");

                for (File file : files) {
                     Command command = null;
                     String xml = null;
                    try {
                        xml = DOMValidateDTD.validate(file, Info.getApplicationPath() + "/config/validator/command.dtd");
                     } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Reaction file {0} is not well formatted: {1}", new Object[]{file.getPath(), e.getLocalizedMessage()});
                        continue;
                    }
                    try {
                        command = (Command) xstream.fromXML(xml);

                        if (command.isHardwareLevel()) { //an hardware level command
                            hardwareCommands.put(command.getName(),
                                    command);
                        } else { //a user level commmand

                            if (folder.getAbsolutePath().startsWith(Info.getPluginsPath())) {
                                command.setEditable(false);
                            }

                            add(command);
                        }

                        summary.append(file.getName()).append("\t\t").append(command.getName())
                                .append("\t\t\t").append(command.getReceiver()).append("\n");
                    } catch (CannotResolveClassException e) {
                        LOG.log(Level.SEVERE, "Cannot unserialize command due to unrecognized class ''{0}'' in \n{1}", new Object[]{e.getMessage(), xml});
                    }
                }

                fstream = new FileWriter(folder + "/index.txt");

                BufferedWriter indexfile = new BufferedWriter(fstream);
                indexfile.write(summary.toString());
                //Close the output stream
                indexfile.close();
            } catch (IOException ex) {
                Logger.getLogger(CommandPersistence.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fstream.close();
                } catch (IOException ex) {
                    Logger.getLogger(CommandPersistence.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            LOG.log(Level.CONFIG, "No commands to load from this folder {0}", folder.toString());
        }
    }

    public static void saveCommands(File folder) {
        try {
            if (userCommands.isEmpty()) {
                LOG.log(Level.WARNING, "There are no commands to persist, {0} will not be altered.", folder.getAbsolutePath());

                return;
            }

            if (!folder.isDirectory()) {
                LOG.log(Level.WARNING, "{0} is not a valid command folder. Skipped", folder.getAbsoluteFile());

                return;
            }

            XStream xstream = FreedomXStream.getXstream();
            deleteCommandFiles(folder);

            for (Command c : userCommands.values()) {
                if (c.isEditable()) {
                    String uuid = c.getUUID();

                    if ((uuid == null) || uuid.isEmpty()) {
                        c.setUUID(UUID.randomUUID().toString());
                    }

                    String fileName = c.getUUID() + ".xcmd";
                    FileWriter fstream = new FileWriter(folder + "/" + fileName);
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(xstream.toXML(c));
                    //Close the output stream
                    out.close();
                }
            }
        } catch (Exception e) {
            LOG.info(e.getLocalizedMessage());
            LOG.severe(Freedomotic.getStackTraceInfo(e));
        }
    }

    private static void deleteCommandFiles(File folder) {
        File[] files = folder.listFiles();

        // This filter only returns object files
        FileFilter objectFileFileter =
                new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isFile() && file.getName().endsWith(".xcmd")) {
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

    private CommandPersistence() {
    }
    private static final Logger LOG = Logger.getLogger(CommandPersistence.class.getName());
}
