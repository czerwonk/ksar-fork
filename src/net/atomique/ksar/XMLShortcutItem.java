/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

/**
 *
 * @author alex
 */
public class XMLShortcutItem {

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    
    public void setStartup(String startup) {
        if ( "yes".equals(startup) ) {
            this.openatstartup=true;
        }
    }
    
    public String getProcesslist() {
        return processlist;
    }

    public boolean hasprocesslist() {
        if ( processlist == null ) {
            return false;
        }
        return true;
    }
    
    public void setProcesslist(String processlist) {
        this.processlist = processlist;
    }
    
    public boolean getStartup() {
        return this.openatstartup;
    }

    public boolean valid() {
        if (type == null) {
            return false;
        }
        if (description == null) {
            errormsg = new String("Missing description on shortcut");
            return false;
        }
        if ("file".equals(type)) {
            if (filename == null) {
                errormsg = new String("Missing filename on file shortcut (" + description + ")");
                return false;
            }
            return true;
        }
        if ("command".equals(type)) {
            if (command == null) {
                errormsg = new String("Missing command on command shortcut (" + description + ")");
                return false;
            }
            return true;
        }
        if ("ssh".equals(type)) {
            if (host == null) {
                errormsg = new String("Missing host info on ssh shortcut (" + description + ")");
                return false;
            }
            if (login == null) {
                errormsg = new String("Missing login info on ssh shortcut (" + description + ")");
                return false;
            }
            if (command == null) {
                errormsg = new String("Missing command on ssh shortcut (" + description + ")");
                return false;
            }
            return true;
        }
        errormsg = new String("type of shortcut unknown :" + type + " (" + description + ")");
        return false;
    }

    public String toString() {
        if ( ! valid() )  {
            return "";
        }
        if ( "ssh".equals(type) ) {
            return "ssh://"+login+"@"+host+"/"+command;
        }
        if ( "file".equals(type) ) {
            return "file://" + filename;
        }
        if ( "command".equals(type) ) {
            return "cmd://"+command;
        }
        return "";
    }
    
    public String getProcesslistCommand() {
        if ( processlist == null ) {
            return null;
        }
        if ( ! valid() ) {
            return null;
        }
        if ( "ssh".equals(type) ) {
            return "ssh://"+login+"@"+host+"/"+processlist;
        }
        if ( "command".equals(type) ) {
            return "cmd://"+processlist;
        }
        return null;
    }
    
    String description = null;
    String type = null;
    String host = null;
    String login = null;
    String command = null;
    String filename = null;
    String errormsg = null;
    String processlist = null;
    boolean openatstartup = false;

    
}
