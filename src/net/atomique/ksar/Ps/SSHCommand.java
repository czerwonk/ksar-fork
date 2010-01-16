/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Ps;

import net.atomique.ksar.*;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

//
// kerberos
import java.security.Security;
import java.util.Hashtable;
import java.util.Map;
/**
 *
 * @author alex
 */
public class SSHCommand extends Thread {

    private final static Properties systemprops = System.getProperties();

    public SSHCommand(final kSar hissar,final String command,final ProcessList hisprocesslist) {
        super();
        num_try = 0;
        String username = null;
        mysar = hissar;
        myprocesslist = hisprocesslist;

        // passed via command
        String passed_user = null;
        String passed_host = null;
        String passed_cmd = null;
        int passed_port = 22;

        if (command != null) {
            String[] cmd_splitted = command.split("@", 2);
            if (cmd_splitted.length != 2) {
                return;
            }
            String[] user_part = cmd_splitted[0].split(":", 2);
            if (user_part.length == 2) {
                passed_user = user_part[0];
                cmd_password = user_part[1];
            } else {
                passed_user = cmd_splitted[0];
            }
            String[] cmd_part = cmd_splitted[1].split("/", 2);
            if (cmd_part.length != 2) {
                return;
            }
            String[] host_part = cmd_part[0].split(":", 2);
            if (host_part.length == 2) {
                passed_host = host_part[0];
                try {
                    passed_port = Integer.parseInt(host_part[1]);
                } catch (NumberFormatException e) {
                    return;
                }
            } else {
                passed_host = host_part[0];
            }
            passed_cmd = cmd_part[1];
        }


        try {
            // start ssh
            JSch jsch = new JSch();
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

            if (passed_user == null) {
                return;
            } else {
                // info has been passed via command
                host = passed_host;
                username = passed_user;
                port = passed_port;
                if (port == 22) {
                    cnx = username + "@" + host;
                } else {
                    cnx = username + "@" + host + ":" + port;
                }
            }
            //


            //kerberos
            //System.setProperty("java.security.useSubjectCredsOnly", "false");
            //System.setProperty("sun.security.krb5.debug", "true");


            // start session, with user host port
            session = jsch.getSession(username, host, port);

            // load known host, if exist
            if (new File(userhome + ".ssh" + (String) systemprops.get("file.separator") + "known_hosts").exists()) {
                jsch.setKnownHosts(userhome + ".ssh" + (String) systemprops.get("file.separator") + "known_hosts");
            }
            
            if (cmd_password != null) {
                session.setPassword(cmd_password);
            }

            // make the ui 
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);

            // make connection
            try {
                session.connect();
            } catch (JSchException ee) {
                return;
            }

            if (passed_cmd == null) {
                return;
            } else {
                cmd = passed_cmd;
            }

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("LC_ALL=C " + cmd  +  "\n");
            channel.setInputStream(null);
            channel.setXForwarding(false);
            ((ChannelExec) channel).setErrStream(System.err);
            
            infile = channel.getInputStream();
            channel.connect();
            
            if ( channel.isClosed() && channel.getExitStatus() != -1 ) {
                    return;
                    //System.exit(2);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void run() {
        int maxwaitdata = 10;
        
        try {
            if (infile == null) {
                return;
            }
            // old fashion lead to error;
            //  wait for channel ready
            InputStreamReader tmpin= new InputStreamReader(infile);
            while ( maxwaitdata >0 && ! tmpin.ready() ) {
                // no data and not in timeout 
                try { Thread.sleep( 1000 ); }catch( Exception ee ) {}
            }
            BufferedReader myfile = new BufferedReader(tmpin);
            myprocesslist.parse(myfile);
            myfile.close();
            tmpin.close();
            infile.close();
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public class MyUserInfo implements UserInfo, UIKeyboardInteractive {

        public boolean promptYesNo(final String str) {
                return true;
        }
        private String passwd;
        private String passphrase;
       
        public String getPassphrase() {
            if (mysar.mydesktop.unified_id) {
                return mysar.mydesktop.unified_pass;
            }
            if (cmd_password != null) {
                return cmd_password;
            }
            return passphrase;
        }

        public boolean promptPassphrase(final String message) {
                num_try++;
                return true;            
        }

        public String getPassword() {
            if (mysar.mydesktop.unified_id) {
                return mysar.mydesktop.unified_pass;
            }
            if (cmd_password != null) {
                return cmd_password;
            }

            return passwd;
        }

        public boolean promptPassword(final String message) {
                num_try++;
                return true;
        }
        
        public String[] promptKeyboardInteractive(final String destination,final String name,final String instruction,final String[] prompt,final boolean[] echo) {
            if ( mysar.mydesktop.unified_id && num_try == 0) {
                num_try++;
                return mysar.mydesktop.unified_pass.split("[.]");
            }
            if (cmd_password != null) {
                num_try++;
                return cmd_password.split("[.]");
            }
            return null;
        }

        public void showMessage(final String message) {
        }
    }

    public static class MyLogger implements com.jcraft.jsch.Logger {

        private static Map<Integer,String> name = new Hashtable<Integer,String>();

        static {
            name.put(new Integer(DEBUG), "DEBUG: ");
            name.put(new Integer(INFO), "INFO: ");
            name.put(new Integer(WARN), "WARN: ");
            name.put(new Integer(ERROR), "ERROR: ");
            name.put(new Integer(FATAL), "FATAL: ");
        }

        public boolean isEnabled(final int level) {
            return true;
        }

        public void log(final int level,final String message) {
            System.err.print(name.get(new Integer(level)));
            System.err.println(message);
        }
    }
    private int port = 22;
    private String host=null;
    final private kSar mysar;
    private InputStream infile;
    private Channel channel;
    private Session session;
    private String cnx;
    private String cmd;
    private String cmd_password;
    private final boolean debug = false;
    private int num_try = 0;
    
    final private ProcessList myprocesslist;
}
