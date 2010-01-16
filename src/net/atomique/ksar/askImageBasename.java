/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import javax.swing.JFileChooser;

/**
 *
 * @author alex
 */
public class askImageBasename {

    public askImageBasename(kSar hissar) {
        mysar = hissar;
    }

    public String run() {
        String filename = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose a basename");
        if (kSarConfig.lastExportDirectory != null) {
            chooser.setCurrentDirectory(kSarConfig.lastExportDirectory);
        }
        int returnVal = chooser.showSaveDialog(mysar.myUI);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().getAbsolutePath();
            kSarConfig.lastExportDirectory = chooser.getSelectedFile();
            if (!kSarConfig.lastExportDirectory.isDirectory()) {
                kSarConfig.lastExportDirectory = kSarConfig.lastExportDirectory.getParentFile();
                kSarConfig.writeDefault();
            }
        }
        if (filename == null) {
            return null;
        }

        return filename;
    }
    kSar mysar;
}
