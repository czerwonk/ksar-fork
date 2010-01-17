/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author alex
 */
public class kSar {

    private final static int TIMER_INTERVAL = 300000;
    
    private final kSarInstanceConfig config;
    private final Timer timer;
    private String selectionPath;
    
    private kSar() {
        this.config = new kSarInstanceConfig();

        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!reload_command.equals("Empty")) {
                    selectionPath = myUI.getSelectionPath();
                    do_mission(reload_command);
                }
            }
            
        }, 0, TIMER_INTERVAL);
    }
    
    public kSar(String title) {
        this();
        
        if ( ! parse_mission(title)) {
            System.err.println("Cannot process input: " + title);
            return;
        }
        
        do_mission(title);
    }

    public kSar(kSarDesktop hisdesktop, String title) {
        this();
        
        mydesktop = hisdesktop;
        if ( ! parse_mission(title)) {
            System.err.println("Cannot process input: " + title);
            return;
        }
        addGUI(title);
        do_mission(title);
    }
    
    public void do_fileread(String filename) {
        resetInfo();
        
        FileRead command = null;
        
        if (filename == null) {
            command = new FileRead(this, this.config.getLastFile(), true);
        } 
        else {
            command = new FileRead(this, new File(filename), false);
        }
        
        launched_command = command;
        reload_command = command.get_action();
        launched_command.start();
        
        this.config.setLastFile(command.getSarFile());
    }

    public void do_sshread(String cmd) {
        resetInfo();
        
        SSHCommand command = null;
        
        if (cmd == null) {
            command = new SSHCommand(this, this.config.getLastSshServer(), this.config.getLastSshCommand());
        } 
        else {
            command = new SSHCommand(this, cmd);
        }
        
        launched_command = command;
        reload_command = command.get_action();
        launched_command.start();
        
        this.config.setLastSshCommand(command.getCommand());
        this.config.setLastSshServer(command.getServer());
    }
    
    public void do_localcommand(String cmd) {
        resetInfo();
        
        LocalCommand command = null;
        
        if (cmd == null) {
            command = new LocalCommand(this, this.config.getLastCommand(), true);
        } else {
            command = new LocalCommand(this, cmd, false);
        }
        
        launched_command = command;
        reload_command = command.get_action();
        launched_command.start();
        
        this.config.setLastCommand(command.getCommand());
    }

    public void do_mission(String title) {
        if ( "Empty".equals(title) ) {
            return;
        }
        if (title.startsWith("file://")) {
            String filename = new String(title.substring(7));
            do_fileread(filename);
            return;
        }        
        if (title.startsWith("cmd://")) {
            String commandname = new String(title.substring(6));
            get_processlist(title);
            do_localcommand(commandname);
            return;
        }
        if (title.startsWith("ssh://")) {
            String commandname = new String(title.substring(6));
            get_processlist(title);
            do_sshread(commandname);
            return;
        }
        do_fileread(title);
    }

    private void get_processlist(String orig) {
        //
        if ( kSarConfig.shortcut_window_processlist == null ) {
            return ;
        }
        String processlist_cmd=kSarConfig.shortcut_window_processlist.get(orig);
        if ( processlist_cmd != null ) {
            pslist = new net.atomique.ksar.Ps.ProcessList(this,processlist_cmd);
        }
        
    }
    
    private boolean testfile(String filepath) {
        if (!new File(filepath).exists()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean parse_mission(String mission) {
        // Empty is open by GUI skipping testing
        if ("Empty".equals(mission)) {
            return true;
        }
        // parse command line input string
        if (mission.startsWith("file://")) {
            return testfile(mission.substring(7));
        } else if (mission.startsWith("ssh://")) {
            return true;
        } else if (mission.startsWith("cmd://")) {
            return true;
        } else {
            return testfile(mission);
        }
    }

    public void setPageSize() {
        String tmp = kSarConfig.readSpecial("PGSZ:" + hostName);

        if (tmp != null) {
            Integer i = new Integer(tmp);
            solarispagesize = i.intValue();
        } 
    }

    public void getUserPref() {
        String prefGraph = new String(kSarConfig.readSpecial("PDF:" + hostName));
        if (prefGraph != null && prefGraph.length() > 3) {
            for (Iterator<String> it = pdfList.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                AllGraph value = pdfList.get(key);
                if (prefGraph.indexOf(" " + key.toString() + " ") > 0) {
                    printList.put(key.toString(), pdfList.get(key.toString()));
                }
            }
        } else {
            printList = pdfList;
        }
    }

    public void getGraphList(String graphlist) {
        if (graphlist == null) {
            printList.putAll(pdfList);
            return;
        }
        if (graphlist.length() < 2) {
            printList.putAll(pdfList);
            return;
        }
        for (Iterator<String> it = pdfList.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            AllGraph value = pdfList.get(key);
            if (graphlist.indexOf(" " + key.toString() + " ") >= 0) {
                //System.out.println("add: " + key.toString());
                printList.put(key.toString(), value);
            }
        }
    }

    public int outputCsv(String csvfilename) {
        // open file
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(csvfilename));
        } catch (IOException e) {
            out = null;
        }

        try {
            // Header
            out.write("Date;");
            TreeMap<String,TimeSeries> tmphash = new TreeMap<String,TimeSeries>();
            tmphash.putAll(dispo);
            for (Iterator<String> it = tmphash.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                out.write(key + ";");

            }
            out.write("\n");
            // data
            // fir itate thought date
            ArrayList<Second> tmp2list = new ArrayList<Second>();
            tmp2list.addAll(datefound);
            for (Iterator<Second> it_time = tmp2list.iterator(); it_time.hasNext();) {
                Second value_time = it_time.next();                
                out.write(value_time.toString() + ";");
                for (Iterator<String> it = tmphash.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    TimeSeries value = dispo.get(key);
                    if (value.getValue(value_time) == null) {
                        out.write(";");
                    } else {
                        out.write(value.getValue(value_time) + ";");
                    }
                }
                out.write("\n");
            }
            out.close();
        } catch (IOException e) {
            out = null;
        }

        return 0;
    }

    public int outputPdf(String pdffilename, boolean usepref, String graphlist) {
        if (usepref) {
            getUserPref();
        } else {
            getGraphList(graphlist);
        }

        PdfExport t = new PdfExport(pdffilename, printList, null, null, (kSar) this);
        t.run();
        return 0;
    }

    public int outputJpg(String basefilename, boolean usepref, boolean dohtml, String graphlist, int width, int height) {
        if (usepref) {
            getUserPref();
        } else {
            getGraphList(graphlist);
        }

        JpgExport t = new JpgExport(basefilename, printList, null, null, (kSar) this, dohtml, width, height);
        t.run();

        return 0;
    }

    public int outputPng(String basefilename, boolean usepref, boolean dohtml, String graphlist, int width, int height) {
        if (usepref) {
            getUserPref();
        } else {
            getGraphList(graphlist);
        }

        PngExport t = new PngExport(basefilename, printList, null, null, (kSar) this, dohtml, width, height);
        t.run();

        return 0;
    }

    public String calendarinfo() {
        if (statstart == null) {
            return "";
        }
        StringBuffer tmpstr = new StringBuffer("\nTime range information:\n");
        tmpstr.append("First data point: " + statstart + "\n");
        tmpstr.append("Last data point: " + statend + "\n");
        tmpstr.append("\nGraph range:\n");
        tmpstr.append("First data point: " + startofgraph + "\n");
        tmpstr.append("Last data point: " + endofgraph + "\n");
        return tmpstr.toString();
    }

    public void refreshdisktree() {
        parseAlternatediskname();
        Enumeration mydisklist = diskstreenode.children();
        while (mydisklist.hasMoreElements()) {
            DefaultMutableTreeNode curtree = (DefaultMutableTreeNode) mydisklist.nextElement();
            String tmp = (String) Adiskname.get(curtree.toString());
            if (tmp != null) {
                curtree.setUserObject(tmp);
            }
        }
    }

    public void selectrange(String _startdate, String _enddate) {
        if (_startdate != null) {
            for (int i = 0; i < datefound.size(); i++) {
                String tmp = datefound.get(i).toString();
                if (tmp.indexOf(_startdate) >= 0) {
                    startofgraph = (Second) datefound.get(i);
                    break;
                }
            }
        }
        if (_enddate != null) {
            for (int i = datefound.size() - 1; i > 0; i--) {
                String tmp = datefound.get(i).toString();
                if (tmp.indexOf(_enddate) >= 0) {
                    endofgraph = (Second) datefound.get(i);
                    break;
                }
            }
        }
        return;
    }

    public void parseAlternatediskname() {
        String tmp = kSarConfig.readSpecial("ADISK:" + hostName);
        if (tmp == null) {
            return;
        }
        String [] tmp2 = tmp.split("!");
        for (int i=0; i < tmp2.length; i++) {
            String tmpstr = tmp2[i];
            String val[] = tmpstr.split("=");
            if (val.length != 2) {
                continue;
            }
            Adiskname.put(val[0], val[1]);
        }
    }

    
    
    
    public void resetInfo() {
        // remake the tree
        //graphtree = new DefaultMutableTreeNode("kSar");
        if ( myUI != null ) {
            myUI.setTitle("Empty");
            myUI.selecttimemenu.setEnabled(false);
            myUI.exportpdfmenu.setEnabled(false);
            myUI.exportjpgmenu.setEnabled(false);
            myUI.exportpngmenu.setEnabled(false);
            myUI.disknamemenu.setEnabled(false);
            myUI.menushowstackedmem.setEnabled(false);
            myUI.exportcsvmenu.setEnabled(false);
            myUI.obj2 = null;
            myUI.reset2tree();
            //setshowstackedcpu(false);
            myUI.showtriggermenu.setSelected(showtrigger);
            myUI.chkbox_cpuused.setSelected(showstackedcpu);
            myUI.memusedbufadj.setSelected(showmemusedbuffersadjusted);
            myUI.redobutton.setEnabled(false);            
        }
        
        myOS = null;
        sarType = 0;
        kernelVersion = null;
        sarDate = null;
        day = 0;
        month = 0;
        year = 0;
        haspidnode = false;
        pidSarList.clear();
        pidstreenode = new DefaultMutableTreeNode("Pid");
        DetectedBounds.clear();
        pdfList.clear();
        isparsing = false;
        othergraphlist.clear();
        //
        disksSarList.clear();
        pdfList.clear();
        printList.clear();
        cpuSarList.clear();
        fileSarList.clear();
        msgSarList.clear();
        scallSarList.clear();
        cswchSarList.clear();
        ifaceSarList.clear();
        cpustreenode = new DefaultMutableTreeNode("CPU");
        hascpunode = false;
        diskstreenode = new DefaultMutableTreeNode("Disk");
        hasdisknode = false;
        ifacetreenode = new DefaultMutableTreeNode("Interface");
        hasifnode = false;
        //
        filetreenode = new DefaultMutableTreeNode("File");
        hasfilenode = false;
        msgtreenode = new DefaultMutableTreeNode("Message & Semaphore");
        hasmsgnode = false;
        scalltreenode = new DefaultMutableTreeNode("Syscalls");
        hasscallnode = false;
        cswchtreenode = new DefaultMutableTreeNode("Context");
        hascswchnode = false;
        //
        buffertreenode = new DefaultMutableTreeNode("Buffers");
        hasbuffernode = false;
        bufferSarList.clear();
        
        haspaging1node = false;
        paging1treenode = new DefaultMutableTreeNode("Paging1");
        paging1SarList.clear();
        
        haspaging2node = false;
        paging2treenode = new DefaultMutableTreeNode("Paging2");
        paging2SarList.clear();
        
        hasswapingnode = false;
        swapingtreenode = new DefaultMutableTreeNode("Swapping");
        swapingSarList.clear();
    
        hasttynode = false;
        ttytreenode = new DefaultMutableTreeNode("TTY");
        ttySarList.clear();
 
        haspsetnode = false;
        psettreenode = new DefaultMutableTreeNode("Pset");
        psetSarList.clear();

        hasnfsnode = false;
        nfstreenode = new DefaultMutableTreeNode("NFS"); 
        
        hasintrlistnode = false;
        intrtreenode = new DefaultMutableTreeNode("interrupt");
        intrSarlist.clear();
        
        hostName = null;
        sarType = 0;
        statstart = null;
        statend = null;
        datefound.clear();
        startofgraph = null;
        endofgraph = null;
        command_interrupted = false;
        isparsing = false;
        myOS = null;
        lastever = new Second(0, 0, 0, 1, 1, 1970);
        DetectedBounds.clear();
        solarispagesize = -1;
        othergraphlist.clear();
        // reset parser
        command_interrupted = false;
        sarParsersolaris = null;
        sarParserlinux = null;
        sarParserAix = null;
        sarParserHpux = null;
        sarParserMac = null;
        //        
    }

    
    protected void finalize() throws Throwable {
        cleanup_temp();
        super.finalize();
    }
    
    public void cleanup_temp() {
        try {
            if (tmpfile != null ) {
            tmpfile_out.close();
            tmpfile.delete();
        }
        } catch (IOException ioe) {         
        }
    }
    
    public void make_temp() {        
        try {
            if (tmpfile != null ) {
            tmpfile_out.close();
            tmpfile.delete();
        }
            tmpfile = File.createTempFile("ksar",".sartxt");
            tmpfile_out = new BufferedWriter(new FileWriter(tmpfile));
        } catch (IOException ioe) {         
        }
        if ( tmpfile != null ) {
            tmpfile.deleteOnExit();
        }
    }
    
    public void parse(BufferedReader br) {
        int parserreturn = 0;
        String thisLine;
        StringTokenizer matcher;
        String first;
        long start;
        long num_lines = 0;
        
        make_temp();
        
        if (myUI != null) {
            changemenu(false);
        }

        try {
            tell_parsing(true);
            start = System.currentTimeMillis();
            while ( (thisLine = br.readLine()) != null && ! command_interrupted) {
                num_lines++;
                //System.out.println("--:" + thisLine);
                if ( tmpfile_out != null ) {
                    tmpfile_out.write(thisLine+"\n");
                }
                //parsedfile.append(thisLine);
                // skip empty line
                if (thisLine.length() == 0) {
                    continue;
                }
                matcher = new StringTokenizer(thisLine);
                if (matcher.countTokens() == 0) {
                    continue;
                }
                // ok let's check OS version by getting first string
                first = matcher.nextToken();

                // SunOS host 5.9 Generic_118558-28 sun4u    09/01/2006
                if ( "SunOS".equals(first) ) {
                    sarType = 1;
                    if (myOS == null) {
                        myOS = new OSInfo("SunOS", "automatically");
                    }
                    hostName = matcher.nextToken();
                    myOS.setHostname(hostName);
                    osVersion = matcher.nextToken();
                    myOS.setOSversion(osVersion);
                    kernelVersion = matcher.nextToken();
                    myOS.setKernel(kernelVersion);
                    cpuType = matcher.nextToken();
                    myOS.setCpuType(cpuType);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    setPageSize();
                    //parseAlternatediskname();
                    continue;
                }
                // Linux 2.4.21-32.ELsmp (host)       09/09/06
                if ( "Linux".equals(first) ) {
                    String tmpstr;
                    sarType = 2;
                    if (myOS == null) {
                        myOS = new OSInfo("Linux", "automatically");
                    }
                    kernelVersion = matcher.nextToken();
                    myOS.setKernel(kernelVersion);
                    tmpstr = matcher.nextToken();
                    hostName = tmpstr.substring(1, tmpstr.length() - 1);
                    myOS.setHostname(hostName);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    dateSplit = sarDate.split("-");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[2]);
                        month = Integer.parseInt(dateSplit[1]);
                        year = Integer.parseInt(dateSplit[0]);
                    }
                    solarispagesize = 0;
                    parseAlternatediskname();
                    continue;
                }
                // AIX rsora1 3 4 0006488F4C00    12/18/06
                if ( "AIX".equals(first) ) {
                    String tmpstr;
                    sarType = 3;
                    if (myOS == null) {
                        myOS = new OSInfo("AIX", "automatically");
                    }
                    hostName = matcher.nextToken();
                    myOS.setHostname(hostName);
                    tmpstr = matcher.nextToken();
                    osVersion = new String(matcher.nextToken() + "." + tmpstr);
                    myOS.setOSversion(osVersion);
                    tmpstr = matcher.nextToken();
                    myOS.setMacAddress(tmpstr);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    parseAlternatediskname();
                    solarispagesize = 0;
                    continue;
                }
                //
                //
                if ("HP-UX".equals(first)) {
                    sarType = 4;
                    if (myOS == null) {
                        myOS = new OSInfo("HP-UX", "automatically");
                    }
                    hostName = matcher.nextToken();
                    myOS.setHostname(hostName);
                    osVersion = matcher.nextToken();
                    myOS.setOSversion(osVersion);
                    kernelVersion = matcher.nextToken();
                    myOS.setKernel(kernelVersion);
                    cpuType = matcher.nextToken();
                    myOS.setCpuType(cpuType);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    parseAlternatediskname();
                    continue;
                }
                //
                //
                if ("Darwin".equals(first)) {
                    sarType = 5;
                    if (myOS == null) {
                        myOS = new OSInfo("Mac", "automatically");
                    }
                    hostName = matcher.nextToken();
                    myOS.setHostname(hostName);
                    osVersion = matcher.nextToken();
                    myOS.setOSversion(osVersion);
                    cpuType = matcher.nextToken();
                    myOS.setCpuType(cpuType);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    parseAlternatediskname();
                    continue;
                }
                // SunOS host 5.9 Generic_118558-28 sun4u    09/01/2006
                if ( "Esar".equals(first) ) {
                    sarType = 6;
                    if (myOS == null) {
                        myOS = new OSInfo("Esar SunOS", "automatically");
                    }
                    // skip sunos
                    matcher.nextToken();
                    hostName = matcher.nextToken();
                    myOS.setHostname(hostName);
                    osVersion = matcher.nextToken();
                    myOS.setOSversion(osVersion);
                    kernelVersion = matcher.nextToken();
                    myOS.setKernel(kernelVersion);
                    cpuType = matcher.nextToken();
                    myOS.setCpuType(cpuType);
                    sarDate = matcher.nextToken();
                    myOS.setDate(sarDate);
                    String[] dateSplit = sarDate.split("/");
                    if (dateSplit.length == 3) {
                        day = Integer.parseInt(dateSplit[1]);
                        month = Integer.parseInt(dateSplit[0]);
                        year = Integer.parseInt(dateSplit[2]);
                        if (year < 100) { // solaris 8 show date on two digit
                            year += 2000;
                        }
                    }
                    continue;
                }
                //
                //
                if (first.equals("Average")) {
                    underaverage = 1;
                    continue;
                }
                // match the unix restart message and skip this line
                if (thisLine.indexOf("unix restarts") >= 0 || thisLine.indexOf("LINUX RESTART") >= 0 || thisLine.indexOf(" unix restared") >= 0) {
                    underaverage = 0;
                    continue;
                }

                // match the System Configuration line on AIX
                if (thisLine.indexOf("System Configuration") >= 0) {
                    continue;
                }

                if (thisLine.indexOf("State change") >= 0) {
                    underaverage = 0;
                    continue;
                }

                //
                // Getting here without sarType leads to error
                //
                if ( sarType == 0 ) {
                    if (myUI == null) {
                        // NO GUI print error and exit
                        System.err.println(parser_err1);
                        System.exit(2);
                    }
                    break;
                }
                
                if (myUI != null) {
                    if ((num_lines % 30) == 1) {
                        if (!myUI.getTitle().equals(hostName + " : " + startofgraph + " -> " + endofgraph)) {
                            myUI.setTitle(hostName + " : " + startofgraph + " -> " + endofgraph);
                        }
                    }
                }
                
                //
                // continue with specified parser
                //
                if ( sarType == 1) {
                    if (sarParsersolaris == null) {
                        sarParsersolaris = new net.atomique.ksar.Solaris.Parser(this);
                    }
                    parserreturn = sarParsersolaris.parse(thisLine, first, matcher);
                    continue;
                } else if ( sarType == 2 ) {
                    if (sarParserlinux == null) {
                        sarParserlinux = new net.atomique.ksar.Linux.Parser(this);
                    }
                    parserreturn = sarParserlinux.parse(thisLine, first, matcher);
                    continue;
                } else if ( sarType == 3 ) {
                    if (sarParserAix == null) {
                        sarParserAix = new net.atomique.ksar.AIX.Parser(this);
                    }
                    parserreturn = sarParserAix.parse(thisLine, first, matcher);
                    continue;
                } else if ( sarType == 4 ) {
                    if (sarParserHpux == null) {
                        sarParserHpux = new net.atomique.ksar.Hpux.Parser(this);
                    }
                    parserreturn = sarParserHpux.parse(thisLine, first, matcher);
                    continue;
                } else if ( sarType == 5 ) {
                    if (sarParserMac == null) {
                        sarParserMac = new net.atomique.ksar.Mac.Parser(this);
                    }
                    parserreturn = sarParserMac.parse(thisLine, first, matcher);
                    continue;
                } else if ( sarType == 6 ) {
                    if (sarParserEsar == null) {
                        sarParserEsar = new net.atomique.ksar.Esar.Parser(this);
                    }
                    parserreturn = sarParserEsar.parse(thisLine, first, matcher);
                    continue;
                }
            }
            // end of while
            long elapsedTimeMillis = System.currentTimeMillis() - start;
            System.out.print("time to parse: " + elapsedTimeMillis + "ms ");
            System.out.print("number of line: " + num_lines + " ");
            System.out.println("line/msec: " + (float) (num_lines / elapsedTimeMillis));

        } catch (IOException ioe) {
            if (!command_interrupted) {
                System.err.println("ouch something bad has append");
            }
        }
        tell_parsing(false);
        if ( sarType == 0 ) {
            if (myUI == null) {
                System.err.println(parser_err1);
                System.exit(2);
            } else {
                JOptionPane.showMessageDialog(myUI, parser_err1, "Parser error", JOptionPane.ERROR_MESSAGE);
                resetInfo();
                //myUI.jTree1.setModel(new DefaultTreeModel(graphtree));
                //myUI.jTree1.setSelectionPath(new TreePath(graphtree.getRoot()));
                myUI.setTitle("Empty");
            }
        } else {
            doclosetrigger();
            if (myUI != null) {
                //myUI.jTree1.setModel(new DefaultTreeModel(graphtree));
                //myUI.jTree1.setSelectionPath(new TreePath(graphtree.getRoot()));
                //myUI.home2tree();
                myUI.trySelectByPathString(this.selectionPath);
                changemenu(true);
                myUI.setTitle(hostName + " : " + startofgraph + " -> " + endofgraph);
            }
        }
    }

    private void tell_parsing(final boolean val) {
        isparsing = val;
        if ( myUI == null ) {
            return;
        }
        if (val) {
            myUI.redobutton.setText("Stop");
            myUI.redobutton.setEnabled(true);
        } else {
            myUI.redobutton.setText("Redo");
            myUI.redobutton.setEnabled(true);
        }
    }

    public void changemenu(boolean val) {
        myUI.selecttimemenu.setEnabled(val);
        myUI.exportpdfmenu.setEnabled(val);
        myUI.exportjpgmenu.setEnabled(val);
        myUI.exportpngmenu.setEnabled(val);
        myUI.disknamemenu.setEnabled(val);
        myUI.menushowstackedmem.setEnabled(val);
        myUI.chkbox_stackintr.setEnabled(val);
        myUI.exportcsvmenu.setEnabled(val);
        myUI.redobutton.setEnabled(val);
        myUI.addtoauto.setEnabled(val);
        myUI.exporttxtmenu.setEnabled(val);
        command_interrupted = false;
    }

    private void doclosetrigger() {
        for (Iterator<String> it = pdfList.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            AllGraph value = pdfList.get(key);
            value.doclosetrigger();
        }
    }

    public void addGUI(String title) {
        myUI = new kSarUI(this);
        myUI.setTitle(title);
        myUI.toFront();
        myUI.setVisible(true);
        mydesktop.desktopPane.add(myUI);
        try {
            int num = mydesktop.desktopPane.getAllFrames().length;
            if (num != 1) {
                myUI.reshape(5 * num, 5 * num, 800, 600);
            } else {
                myUI.reshape(0, 0, 800, 600);
            }
            myUI.setSelected(true);
        } catch (PropertyVetoException vetoe) {
        }
    }

    public void add2tree(DefaultMutableTreeNode parent, DefaultMutableTreeNode newNode) {
        if ( myUI != null) {
            myUI.add2tree(parent,newNode);
        }
    }
    
    public void remove2tree(DefaultMutableTreeNode oldNode) {
        if ( myUI != null ) {
            myUI.remove2tree(oldNode);
        }
    }
    
    public int showGraphName() {
        for (Iterator<String> it = pdfList.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            AllGraph value = pdfList.get(key);
        }
        return 0;
    }
    //
    public boolean isparsing = false;
    // type of sar data
    int  sarType = 0;
    // keep track of OSINFO
    public OSInfo myOS = null;
    // this is for the GUI
    public kSarUI myUI = null;
    public kSarDesktop mydesktop = null;
    public DefaultMutableTreeNode graphtree = new DefaultMutableTreeNode("kSar");
    // local info
    public String hostName = null;
    public String osVersion = null;
    public String kernelVersion = null;
    public String cpuType = null;
    public String sarDate = null;
    public int day = 0;
    public int month = 0;
    public int year = 0;
    public int underaverage = 0;
    // parser info
    public String statstart = null;
    //public String graphstart = null;
    public Second startofgraph = null;
    //
    public ArrayList<Second> datefound = new ArrayList<Second>();
    //public HashMap calendarSar = new HashMap();
    //public DefaultListModel timeList = new DefaultListModel();
    public Second lastever = new Second(0, 0, 0, 1, 1, 1970);
    //
    public String statend = null;
    //public String graphend = null;
    public Second endofgraph = null;
    // trigger
    public HashMap<String,AllGraph> DetectedBounds = new HashMap<String,AllGraph>();
    public boolean showtrigger = false;
    public boolean showstackedcpu = false;
    public boolean show100axiscpu = false;
    public boolean showemptydisk = true;
    public boolean showstackedmem = false;
    // pdf
    public HashMap<String,AllGraph> pdfList = new HashMap<String,AllGraph>();
    // Specific graph
    public HashMap<String,TimeSeries> dispo = new HashMap<String,TimeSeries>();
    public HashMap othergraphlist = new HashMap();
    // solaris page size
    public int solarispagesize = -1;
    // disk list
    public boolean hasdisknode = false;
    public DefaultMutableTreeNode diskstreenode = new DefaultMutableTreeNode("Disk");
    public HashMap<String,AllGraph> disksSarList = new HashMap<String,AllGraph>();
    //
    public HashMap<String,diskName> AlternateDiskName = new HashMap<String,diskName>();
    public HashMap<String,String> Adiskname = new HashMap<String,String>();
    // pid list
    public boolean haspidnode = false;
    public DefaultMutableTreeNode pidstreenode = new DefaultMutableTreeNode("Pid");
    public HashMap<String,AllGraph> pidSarList = new HashMap<String,AllGraph>();
    // cpu list
    public boolean hascpunode = false;
    public DefaultMutableTreeNode cpustreenode = new DefaultMutableTreeNode("Cpu");
    public HashMap<String,AllGraph> cpuSarList = new HashMap<String,AllGraph>();

    // interface list
    public boolean hasifnode = false;
    public DefaultMutableTreeNode ifacetreenode = new DefaultMutableTreeNode("Interface");
    public HashMap<String,AllGraph> ifaceSarList = new HashMap<String,AllGraph>();
    //
    static final String parser_err1 = "There was a problem while parsing stat";
    static final String parser_err2 = "Sorry i cannot parse this file using the command line";
    static final String parser_end = "Data import is finished";
    static final String parser_solarispagesize = "\nDon't forget to set PageSize (unders Option Menu)";
    // linux
    public boolean showmemusedbuffersadjusted = false;
    public String reload_command = new String("Empty");
    // print
    //
    public HashMap<String,AllGraph> printList = new HashMap<String,AllGraph>();
    public boolean okforprinting = false;
    // Aix file
    public boolean hasfilenode = false;
    public DefaultMutableTreeNode filetreenode = new DefaultMutableTreeNode("File");
    public HashMap<String,AllGraph> fileSarList = new HashMap<String,AllGraph>();
    // Aix msg
    public boolean hasmsgnode = false;
    public DefaultMutableTreeNode msgtreenode = new DefaultMutableTreeNode("Message & Semaphore");
    public HashMap<String,AllGraph> msgSarList = new HashMap<String,AllGraph>();
    // Aix cswch
    public boolean hascswchnode = false;
    public DefaultMutableTreeNode cswchtreenode = new DefaultMutableTreeNode("Context");
    public HashMap<String,AllGraph> cswchSarList = new HashMap<String,AllGraph>();
    // Aix syscall
    public boolean hasscallnode = false;
    public DefaultMutableTreeNode scalltreenode = new DefaultMutableTreeNode("Syscalls");
    public HashMap<String,AllGraph> scallSarList = new HashMap<String,AllGraph>();
    Thread launched_command = null;
    public boolean command_interrupted = false;
    // Esar buffer
    public boolean hasbuffernode = false;
    public DefaultMutableTreeNode buffertreenode = new DefaultMutableTreeNode("Buffers");
    public HashMap<String,AllGraph> bufferSarList = new HashMap<String,AllGraph>();
    // Esar paging1
    public boolean haspaging1node = false;
    public DefaultMutableTreeNode paging1treenode = new DefaultMutableTreeNode("Paging1");
    public HashMap<String,AllGraph> paging1SarList = new HashMap<String,AllGraph>();
    // Esar paging2
    public boolean haspaging2node = false;
    public DefaultMutableTreeNode paging2treenode = new DefaultMutableTreeNode("Paging2");
    public HashMap<String,AllGraph> paging2SarList = new HashMap<String,AllGraph>();
    // Esar paging2
    public boolean hasswapingnode = false;
    public DefaultMutableTreeNode swapingtreenode = new DefaultMutableTreeNode("Swapping");
    public HashMap<String,AllGraph> swapingSarList = new HashMap<String,AllGraph>();
    // Esar tty
    public boolean hasttynode = false;
    public DefaultMutableTreeNode ttytreenode = new DefaultMutableTreeNode("TTY");
    public HashMap<String,AllGraph> ttySarList = new HashMap<String,AllGraph>();
    // Esar pset
    public boolean haspsetnode = false;
    public DefaultMutableTreeNode psettreenode = new DefaultMutableTreeNode("Pset");
    public HashMap<String,AllGraph> psetSarList = new HashMap<String,AllGraph>();
    // Esar nfs
    public boolean hasnfsnode = false;
    public DefaultMutableTreeNode nfstreenode = new DefaultMutableTreeNode("NFS");
    // intr list
    public boolean hasintrlistnode = false;
    public HashMap<String,AllGraph> intrSarlist = new HashMap<String,AllGraph>();
    public DefaultMutableTreeNode intrtreenode = new DefaultMutableTreeNode("Interrupt");
    public boolean showstackedintrlist=false;
    //
    // Parser
    net.atomique.ksar.Solaris.Parser sarParsersolaris = null;
    net.atomique.ksar.Linux.Parser sarParserlinux = null;
    net.atomique.ksar.AIX.Parser sarParserAix = null;
    net.atomique.ksar.Hpux.Parser sarParserHpux = null;
    net.atomique.ksar.Mac.Parser sarParserMac = null;
    net.atomique.ksar.Esar.Parser sarParserEsar = null;
    //
    net.atomique.ksar.Ps.ProcessList pslist = null;
    //
    public File tmpfile=null;
    public BufferedWriter tmpfile_out =null;
}
