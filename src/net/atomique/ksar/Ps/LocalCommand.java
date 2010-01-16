/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Ps;

import net.atomique.ksar.kSar;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JOptionPane;

/**
 *
 * @author alex
 */
public class LocalCommand extends Thread {

    public LocalCommand(final kSar hissar, final ProcessList hisprocesslist) {
        super();
        mysar = hissar;
        myprocesslist = hisprocesslist;
        try {
            String[] envvar;
            envvar = new String[1];
            envvar[0] = "LC_ALL=C";

            command = JOptionPane.showInputDialog("Enter local command ", "ps -ef");
            if (command == null) {
                return;
            }
            Process proc = Runtime.getRuntime().exec(command, envvar);
            infile = proc.getInputStream();
        } catch (Exception e) {
            if (mysar.myUI == null) {
                System.err.println(errmsg + command);
            } else {
                JOptionPane.showMessageDialog(null, errmsg + command, "Local error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return;
    }

    public LocalCommand(final kSar hissar, final String hiscommand, final ProcessList hisprocesslist) {
        super();
        mysar = hissar;
        command = hiscommand;
        myprocesslist = hisprocesslist;
        try {
            String[] envvar;
            envvar = new String[1];
            envvar[0] = "LC_ALL=C";

            Process proc = Runtime.getRuntime().exec(command, envvar);
            infile = proc.getInputStream();
        } catch (Exception e) {
            if (mysar.myUI == null) {
                System.err.println(errmsg + command);
            } else {
                JOptionPane.showMessageDialog(null, errmsg + command, "Local error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return;
    }

    public void run() {
        try {
            if (infile == null) {
                return;
            } else {
                BufferedReader myfile = new BufferedReader(new InputStreamReader(infile));
                myprocesslist.parse(myfile);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String getAction() {
        return "cmd://" + command;
    }
    
    private final ProcessList myprocesslist;
    private final kSar mysar;
    private InputStream infile;
    public String command;
    private final static String errmsg = "There was a problem while running the command ";
}
