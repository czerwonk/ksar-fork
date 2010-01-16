/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Ps;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.atomique.ksar.kSar;

/**
 *
 * @author alex
 */
public class ProcessList {

    public ProcessList(final kSar hissar,final String command) {
        mysar = hissar;
        if (command.startsWith("cmd://")) {
            String commandname = new String(command.substring(6));
            dolocalcommand(commandname);
        }
        if (command.startsWith("ssh://")) {
            String commandname = new String(command.substring(6));
            dosshread(commandname);
        }
    }

    private void dosshread(final String cmd) {
        if (cmd != null) {
            launched_command = new SSHCommand(mysar, cmd, this);
        }
        launched_command.start();
    }

    private void dolocalcommand(final String cmd) {
        if (cmd != null) {
            launched_command = new LocalCommand(mysar, cmd, this);
        }
        launched_command.start();
    }

    public void parse(final BufferedReader infile) {
        String thisLine;
        String tempbuf[];
        try {
            while ((thisLine = infile.readLine()) != null) {
                tempbuf = thisLine.split("\\s+", 8);
                if (tempbuf.length == 8) {
                    ProcessInfo tmp = new ProcessInfo(tempbuf[1], tempbuf[7]);
                    listproc.put(tempbuf[1], tmp);
                }
            }
            infile.close();
        } catch (IOException ioe) {
        }        
    }
    
    public Map<String, ProcessInfo> listproc = new HashMap<String, ProcessInfo>();
    private Thread launched_command = null;
    private final kSar mysar;
}
