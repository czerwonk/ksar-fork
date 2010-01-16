/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor. 
 */
package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JFileChooser;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class FileRead  extends Thread {

    public FileRead(kSar hissar, File file, boolean autoExecute) {
        mysar = hissar;
        
        if (file != null && autoExecute) {
            sarfilename = file;  
        }
        else {
            sarfilename = this.getLocalFile(file);
        }
    }
    
    public File getLocalFile(File file) {
        JFileChooser fc = new JFileChooser();
        
        if (file != null && file.exists()) {
            fc.setSelectedFile(file);
        }
        
        if (fc.showDialog(null, "Open") == JFileChooser.APPROVE_OPTION
                && fc.getSelectedFile().exists()) {
            return fc.getSelectedFile();
        }
        
        return null;
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
    public File sarfilename = null;
}
