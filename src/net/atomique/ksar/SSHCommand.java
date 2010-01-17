/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
//
// kerberos
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class SSHCommand extends Thread {

    private static Pattern pattern = Pattern.compile("^([^@]+)+@([^:]+)(?:\\:(\\d{1,5}))?$");
    
    private String host;
    private String user;
    private int port = 22;
    private String command;
    private String password;
    private boolean promptForData;
    private static String passphrase;
    
    private final kSar mysar;
    private InputStream in = null;
    private InputStream err = null;
    private Channel channel = null;
    private Session session = null;
    private DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
    private JComboBox combo = new JComboBox(comboModel);
    private DefaultComboBoxModel comboModel2 = new DefaultComboBoxModel();
    private JComboBox combo2 = new JComboBox(comboModel2);
    private boolean debug = false;
    private int num_try = 0;
    private JSch jsch = null;
    
    final static Properties systemprops = System.getProperties();

    private SSHCommand(kSar hissar) {
        this.mysar = hissar;
    }
    
    public SSHCommand(kSar hissar, String suggestedServer, String suggestedCommand) {
        this(hissar);
        
        this.promptForData = true;
        this.setServer(suggestedServer);
        this.command = suggestedCommand;
        
        this.connect();
    }
    
    public SSHCommand(kSar hissar, String command) {
        this(hissar);
        
        this.parseCommand(command);
        
        this.connect();
    }
    
    private void parseCommand(String cmd) {
        if (cmd == null) {
            return;
        }
        
        String[] splitted = cmd.split("/");
        
        if (splitted.length != 2) {
            return;
        }
        
        this.setServer(splitted[0]);
        this.command = splitted[1];
    }
    
    private void connect() {
        num_try = 0;
        String cnx = null;

        try {
            // start ssh
            jsch = new JSch();
            if (debug) {
                JSch.setLogger(new MyLogger());
            }
            // load the identity if file if in the preferences
            if (kSarConfig.sshidentity != null) {
                try {
                    jsch.addIdentity(kSarConfig.sshidentity.toString());
                } catch (Exception e) {
                }
            }

            // get user home
            String userhome = (String) systemprops.get("user.home") + (String) systemprops.get("file.separator");

            if (this.user == null 
                    || this.host == null 
                    || this.promptForData) {
                // make the combo with old connection + local cnx
                if (kSarConfig.sshconnectionmap == null || kSarConfig.sshconnectionmap.size() < 1) {
                    comboModel.addElement(System.getProperty("user.name") + "@localhost");
                } else {
                    for (Iterator it = kSarConfig.sshconnectionmap.iterator(); it.hasNext();) {
                        comboModel.addElement(it.next());
                    }
                    if ( ! kSarConfig.sshconnectionmap.contains((String) System.getProperty("user.name") + "@localhost")) {
                        comboModel.addElement(System.getProperty("user.name") + "@localhost");
                    }
                }
                
                // make the combo editable ;-)
                combo.setEditable(true);
                
                if (this.user != null) {
                    comboModel.setSelectedItem(this.getServer());
                }
                
                // ok no command passed with user/host popup the window
                int ret = JOptionPane.showConfirmDialog(mysar.myUI, combo, "SSH Connection", JOptionPane.YES_NO_CANCEL_OPTION);
                if (ret != JOptionPane.OK_OPTION) {
                    return;
                }

                if (combo.getSelectedItem() == null) {
                    return;
                }
                
                cnx = (String)combo.getSelectedItem();
                this.setServer(cnx);
            }

            // start session, with user host port
            session = jsch.getSession(this.user, this.host, this.port);
            
            if ( ! kSarConfig.ssh_stricthostchecking ) {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
            }
            
            // load known host, if exist
            if (new File(userhome + ".ssh" + (String) systemprops.get("file.separator") + "known_hosts").exists()) {
                jsch.setKnownHosts(userhome + ".ssh" + (String) systemprops.get("file.separator") + "known_hosts");
            }
            // set the password is unified is used
            if (mysar.mydesktop.unified_id && mysar.mydesktop.unified_user.equals(this.user)) {
                session.setPassword(mysar.mydesktop.unified_pass);
            }
            
            if (this.password != null) {
                session.setPassword(this.password);
            }

            // make the ui 
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);

            // make connection
            try {
                session.connect();
            } catch (JSchException ee) {
                JOptionPane.showMessageDialog(mysar.myUI, "Unable to connect", "SSH error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // save the connection user/host for futurused
            if (kSarConfig.sshconnectionmap == null || kSarConfig.sshconnectionmap.size() == 0) {
                kSarConfig.sshconnectionmap.add(cnx);
                kSarConfig.writeDefault();
            } else {
                if ( ! kSarConfig.sshconnectionmap.contains(cnx)) {
                    kSarConfig.sshconnectionmap.add(cnx);
                    kSarConfig.writeDefault();
                }
            }

            if (this.command == null || this.promptForData) {
                // make the combo of the all command + sar -A
                if (kSarConfig.sshconnectioncmd == null || kSarConfig.sshconnectioncmd.size() < 1) {
                    comboModel2.addElement("sar -A");
                } else {
                    for (Iterator it = kSarConfig.sshconnectioncmd.iterator(); it.hasNext();) {
                        comboModel2.addElement(it.next());
                    }
                    if ( ! kSarConfig.sshconnectioncmd.contains("sar -A") ) {
                        comboModel2.addElement("sar -A");
                    }
                }
                combo2.setEditable(true);
                
                if (this.command != null) {
                    comboModel2.setSelectedItem(this.command);   
                }
                
                int ret2 = JOptionPane.showConfirmDialog(mysar.myUI, combo2, "SSH Command", JOptionPane.YES_NO_CANCEL_OPTION);
                if (ret2 != JOptionPane.OK_OPTION) {
                    return;
                }
                this.command = (String)combo2.getSelectedItem();

                if (this.command == null) {
                    return;
                }
            }
            
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("LC_ALL=C " + this.command  +  "\n");
            channel.setInputStream(null);
            channel.setXForwarding(false);
            ((ChannelExec) channel).setErrStream(System.err);
            
            
            in = channel.getInputStream();
            err = ((ChannelExec)channel).getErrStream();
            
            channel.connect();
            if (channel.isClosed()) {
                if (channel.getExitStatus() != -1) {
                    JOptionPane.showMessageDialog(mysar.myUI, "There was a problem while retrieving stat", "SSH error", JOptionPane.ERROR_MESSAGE);
                    System.exit(2);
                }
            }

            // keep the command for future use for the combo
            if (kSarConfig.sshconnectioncmd == null || kSarConfig.sshconnectioncmd.size() == 0) {
                kSarConfig.sshconnectioncmd.add(this.command);
                kSarConfig.writeDefault();
            } else {
                if ( ! kSarConfig.sshconnectioncmd.contains(this.command) ) {
                    kSarConfig.sshconnectioncmd.add(this.command);
                    kSarConfig.writeDefault();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public String getServer() {
        return String.format("%s%s@%s%s", 
                             ((this.user != null) ? this.user : "user.name"),
                             ((this.password != null) ? ":" + this.password : ""),
                             ((this.host != null) ? this.host : "localhost"),
                             ((this.port != 22) ? ":" + Integer.toString(this.port) : ""));
    }
    
    public void setServer(String server) {
        if (server == null) {
            return;
        }
        
        Matcher matcher = pattern.matcher(server);
        
        if (matcher.find()) {
            if (matcher.group(1).contains(":")) {
                String[] splittedLogin = matcher.group(1).split(":");
                
                this.user = splittedLogin[0];
                this.password = splittedLogin[1];
            }
            else
            {
                this.user = matcher.group(1);
            }
            
            this.host = matcher.group(2);
            
            if (matcher.group(3) != null) {
                this.port = Integer.parseInt(matcher.group(3));
            }
        }
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }

    public String get_action() {
        return ("ssh://" + this.getServer() + "/" + this.command);
    }

    public void run() {
        int max_waitdata = 10;
        
        try {
            if (in == null) {
                return;
            }
            // old fashion lead to error;
            //  wait for channel ready
            InputStreamReader tmpin= new InputStreamReader(in);
            InputStreamReader tmperr = new InputStreamReader(err);
            
            while ( max_waitdata >0 && ! tmpin.ready() ) {
                // no data and not in timeout 
                try { Thread.sleep( 1000 ); }catch( Exception ee ) {}
            }
            BufferedReader myfile = new BufferedReader(tmpin);
            
            
            mysar.parse(myfile);
            myfile.close();
            tmpin.close();
            in.close();
            err.close();
            channel.disconnect();
            session.disconnect();
            channel=null;
            session=null;
        } catch (Exception e) {
            System.out.println(e);
        }
        return;
    }

    public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

        public boolean promptYesNo(String str) {

            String[] options = {"yes", "no"};
            if (mysar.myUI != null) {
                int foo = JOptionPane.showOptionDialog(mysar.myUI, str, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                return foo == 0;
            } else {
                return true;
            }
        }
        //String passwd;
        JTextField passwordField = (JTextField) new JPasswordField(20);
        JTextField passphraseField = (JTextField) new JPasswordField(20);

        public String getPassphrase() {
            if (mysar.mydesktop.unified_id) {
                return mysar.mydesktop.unified_pass;
            }

            return passphrase;
        }

        public boolean promptPassphrase(String message) {
            if (( ! mysar.mydesktop.unified_id && password == null) || num_try > 0) {
                Object[] ob = {passphraseField};
                int result = JOptionPane.showConfirmDialog(mysar.myUI, ob, message, JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    passphrase = passphraseField.getText();
                    return true;
                } else {
                    return false;
                }
            } else {
                num_try++;
                return true;
            }
        }

        public String getPassword() {
            if (mysar.mydesktop.unified_id) {
                return mysar.mydesktop.unified_pass;
            }
            
            return password;
        }

        public boolean promptPassword(String message) {
            if (( ! mysar.mydesktop.unified_id  && password == null) || num_try > 0) {
                Object[] ob = {passwordField};
                if (mysar.myUI != null) {
                    int result = JOptionPane.showConfirmDialog(mysar.myUI, ob, message, JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        password = passwordField.getText();
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                num_try++;
                return true;
            }
        }
        final GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        private Container panel;

        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
            if (mysar.mydesktop.unified_id && num_try == 0) {
                num_try++;
                return mysar.mydesktop.unified_pass.split("[.]");
            }
            if (password != null) {
                num_try++;
                return password.split("[.]");
            }
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts = new JTextField[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]), gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if (echo[i]) {
                    texts[i] = new JTextField(20);
                } else {
                    texts[i] = new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if (JOptionPane.showConfirmDialog(mysar.myUI, panel,destination + " : " + name,JOptionPane.OK_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                String[] response = new String[prompt.length];
                StringBuffer t = new StringBuffer();

                for (int i = 0; i < prompt.length; i++) {
                    response[i] = texts[i].getText();
                    t.append(response[i]);
                }
                password = t.toString();
                return response;
            } else {
                return null;  // cancel
            }
        }

        public void showMessage(String message) {
            if (mysar.myUI != null) {
                JOptionPane.showMessageDialog(mysar.myUI, message);
            } else {
                return;
            }
        }
    }

    public static class MyLogger implements com.jcraft.jsch.Logger {

        static Hashtable<Integer,String> name = new Hashtable<Integer,String>();

        static {
            name.put(new Integer(DEBUG), "DEBUG: ");
            name.put(new Integer(INFO), "INFO: ");
            name.put(new Integer(WARN), "WARN: ");
            name.put(new Integer(ERROR), "ERROR: ");
            name.put(new Integer(FATAL), "FATAL: ");
        }

        public boolean isEnabled(int level) {
            return true;
        }

        public void log(int level, String message) {
            System.err.print(name.get(new Integer(level)));
            System.err.println(message);
        }
    }
}
