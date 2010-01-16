/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar;

/**
 *
 * @author alex
 */
public class XMLAssociationItem {

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getProcesslist() {
        return processlist;
    }
    
    public void setProcesslist(String processlist) {
        this.processlist = processlist; 
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getErrormsg() {
        return errormsg;
    }

    
    public boolean valid() {
        if ( description == null ) {
            errormsg = new String("Missing description on association");
            return false;
        }
        if (command == null) {
            errormsg = new String("Missing command on association (" + description+")");
            return false;
        }
        if ( command.length() == 0 ) {
            errormsg = new String("Missing command on association (" + description+")");
            return false;
        }
        return true;
    }
    
    public String toString() {
        if ( ! valid() ) {
            return "";
        }
        return "cmd://" + command;
    }
    
    String description = null;
    String command = null;
    String processlist =null;
    String errormsg = null;
}
