/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author alex
 */
public class SSHIdentity {

    public SSHIdentity(kSarDesktop hisdesktop) {
        mydesktop = hisdesktop;
        String filename = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose your identity file");
        chooser.setFileHidingEnabled(false);
        if ( kSarConfig.sshidentity != null) {
            chooser.setCurrentDirectory(kSarConfig.sshidentity);
        }
        int returnVal = chooser.showOpenDialog(mydesktop);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().getAbsolutePath();
            File tmp = new File(filename);
            if (tmp.exists()) {
                kSarConfig.sshidentity = tmp;
                kSarConfig.writeDefault();
            } else {
                filename = null;
            }
        } else {
            kSarConfig.sshidentity = null;
            kSarConfig.writeDefault();
        }

        return;
    }
    kSarDesktop mydesktop;
}
