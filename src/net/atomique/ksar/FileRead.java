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

    private final kSar mysar;
    private File sarfilename = null;
    
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
    
    public File getSarFile() {
        return this.sarfilename;
    }
    
    public void setSarFile(File sarFile) {
        this.sarfilename = sarFile;
    }

    public String get_action() {
        return ("file://" + sarfilename);
    }
    
    public void run() {
        if (this.sarfilename == null) {
            return;
        }
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(sarfilename));
            mysar.parse(reader);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception ex) {
                // ex would hide exception in outer try block
            }
        }
    }
}
