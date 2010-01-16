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
 * @author alex
 */
public class LocalCommand extends Thread {

    public LocalCommand(kSar hissar) {
        mysar = hissar;
        try {
            String[] envvar;
            envvar = new String[1];
            envvar[0] = new String("LC_ALL=C");

            command = JOptionPane.showInputDialog("Enter local command ", "sar -A");
            if (command == null) {
                return;
            }
            Process p = Runtime.getRuntime().exec(command, envvar);
            in = p.getInputStream();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "There was a problem while running the command ", "Local error", JOptionPane.ERROR_MESSAGE);
            in = null;
        }

        return;
    }

    public LocalCommand(kSar hissar, String hiscommand) {
        mysar = hissar;
        command = hiscommand;
        try {
            String[] envvar;
            envvar = new String[1];
            envvar[0] = new String("LC_ALL=C");

            Process p = Runtime.getRuntime().exec(command, envvar);
            in = p.getInputStream();
        } catch (Exception e) {
            if ( mysar.myUI != null ) {
                JOptionPane.showMessageDialog(null, "There was a problem while running the command " +command, "Local error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("There was a problem while running the command " +command);
            }
            in = null;
        }

        return;
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
        return "cmd://" + command;
    }
    
    kSar mysar = null;
    InputStream in = null;
    public String command=null;
}
