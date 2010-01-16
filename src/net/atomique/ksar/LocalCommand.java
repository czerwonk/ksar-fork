/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class LocalCommand extends Thread {

    private final kSar mysar;
    private InputStream in;
    private String command;
    
    public LocalCommand(kSar hissar, String hiscommand, boolean promptForData) {
        mysar = hissar;
        
        if (hiscommand != null && !promptForData) {
            this.command = hiscommand;
        }
        else {   
            String suggestion = ((hiscommand != null) ? hiscommand : "sar -A");
            this.command = JOptionPane.showInputDialog("Enter local command ", suggestion);
        }

        if (this.command == null) {
            return;
        }
        
        try {
            String[] envvar;
            envvar = new String[1];
            envvar[0] = new String("LC_ALL=C");

            Process p = Runtime.getRuntime().exec(command, envvar);
            in = p.getInputStream();
        } catch (Exception e) {
            if ( mysar.myUI != null ) {
                JOptionPane.showMessageDialog(null, "There was a problem while running the command " + this.command, "Local error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("There was a problem while running the command " + this.command);
            }
            in = null;
        }

        return;
    }
    
    public String getCommand () {
        return this.command;
    }

    public void run() {
        try {
            if (in != null) {
                BufferedReader myfile = new BufferedReader(new InputStreamReader(in));
                mysar.parse(myfile);
            } else {
                return;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return;
    }
    
    public String get_action() {
        return "cmd://" + this.command;
    }
}
