/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author alex
 */
public class VersionNumber {

    private static VersionNumber instance = new VersionNumber();
    
    public static VersionNumber getInstance() {
        return instance;
    }
    
    VersionNumber() {
        setTo(readVersion("/kSar.ver"));
    }
    
    public String readVersion(String filename) {
        StringBuffer tmpstr = new StringBuffer();
        BufferedReader reader = null;
        try {
            InputStream is = this.getClass().getResourceAsStream(filename);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            String line = "";
            while ((line = reader.readLine()) != null) {
                tmpstr.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        try {
            reader.close();
            return tmpstr.toString();
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setTo(String version) {
        setVersionNumber(version);
    }

    public void setVersionNumber(String version) {
        myversion = version;
        String tmp[]= version.split("\\.");
        if ( tmp.length != 3) {
            return;
        }
        major = new Integer(tmp[0]);
        minor = new Integer(tmp[1]);
        micro = new Integer(tmp[2]);
        return;
    }

    public static String getVersionNumber() {
        return myversion;
    }
    public static Integer getVersionNumberint() {
        return (major*100)+(minor*10)+micro;
    }

    public static boolean isOlderThan(String version) {
        Integer mymajor;
        Integer myminor;
        Integer mymicro;

        String [] tmp= version.split("\\.");
        if ( tmp.length != 3) {
            return false;
        }

        mymajor = new Integer(tmp[0]);
        myminor = new Integer(tmp[1]);
        mymicro = new Integer(tmp[2]);

        
        if (major.intValue() < mymajor.intValue()) {
            return true;
        }
        
        if (minor.intValue() < myminor.intValue()) {
            return true;
        }
        
        if (micro.intValue() < mymicro.intValue()) {
            return true;
        }

        return false;
    }
    
    
    private static String myversion;
    static Integer major;
    static Integer minor;
    static Integer micro;
}
