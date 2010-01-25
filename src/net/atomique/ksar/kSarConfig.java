/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class kSarConfig implements IConfigurationViewModel {

    private static kSarConfig instance = new kSarConfig();

    public static kSarConfig getInstance() {
        return instance;
    }

    kSarConfig() {
        
        if ( ! loaded ) {
            myPref = Preferences.userNodeForPackage(kSar.class);           
            loaded = true;
            loadDefault();
        }
        
    }

    static public void flush_prefs(String pref) {
        if ( pref == null ) {
            return;
        }
        pref_to_flush=pref;
    }
    
    static public void clear_all() {        
        try {
            myPref.clear();
            myPref.flush();
        } catch (BackingStoreException e) {
            if ( hasUI ) {
                JOptionPane.showMessageDialog(null, prefsave_err, "Config error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println(prefsave_err);
            }
        }
        loaded=false;
        loadDefault();
    }
    
    static public void loadDefault() {
        Integer version;
        tempdir = System.getProperty("java.io.tmpdir");
        if ( !(tempdir.endsWith("/") || tempdir.endsWith("\\")) ) {
            tempdir = tempdir + System.getProperty("file.separator");
        }
        version = myPref.getInt("version", 0);
        unified_user = myPref.get("UnifiedUser", null);
        unified_host = myPref.get("UnifiedHost", null);
        /* */
        String mylastReadDirectory = myPref.get("lastReadDirectory", null);
        if ( mylastReadDirectory != null) {
            lastReadDirectory = new File(mylastReadDirectory);
        }
        String mylastexportdirectory = myPref.get("lastExportDirectory", null);
        if (mylastexportdirectory != null) {
            lastExportDirectory = new File(mylastexportdirectory);
        }
        /* ssh old host */
        sshconnectionmap = new ArrayList<String>();
        String SSHConnectionList = myPref.get("SSHConnectionList", null);
        if (SSHConnectionList != null && version.intValue() > 0 ) {
            String [] tmp = SSHConnectionList.split("§");
            for (int i=0; i <tmp.length ; i++) {
                sshconnectionmap.add(tmp[i]);
            }
        }
        /* ssh command */
        sshconnectioncmd = new ArrayList<String>();
        String SSHCommandList = myPref.get("SSHCommandList", null);
        if (SSHCommandList != null && version.intValue() > 0 ) {
            String [] tmp = SSHCommandList.split("§");
            for (int i=0; i <tmp.length ; i++) {
                sshconnectioncmd.add(tmp[i]);
            }
        }
        // shortcut window
        shortcut_window_list = new HashMap<String,String>();
        String shortcutwindowtemp = myPref.get("Shortcut", null);
        if ( shortcutwindowtemp != null ) {
            String [] tmp = shortcutwindowtemp.split("§");
            for (int i=0; i <tmp.length ; i++) {
                String tmpstr = tmp[i];
                String [] temp = tmpstr.split("∞");
                
                if (temp.length == 2) {
                	shortcut_window_list.put(temp[0], temp[1]);                	
                }
            }
        }
        // known host
        ssh_stricthostchecking = myPref.getBoolean("stricthost", true);
        
        // shortcut window
        shortcut_window_processlist = new HashMap<String,String>();
        String shortcut_window_processlisttemp = myPref.get("Processlist", null);
        if ( shortcut_window_processlisttemp != null ) {
            String [] tmp = shortcut_window_processlisttemp.split("§");
            for (int i=0; i <tmp.length ; i++) {
                String tmpstr = tmp[i];
                String [] temp = tmpstr.split("∞");                
                shortcut_window_processlist.put(temp[0], temp[1]);
            }
        }
        // startup window
        startup_windows_list = new ArrayList<String>();
        String startupList = myPref.get("Startup", null);
        if (startupList != null) {
            String [] tmp = startupList.split("§");
            for (int i=0; i <tmp.length ; i++) {
                startup_windows_list.add(tmp[i]);
            }
        }
        // command association
        association_list = new HashMap<String,String>();
        String associationList = myPref.get("Association", null);
        if ( associationList != null) {
            String [] tmp = associationList.split("§");
            for (int i=0; i <tmp.length ; i++) {
                String tmpstr = tmp[i];
                String [] temp = tmpstr.split("∞");
                association_list.put(temp[0], temp[1]);
            }
        }
        // solaris trigger
        solariscpuidletrigger = new Double(myPref.getDouble("SolarisCpuIdleTrig", 0));
        solariscpusystemtrigger = new Double(myPref.getDouble("SolarisCpuSystemTrig", 50));
        solariscpuwiotrigger = new Double(myPref.getDouble("SolarisCpuWioTrig", 25));
        solariscpuusrtrigger = new Double(myPref.getDouble("SolarisCpuUsrTrig", 50));
        solarisbufferrcachetrigger = new Double(myPref.getDouble("SolarisBufferRcacheTrig", 95));
        solarisdiskbusytrigger = new Double(myPref.getDouble("SolarisDiskBusyTrig", 100));
        solarisrqueuetrigger = new Double(myPref.getDouble("SolarisRqueueTrig", 2));
        solarispagescantrigger = new Double(myPref.getDouble("SolarisPageScanTrig", 100));
        solarisdiskavservtrigger = new Double(myPref.getDouble("SolarisDiskAvservTrig", 30));
        solarisdiskavquetrigger = new Double(myPref.getDouble("SolarisDiskAvqueTrig", 10));

        // hpux trigger
        hpuxcpuidletrigger = new Double(myPref.getDouble("HpuxCpuIdleTrig", 0));
        hpuxcpusystemtrigger = new Double(myPref.getDouble("HpuxCpuSystemTrig", 50));
        hpuxcpuwiotrigger = new Double(myPref.getDouble("HpuxCpuWioTrig", 25));
        hpuxcpuusrtrigger = new Double(myPref.getDouble("HpuxCpuUsrTrig", 50));
        hpuxbufferrcachetrigger = new Double(myPref.getDouble("HpuxBufferRcacheTrig", 95));
        hpuxdiskbusytrigger = new Double(myPref.getDouble("HpuxDiskBusyTrig", 100));
        hpuxrqueuetrigger = new Double(myPref.getDouble("HpuxRqueueTrig", 2));
        hpuxdiskavservtrigger = new Double(myPref.getDouble("HpuxDiskAvservTrig", 30));
        hpuxdiskavquetrigger = new Double(myPref.getDouble("HpuxDiskAvqueTrig", 10));

        // aix trigger
        aixcpuidletrigger = new Double(myPref.getDouble("AIXCpuIdleTrig", 0));
        aixcpusystemtrigger = new Double(myPref.getDouble("AIXCpuSystemTrig", 50));
        aixcpuwiotrigger = new Double(myPref.getDouble("AIXCpuWioTrig", 25));
        aixcpuusrtrigger = new Double(myPref.getDouble("AIXCpuUsrTrig", 50));
        aixbufferrcachetrigger = new Double(myPref.getDouble("AIXBufferRcacheTrig", 95));
        aixdiskbusytrigger = new Double(myPref.getDouble("AIXDiskBusyTrig", 100));
        aixrqueuetrigger = new Double(myPref.getDouble("AIXRqueueTrig", 2));
        aixdiskavservtrigger = new Double(myPref.getDouble("AIXDiskAvservTrig", 30));
        aixdiskavquetrigger = new Double(myPref.getDouble("AIXDiskAvqueTrig", 10));

        // linux trigger
        linuxcpuidletrigger = new Double(myPref.getDouble("LinuxCpuIdleTrig", 0));
        linuxcpusystemtrigger = new Double(myPref.getDouble("LinuxCpuSystemTrig", 50));
        linuxcpuwiotrigger = new Double(myPref.getDouble("LinuxCpuWioTrig", 25));
        linuxcpuusrtrigger = new Double(myPref.getDouble("LinuxCpuUsrTrig", 25));

        // pdf options
        pdfindexpage = myPref.get("PDFindexpage", "");
        pdfbottomleft = myPref.get("PDFbottomleft", "kSar-" + VersionNumber.getInstance().getVersionNumber());
        pdfupperright = myPref.get("PDFupperright", "");

        // PNG/PNG options
        imagewidth = myPref.getInt("ImageWidth", 800);
        imageheight = myPref.getInt("ImageHeight", 600);
        imagehtml = myPref.getBoolean("ImageHTML", false);

        //
        String mysshidentity = myPref.get("SSHIdentify", null);
        if (mysshidentity != null) {
            sshidentity = new File(mysshidentity);
        }
        String mybackimage = myPref.get("BackgroundImage", null);
        if (mybackimage != null) {
            background_image = new File(mybackimage);
        }
        tile_at_startup = myPref.getBoolean("StartTile", false);
        
        //
        landf = myPref.get("landf", UIManager.getLookAndFeel().getName());
        
        Integer tmpcolor ;
        tmpcolor = myPref.getInt("color1", Color.BLUE.getRGB());
        color1 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color2", Color.GREEN.getRGB());
        color2 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color3", Color.RED.getRGB());
        color3 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color4", Color.BLACK.getRGB());
        color4 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color5", Color.MAGENTA.getRGB());
        color5 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color6", Color.YELLOW.getRGB());
        color6 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color7", Color.CYAN.getRGB());
        color7 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color8", Color.GRAY.getRGB());
        color8 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color9", Color.ORANGE.getRGB());
        color9 = new Color(tmpcolor.intValue());
        
        tmpcolor = myPref.getInt("color10", Color.PINK.getRGB());
        color10 = new Color(tmpcolor.intValue());

        tmpcolor = myPref.getInt("color11", Color.DARK_GRAY.getRGB());
        color11 = new Color(tmpcolor.intValue());
        //
        alwaysrefresh = myPref.getInt("alwaysrefresh", 100); //ts les 1
        somerefresh = myPref.getInt("somerefresh", 500); // ts les 5
        lessrefresh = myPref.getInt("lessrefresh", 1000); // ts les 10
        norefresh = myPref.getInt("norefresh", 5000); // ts les 100
        somerefresh_time = myPref.getInt("somerefresh_time", 10);
        lessrefresh_time = myPref.getInt("lessrefresh_time", 100);
        
        dataUpdateInterval = myPref.getLong("AutoDataUpdateInterval", 300000);
    }
    
    static public void writeDefault() {
        if ( ! loaded ) {
            myPref = Preferences.userNodeForPackage(kSar.class);
            loaded = true;
        }
        myPref.putInt("version", Integer.valueOf(VersionNumber.getInstance().getVersionNumberint()));
        
        /* if lastReadDirectory & lastExportDirectory has been set write it to conf */
        if (lastReadDirectory != null) {
            myPref.put("lastReadDirectory", lastReadDirectory.toString());
        }
        if (lastExportDirectory != null) {
            myPref.put("lastExportDirectory", lastExportDirectory.toString());
        }
        if ( unified_user != null ) {
            myPref.put("UnifiedUser", unified_user);
        }
        if ( unified_host != null ) {
            myPref.put("UnifiedHost", unified_host);
        }
        tmpbuf = new StringBuffer();
        for (Iterator<String> it = sshconnectioncmd.iterator(); it.hasNext();) {
            tmpbuf.append(it.next() + "§");
        }
        if (tmpbuf.length() > 1) {
            myPref.put("SSHCommandList", tmpbuf.toString());
        }
        //
        tmpbuf = new StringBuffer();
        for (Iterator<String> it = shortcut_window_processlist.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = shortcut_window_processlist.get(key);
            tmpbuf.append(key+"∞"+value+"§");
        }
        if ( tmpbuf.length() > 1 ) {
            myPref.put("Processlist", tmpbuf.toString());
        } else {
            myPref.remove("Processlist");
        }
        //
        tmpbuf = new StringBuffer();
        for (Iterator<String> it = shortcut_window_list.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = shortcut_window_list.get(key);
            tmpbuf.append(key+"∞"+value+"§");
        }
        if ( tmpbuf.length() > 1 ) {
            myPref.put("Shortcut", tmpbuf.toString());
        } else {
            myPref.remove("Shortcut");
        }
        // association
        tmpbuf = new StringBuffer();
        for (Iterator<String> it = association_list.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            String value = association_list.get(key);
            tmpbuf.append(key+"∞"+value+"§");
        }
        if ( tmpbuf.length() > 1 ) {
            myPref.put("Association", tmpbuf.toString());
        } else {
            myPref.remove("Association");
        }
        //
        tmpbuf =new StringBuffer();
        for (Iterator<String> it = startup_windows_list.iterator(); it.hasNext(); ) {
            tmpbuf.append(it.next()+"§");
        }
        if ( tmpbuf.length() > 1 ) {
            myPref.put("Startup", tmpbuf.toString());
        } else {
            myPref.remove("Startup");
        }
        //
        tmpbuf = new StringBuffer();
        for (Iterator<String> it = sshconnectionmap.iterator(); it.hasNext();) {
            tmpbuf.append(it.next() + "§");
        }
        if (tmpbuf.length() > 1) {
            myPref.put("SSHConnectionList", tmpbuf.toString());
        }
        // solaris trigger
        myPref.putDouble("SolarisCpuIdleTrig", solariscpuidletrigger.doubleValue());
        myPref.putDouble("SolarisCpuSystemTrig", solariscpusystemtrigger.doubleValue());
        myPref.putDouble("SolarisCpuWioTrig", solariscpuwiotrigger.doubleValue());
        myPref.putDouble("SolarisCpuusrTrig", solariscpuusrtrigger.doubleValue());
        myPref.putDouble("SolarisBufferRcacheTrig", solarisbufferrcachetrigger.doubleValue());
        myPref.putDouble("SolarisDiskBusyTrig", solarisdiskbusytrigger.doubleValue());
        myPref.putDouble("SolarisRqueueTrig", solarisrqueuetrigger.doubleValue());
        myPref.putDouble("SolarisPageScanTrig", solarispagescantrigger.doubleValue());
        myPref.putDouble("SolarisDiskAvservTrig", solarisdiskavservtrigger.doubleValue());
        myPref.putDouble("SolarisDiskAvqueTrig", solarisdiskavquetrigger.doubleValue());
        // hpux trigger
        myPref.putDouble("HpuxCpuIdleTrig", hpuxcpuidletrigger.doubleValue());
        myPref.putDouble("HpuxCpuSystemTrig", hpuxcpusystemtrigger.doubleValue());
        myPref.putDouble("HpuxCpuWioTrig", hpuxcpuwiotrigger.doubleValue());
        myPref.putDouble("HpuxCpuusrTrig", hpuxcpuusrtrigger.doubleValue());
        myPref.putDouble("HpuxBufferRcacheTrig", hpuxbufferrcachetrigger.doubleValue());
        myPref.putDouble("HpuxDiskBusyTrig", hpuxdiskbusytrigger.doubleValue());
        myPref.putDouble("HpuxRqueueTrig", hpuxrqueuetrigger.doubleValue());
        myPref.putDouble("HpuxDiskAvservTrig", hpuxdiskavservtrigger.doubleValue());
        myPref.putDouble("HpuxDiskAvqueTrig", hpuxdiskavquetrigger.doubleValue());
        // aix trigger
        myPref.putDouble("AIXCpuIdleTrig", aixcpuidletrigger.doubleValue());
        myPref.putDouble("AIXCpuSystemTrig", aixcpusystemtrigger.doubleValue());
        myPref.putDouble("AIXCpuWioTrig", aixcpuwiotrigger.doubleValue());
        myPref.putDouble("AIXCpuUsrTrig", aixcpuusrtrigger.doubleValue());
        myPref.putDouble("AIXBufferRcacheTrig", aixbufferrcachetrigger.doubleValue());
        myPref.putDouble("AIXDiskBusyTrig", aixdiskbusytrigger.doubleValue());
        myPref.putDouble("AIXRqueueTrig", aixrqueuetrigger.doubleValue());
        myPref.putDouble("AIXDiskAvservTrig", aixdiskavservtrigger.doubleValue());
        myPref.putDouble("AIXDiskAvqueTrig", aixdiskavquetrigger.doubleValue());

        //
        myPref.putDouble("LinuxCpuIdleTrig", linuxcpuidletrigger.doubleValue());
        myPref.putDouble("LinuxCpuSystemTrig", linuxcpusystemtrigger.doubleValue());
        myPref.putDouble("LinuxCpuWioTrig", linuxcpuwiotrigger.doubleValue());
        myPref.putDouble("LinuxCpuUsrTrig", linuxcpuusrtrigger.doubleValue());
        //
        // pdf options
        if (!pdfbottomleft.equals("kSar-" + VersionNumber.getInstance().getVersionNumber())) {
            myPref.put("PDFbottomleft", pdfbottomleft);
        }
        myPref.put("PDFindexpage", pdfindexpage);
        myPref.put("PDFupperright", pdfupperright);
        /* PNG/JPG options */
        myPref.putInt("ImageWidth", imagewidth);
        myPref.putInt("ImageHeight", imageheight);
        myPref.putBoolean("ImageHTML", imagehtml);
        /* ssh identity */
        if (sshidentity != null) {
            myPref.put("SSHIdentify", sshidentity.toString());
        } else {
            myPref.remove("SSHIdentify");
        }
        if (background_image != null) {
            myPref.put("BackgroundImage", background_image.toString());
        } else {
            myPref.remove("BackgroundImage");
        }
        myPref.putBoolean("StartTile", tile_at_startup);
        //
        myPref.putBoolean("stricthost", ssh_stricthostchecking);
        myPref.putInt("color1", color1.getRGB());
        myPref.putInt("color2", color2.getRGB());
        myPref.putInt("color3", color3.getRGB());
        myPref.putInt("color4", color4.getRGB());
        myPref.putInt("color5", color5.getRGB());
        myPref.putInt("color6", color6.getRGB());
        myPref.putInt("color7", color7.getRGB());
        myPref.putInt("color8", color8.getRGB());
        myPref.putInt("color9", color9.getRGB());
        myPref.putInt("color10", color10.getRGB());
        myPref.putInt("color11", color11.getRGB());
        //
        //
         myPref.putInt("alwaysrefresh", alwaysrefresh); //ts les 1
         myPref.putInt("somerefresh", somerefresh); // ts les 5
         myPref.putInt("lessrefresh", lessrefresh); // ts les 10
         myPref.putInt("norefresh", norefresh); // ts les 100
         //
         myPref.putInt("somerefresh_time", somerefresh_time);
         myPref.putInt("lessrefresh_time", lessrefresh_time);
         
         myPref.put("landf", landf);
        
         myPref.putLong("AutoDataUpdateInterval", dataUpdateInterval);
         
        if ( pref_to_flush != null ) {
            myPref.remove(pref_to_flush);
        }
        try {
            myPref.flush();
        } catch (BackingStoreException e) {
            if (hasUI) {
                JOptionPane.showMessageDialog(null, prefsave_err, "Config error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println(prefsave_err);
            }
        }

    }

    public static String getBackground_image() {
        if (background_image == null) {
            return "";
        }
        return background_image.getAbsolutePath();
    }

    public static String getSSHidentity() {
        if (sshidentity == null) {
            return "";
        }
        return sshidentity.getAbsolutePath();
    }

    public static String readSpecial(String key) {
        String value = myPref.get(key,null);
        StringBuffer temp = new StringBuffer();
        if ( value == null) {
            return null;
        }
        if ( "__SPLITVALUE__".equals(value) ) {
            int idx=0;
            String tmp;
            //System.err.println("splitted value: " + key);
            while ( (tmp=myPref.get("__"+ idx + "_"+key,null)) != null) {
                // ok if we got an array concat
                //System.err.println("loaded idx:"+idx+" value:" + tmp);
                temp.append(tmp);
                idx++;
            }
            return temp.toString();
        } 
        
        return myPref.get(key, null);
        
    }

    public static void writeSpecial(String key, String value) {
        if ( value == null) {
            return;
        }
        /*
         preferences is too long split it, and store reference       
        */
        int value_size = value.length();
        //System.err.println("key:  "+ key + "string size: " + value_size);
        int idx=0;
        if ( value_size > 8000 ) {
            int cur_pos=0;

            while ( (value_size-cur_pos) > 8000) {
                //System.err.println("idx: "+idx+ " B: " + cur_pos + " E: "+ (cur_pos+8000));
                myPref.put("__"+ idx + "__"+key, value.substring(cur_pos,(cur_pos+8000)));
                //System.err.println("idx:"+idx+ "!"+value.substring(cur_pos,(cur_pos+8000))+"!");
                idx++;
                cur_pos+=8000;
            }
            //System.err.println("Last B:" + cur_pos + " E: " + value_size );
            myPref.put("__"+ idx + "_"+key, value.substring(cur_pos,value_size));
            //System.err.println("last: !" + value.substring(cur_pos,value_size));
            
            
            myPref.put(key,"__SPLITVALUE__");
        } else {
            myPref.put(key, value);
        }
        
        try {
            myPref.flush();
        } catch (BackingStoreException e) {
            if ( hasUI ) {
                JOptionPane.showMessageDialog(null, prefsave_err, "Config error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println(prefsave_err);
            }
        }
    }
    private static Preferences myPref;
    private static boolean loaded;
    /* */
    static public File lastReadDirectory;
    static public File lastExportDirectory;
    /* ssh and command ligne */
    static public List<String> sshconnectionmap;
    static public List<String> sshconnectioncmd;
    static public Map<String,String> shortcut_window_list;
    static public List<String> startup_windows_list;
    static public Map<String,String> association_list;
    static public boolean ssh_stricthostchecking;
    //
    static public HashMap<String,String> shortcut_window_processlist;
    /* solaris trigger */
    static public Double solariscpuidletrigger;
    static public Double solariscpusystemtrigger;
    static public Double solariscpuwiotrigger;
    static public Double solariscpuusrtrigger;
    static public Double solarisbufferrcachetrigger;
    static public Double solarisdiskbusytrigger;
    static public Double solarisdiskavquetrigger;
    static public Double solarisdiskavservtrigger;
    static public Double solarisrqueuetrigger;
    static public Double solarispagescantrigger;
    /* hpux trigger */
    static public Double hpuxcpuidletrigger;
    static public Double hpuxcpusystemtrigger;
    static public Double hpuxcpuwiotrigger;
    static public Double hpuxcpuusrtrigger;
    static public Double hpuxbufferrcachetrigger;
    static public Double hpuxdiskbusytrigger;
    static public Double hpuxdiskavquetrigger;
    static public Double hpuxdiskavservtrigger;
    static public Double hpuxrqueuetrigger;
    /* aix trigger */
    static public Double aixcpuidletrigger;
    static public Double aixcpusystemtrigger;
    static public Double aixcpuwiotrigger;
    static public Double aixcpuusrtrigger;
    static public Double aixbufferrcachetrigger;
    static public Double aixdiskbusytrigger;
    static public Double aixdiskavquetrigger;
    static public Double aixdiskavservtrigger;
    static public Double aixrqueuetrigger;
    /* linux trigger */
    static public Double linuxcpuidletrigger;
    static public Double linuxcpusystemtrigger;
    static public Double linuxcpuwiotrigger;
    static public Double linuxcpuusrtrigger;
    static public boolean linuxhack;
    /* PDF options */
    static public String pdfindexpage;
    static public String pdfbottomleft;
    static public String pdfupperright;
    /* PNG/JPG options */
    static public int imagewidth;
    static public int imageheight;
    static public boolean imagehtml;
    /* others */
    static public File sshidentity;
    static public File background_image;
    /* local var */
    static StringBuffer tmpbuf = null;
    static boolean hasUI = false;
    static final String prefsave_err = "There was a problem while saving your preferences";
    static public String unified_user;
    static public String unified_host;
    static private String pref_to_flush=null;
    static public boolean tile_at_startup;
    static String tempstr = null;
    static String landf;
    
    static public Color color1;
    static public Color color2;
    static public Color color3;
    static public Color color4;
    static public Color color5;
    static public Color color6;
    static public Color color7;
    static public Color color8;
    static public Color color9;
    static public Color color10;
    static public Color color11;
    
    static public int alwaysrefresh;
    static public int somerefresh;
    static public int somerefresh_time;
    static public int lessrefresh;
    static public int lessrefresh_time;
    static public int norefresh;
    
    // not save for use in app
    static public String tempdir;
    public static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);

    private static long dataUpdateInterval;
    
    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getBackgroundImageFile()
     */
    @Override
    public File getBackgroundImageFile() {
        return background_image;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getDataUpdateInterval()
     */
    @Override
    public long getDataUpdateInterval() {
        return dataUpdateInterval;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getImageHeight()
     */
    @Override
    public int getImageHeight() {
        return imageheight;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getImageWidth()
     */
    @Override
    public int getImageWidth() {
        return imagewidth;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getLookAndFeel()
     */
    @Override
    public String getLookAndFeel() {
        return landf;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getPdfBottomText()
     */
    @Override
    public String getPdfBottomText() {
        return pdfbottomleft;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getPdfIndexPageText()
     */
    @Override
    public String getPdfIndexPageText() {
        return pdfindexpage;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getPdfUpperRightText()
     */
    @Override
    public String getPdfUpperRightText() {
        return pdfupperright;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#getSshKeyFile()
     */
    @Override
    public File getSshKeyFile() {
        return sshidentity;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#isHtmlIndexEnabled()
     */
    @Override
    public boolean isHtmlIndexEnabled() {
        return imagehtml;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#isSshStrictHostCheckEnabled()
     */
    @Override
    public boolean isSshStrictHostCheckEnabled() {
        return ssh_stricthostchecking;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setBackgroundImageFile(java.io.File)
     */
    @Override
    public void setBackgroundImageFile(File file) {
        background_image = file;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setDataUpdateInterval(long)
     */
    @Override
    public void setDataUpdateInterval(long value) {
        dataUpdateInterval = value;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setHtmlIndexEnabled(boolean)
     */
    @Override
    public void setHtmlIndexEnabled(boolean value) {
        imagehtml = value;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setImageHeight(int)
     */
    @Override
    public void setImageHeight(int value) {
        imageheight = value;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setImageWidth(int)
     */
    @Override
    public void setImageWidth(int value) {
        imagewidth = value;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setLookAndFeel(javax.swing.UIManager.LookAndFeelInfo)
     */
    @Override
    public void setLookAndFeel(String lookAndFeel) {
        landf = lookAndFeel;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setPdfBottomText(java.lang.String)
     */
    @Override
    public void setPdfBottomText(String text) {
        pdfbottomleft = text;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setPdfIndexPageText(java.lang.String)
     */
    @Override
    public void setPdfIndexPageText(String text) {
        pdfindexpage = text;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setPdfUpperRightText(java.lang.String)
     */
    @Override
    public void setPdfUpperRightText(String text) {
        pdfupperright = text;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setSshKeyFile(java.io.File)
     */
    @Override
    public void setSshKeyFile(File file) {
        sshidentity = file;
    }

    /* (non-Javadoc)
     * @see net.atomique.ksar.IConfigurationViewModel#setSshStrictHostCheckEnabled(boolean)
     */
    @Override
    public void setSshStrictHostCheckEnabled(boolean value) {
        ssh_stricthostchecking = value;
    }
}