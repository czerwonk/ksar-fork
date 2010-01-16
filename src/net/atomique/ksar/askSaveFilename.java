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
public class askSaveFilename {

    public askSaveFilename(final kSar hissar) {
        mysar = hissar;
    }

    public String run() {
        String fileName=null;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save sar text file");
        if ( kSarConfig.lastExportDirectory != null) {
            chooser.setCurrentDirectory(kSarConfig.lastExportDirectory);
        }
        int returnVal = chooser.showSaveDialog(mysar.myUI);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
        }
        if (fileName == null) {
            return null;
        }

        if (new File(fileName).exists()) {
            String[] choix = {"Yes", "No"};
            int resultat = JOptionPane.showOptionDialog(null, "Overwrite " + fileName + " ?", "File Exist", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, choix, choix[1]);
            if (resultat != 0) {
                return null;
            }
        }
        return fileName;
    }
    private final kSar mysar;
}
