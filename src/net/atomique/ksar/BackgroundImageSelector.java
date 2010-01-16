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
public class BackgroundImageSelector {

    private final JFileChooser chooser; 
    
    public BackgroundImageSelector() { 
        this.chooser = new JFileChooser();
        this.chooser.setDialogTitle("Choose your background image file");
        this.chooser.setFileHidingEnabled(true);
    }
    
    /**
     * Selects a background file from file system
     * @return File or null
     */
    public File chooseBackgroundFile() {
        if (kSarConfig.background_image != null) {
            this.chooser.setCurrentDirectory(kSarConfig.background_image);
        }
        
        if (this.chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION
                && this.chooser.getSelectedFile().exists()) {
            return this.chooser.getSelectedFile();
        }
        
        return null;
    }
}
