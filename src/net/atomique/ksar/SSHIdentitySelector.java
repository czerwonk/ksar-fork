/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class SSHIdentitySelector {

    final JFileChooser chooser;
    
    public SSHIdentitySelector() {
        this.chooser = new JFileChooser();
        this.chooser.setDialogTitle("Choose your identity file");
        this.chooser.setFileHidingEnabled(false);
    }
    
    /**
     * Selects a new SSH identity from file system
     * @return Selected file or null
     */
    public File selectIdentity() {
        if (kSarConfig.sshidentity != null) {
            this.chooser.setCurrentDirectory(kSarConfig.sshidentity);
        }

        if (this.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                && this.chooser.getSelectedFile().exists()) {
            return this.chooser.getSelectedFile();
        }
       
        return null;
    }
}
