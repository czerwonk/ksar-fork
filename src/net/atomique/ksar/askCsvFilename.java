/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author alex
 */
public class askCsvFilename {

    public askCsvFilename(kSar hissar) {
        mysar = hissar;
    }
    
    public String run() {
        String filename = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export CSV");
        if ( kSarConfig.lastExportDirectory != null) {
            chooser.setCurrentDirectory(kSarConfig.lastExportDirectory);
        }
        int returnVal = chooser.showSaveDialog(mysar.myUI);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = chooser.getSelectedFile().getAbsolutePath();
            kSarConfig.lastExportDirectory = chooser.getSelectedFile();
            if (! kSarConfig.lastExportDirectory.isDirectory()) {
                kSarConfig.lastExportDirectory = kSarConfig.lastExportDirectory.getParentFile();
                kSarConfig.writeDefault();
            }
        }
        if (filename == null) {
            return null;
        }

        if (new File(filename).exists()) {
            String[] choix = {"Yes", "No"};
            int resultat = JOptionPane.showOptionDialog(null, "Overwrite " + filename + " ?", "File Exist", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choix, choix[1]);
            if (resultat != 0) {
                return null;
            }
        }
        return filename;
    }
    kSar mysar;
}
