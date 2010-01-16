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
public class BackgroundImage {

    public BackgroundImage(final kSarDesktop hisdesktop) {
        mydesktop = hisdesktop;
        String filename = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose your background image file");
        chooser.setFileHidingEnabled(true);
        if (kSarConfig.background_image != null) {
            chooser.setCurrentDirectory(kSarConfig.background_image);
        }
        int returnVal = chooser.showOpenDialog(mydesktop);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().getAbsolutePath();
            File tmp = new File(filename);
            if (tmp.exists()) {
                kSarConfig.background_image = tmp;
                kSarConfig.writeDefault();
            } else {
                filename = null;
            }
        } else {
            kSarConfig.background_image = null;
            kSarConfig.writeDefault();
        }

        return;
    }
    final private kSarDesktop mydesktop;
}
