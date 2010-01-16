/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author alex
 */
public class VersionNumber {

    private static VersionNumber instance = new VersionNumber();
    private final Pattern pattern;
    
    private String versionName;
    private int major;
    private int minor;
    private int micro;
    
    public static VersionNumber getInstance() {
        return instance;
    }
    
    VersionNumber() {
        this.pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
        
        setVersionNumber(readVersion("/kSar.ver"));
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

    public void setVersionNumber(String version) {
        Matcher matcher = this.pattern.matcher(version);
        
        if (!matcher.find()) {
            throw new IllegalArgumentException(version);
        }
        
        this.versionName = version;
        this.major = new Integer(matcher.group(1));
        this.minor = new Integer(matcher.group(2));
        this.micro = new Integer(matcher.group(3));
        
        return;
    }

    public String getVersionNumber() {
        return this.versionName;
    }
    public Integer getVersionNumberint() {
        return ((this.major*100) + (this.minor*10) + this.micro);
    }

    public boolean isOlderThan(String version) {
        Matcher matcher = this.pattern.matcher(version);
        
        if (matcher == null) {
            throw new IllegalArgumentException(version);
        }
        
        Integer mymajor = new Integer(matcher.group(1));
        Integer myminor = new Integer(matcher.group(2));
        Integer mymicro = new Integer(matcher.group(3));
        
        if (this.major < mymajor.intValue()) {
            return true;
        }
        
        if (this.minor < myminor.intValue()) {
            return true;
        }
        
        if (this.micro < mymicro.intValue()) {
            return true;
        }

        return false;
    }
}
