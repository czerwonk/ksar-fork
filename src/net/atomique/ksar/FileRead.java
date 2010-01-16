/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. 
 */
package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JFileChooser;

/**
 *
 * @author alex 
 */
public class FileRead  extends Thread {

    public FileRead(kSar hissar) {
        mysar = hissar;
        try {
            sarfilename = getLocalFile();
            if (sarfilename == null) {
            }
            return;
        } catch (Exception e) {
        }
    }

    public FileRead(kSar hissar, String filename) {
        mysar = hissar;
        sarfilename = filename;
    }
    
    public String getLocalFile() {
        String filename = null;
        JFileChooser fc = new JFileChooser();
        if ( kSarConfig.lastReadDirectory != null) {
            fc.setCurrentDirectory(kSarConfig.lastReadDirectory);
        }
        int returnVal = fc.showDialog(null, "Open");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filename = fc.getSelectedFile().getAbsolutePath();
            kSarConfig.lastReadDirectory = fc.getSelectedFile();
            if (! kSarConfig.lastReadDirectory.isDirectory() ) {
                kSarConfig.lastReadDirectory = kSarConfig.lastReadDirectory.getParentFile();
                kSarConfig.writeDefault();
            }
        }
        return filename;
    }

    public String get_action() {
        return "file://"+sarfilename;
    }
    
    public void run() {
        try {
            if (sarfilename == null) {
                return;
            }
            FileReader tmpfile = new FileReader(sarfilename);
            BufferedReader myfile = new BufferedReader(tmpfile);
            mysar.parse(myfile);
            myfile.close();
            tmpfile.close();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    kSar mysar = null;
    public String sarfilename = null;
}
