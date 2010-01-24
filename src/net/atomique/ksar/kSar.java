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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

/**
 *
 * @author alex, Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class kSar {

    private final static int TIMER_INTERVAL = 300000;
    
    private final kSarInstanceConfig config;
    private final Timer timer;
    private final IMessageCreator messageCreator;
    private final Semaphore semaphore;
    private String selectionPath;
    private SwingWorker<Reader, Void> swingWorker;
    
    
    private kSar() {
        this.config = new kSarInstanceConfig();
        this.messageCreator = new JOptionPaneMessageCreator(myUI);
        
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (!reload_command.equals("Empty")) {
                    selectionPath = myUI.getSelectionPath();
                    updateDataByCommand(reload_command);
                }
            }
            
        }, 0, TIMER_INTERVAL);
        
        this.semaphore = new Semaphore(1, true);
    }
    
    public kSar(String title) {
        this();
        
        if (!validateRedoCommand(title)) {
            System.err.println("Cannot process input: " + title);
            return;
        }
        
        updateDataByCommand(title);
    }

    public kSar(kSarDesktop hisdesktop, String title) {
        this();
        
        mydesktop = hisdesktop;
        
        if (!validateRedoCommand(title)) {
            System.err.println("Cannot process input: " + title);
            return;
        }
        
        addGUI(title);
        updateDataByCommand(title);
    }
    
    
    public synchronized boolean isUpdateInProgress() {
        return (this.semaphore.availablePermits() == 0);
    }
    
    public synchronized void cancelUpdate() {
        if (this.swingWorker != null) {
            this.swingWorker.cancel(true);
        }
    }
    
    private <T extends IDataRetriever> void runUpdate(final T dataRetriever, 
                                                      final IDataRetrievingSuccessfulHandler<T> handler) {
        if (myUI != null) {
            this.changemenu(false);
        }

        this.tell_parsing(true);
        
        this.swingWorker = new SwingWorker<Reader, Void>() {

            @Override
            protected Reader doInBackground() throws Exception {
                semaphore.acquire();
                
                return dataRetriever.getData();
            }
            
            @Override
            protected void done() {
                try {
                    if (isCancelled()) {
                        resetInfo();
                        return;
                    }
                    
                    updateData(get());
                    handler.afterCompleted(dataRetriever);
                    reload_command = dataRetriever.getRedoCommand();
                    
                    doclosetrigger();
                    myUI.trySelectByPathString(selectionPath);
                    changemenu(true);
                    myUI.setTitle(hostName + " : " + startofgraph + " -> " + endofgraph);
                }
                catch (InterruptedException ex) {
                    // interuptions should be logged in future
                }
                catch (ParsingException ex) {
                    messageCreator.showErrorMessage("Error", ex.getMessage());
                }
                catch (IOException ex) {
                    messageCreator.showErrorMessage("Error", ex.getMessage());
                }
                catch (ExecutionException ex) {
                    if (ex.getCause() instanceof DataRetrievingFailedException) {
                        messageCreator.showErrorMessage("Error", ex.getCause().getMessage());
                    }
                    else {
                        ex.getCause().printStackTrace();
                        messageCreator.showErrorMessage("Error", "An unexpected error occured.");
                    }
                }
                finally {
                    semaphore.release();
                    
                    tell_parsing(false);
                }
            }
        };
        
        this.swingWorker.execute();
    }
    
    private void updateData(Reader reader) throws IOException, ParsingException {
        if (reader == null) {
            throw new IOException("No data found.");
        }
        
        BufferedReader bufferedReader = null;
        
        try {    
            bufferedReader = new BufferedReader(reader);
            this.resetInfo();
            this.parse(bufferedReader);
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException ex) {
                    // exception could hide any exception thrown in outer try block
                }
            }
        }
    }
    
    public void readDataFromFile(String filename) {
        FileSystemDataRetriever dataRetriever = ((filename == null) ? new FileSystemDataRetriever(this.config.getLastFile(), true)
                                                                    : new FileSystemDataRetriever(new File(filename), false));
        
        this.runUpdate(dataRetriever,
                       new IDataRetrievingSuccessfulHandler<FileSystemDataRetriever>() {

                            @Override
                            public void afterCompleted(FileSystemDataRetriever dataRetriever) {
                                config.setLastFile(dataRetriever.getSarFile());
                            }
                       });
    }

    public void readDataFromSsh(String cmd) {
        SshDataRetriever dataRetriever = ((cmd == null) ? new SshDataRetriever(this.config.getLastSshServer(), 
                                                                               this.config.getLastSshCommand(), 
                                                                               this.messageCreator)
                                                        : new SshDataRetriever(cmd, this.messageCreator));
        
        this.runUpdate(dataRetriever,
                       new IDataRetrievingSuccessfulHandler<SshDataRetriever>() {

                            @Override
                            public void afterCompleted(SshDataRetriever dataRetriever) {
                                config.setLastSshCommand(dataRetriever.getCommand());
                                config.setLastSshServer(dataRetriever.getServer());
                            }
                       });
    }
    
    public void executeLocalCommand(String cmd) {
        LocalProcessDataRetriever dataRetriever = ((cmd == null) ? new LocalProcessDataRetriever(this.config.getLastCommand(), true)
                                                                 : new LocalProcessDataRetriever(cmd, false));
        
        this.runUpdate(dataRetriever,
                       new IDataRetrievingSuccessfulHandler<LocalProcessDataRetriever>() {

                            @Override
                            public void afterCompleted(LocalProcessDataRetriever dataRetriever) {
                                config.setLastCommand(dataRetriever.getCommand());
                            }
                       });
    }

    public void updateDataByCommand(String command) {
        if (command.equals("Empty")) {
            return;
        }
        else if (command.startsWith("file://")) {
            String filename = new String(command.substring(7));
            readDataFromFile(filename);
        }        
        else if (command.startsWith("cmd://")) {
            String commandname = new String(command.substring(6));
            get_processlist(command);
            executeLocalCommand(commandname);
        }
        else if (command.startsWith("ssh://")) {
            String commandname = new String(command.substring(6));
            get_processlist(command);
            readDataFromSsh(commandname);
        }
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

    public boolean validateRedoCommand(String command) {
        if (command.startsWith("file://")) {
            String[] spilltedCommand = command.split("\\w+://");
            return new File(spilltedCommand[1]).exists();
        }
        
        return true;
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
            myUI.showtriggermenu.setSelected(showtrigger);
            myUI.chkbox_cpuused.setSelected(showstackedcpu);
            myUI.memusedbufadj.setSelected(showmemusedbuffersadjusted);
            myUI.redobutton.setEnabled(false);            
        }
        
        myOS = null;
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
        statstart = null;
        statend = null;
        datefound.clear();
        startofgraph = null;
        endofgraph = null;
        isparsing = false;
        myOS = null;
        lastever = new Second(0, 0, 0, 1, 1, 1970);
        DetectedBounds.clear();
        solarispagesize = -1;
        othergraphlist.clear();
    }

    /**
     * Parses data provided by DataRetriever
     * @param reader Reader provided by DataRetriever
     * @throws IOException If reading from stream fails
     * @throws ParsingException If parsing fails
     */
    private void parse(BufferedReader reader) throws IOException, ParsingException {
        long start;
        long lineCount = 0;
        IOsSpecificParser parser = null;
        
        start = System.currentTimeMillis();
        String line = null;
        
        try {
            while ((line = reader.readLine()) != null) {
                lineCount++;
                
                // skip empty line
                if (line.length() == 0) {
                    continue;
                }
                
                StringTokenizer matcher = new StringTokenizer(line);
                
                if (matcher.countTokens() == 0) {
                    continue;
                }
                
                String first = matcher.nextToken();
                
                if (first.equals("Average")) {
                    underaverage = 1;
                    continue;
                }
                
                // match the unix restart message and skip this line
                if (line.indexOf("unix restarts") >= 0 
                        || line.indexOf("LINUX RESTART") >= 0 
                        || line.indexOf(" unix restared") >= 0) {
                    underaverage = 0;
                    continue;
                }

                // match the System Configuration line on AIX
                if (line.indexOf("System Configuration") >= 0) {
                    continue;
                }

                if (line.indexOf("State change") >= 0) {
                    underaverage = 0;
                    continue;
                }
                
                if (parser == null) {
                    
                    if (first.equals(SarType.SunOS.getParsingString())) {
                        parser = new net.atomique.ksar.Solaris.Parser(this);
                    }
                    else if (first.equals(SarType.Darwin.getParsingString())) {
                        parser = new net.atomique.ksar.Mac.Parser(this);
                    }
                    else if (first.equals(SarType.Linux.getParsingString())) {
                        parser = new net.atomique.ksar.Linux.Parser(this);
                    }
                    else if (first.equals(SarType.HP_UX.getParsingString())) {
                        parser = new net.atomique.ksar.Hpux.Parser(this);
                    }
                    else if (first.equals(SarType.AIX.getParsingString())) {
                        parser = new net.atomique.ksar.AIX.Parser(this);
                    }
                    else if (first.equals(SarType.Esar.getParsingString())) {
                        parser = new net.atomique.ksar.Esar.Parser(this);
                    }
                    else {
                        throw new ParsingException(parser_err1);
                    }
                    
                    parser.parseOsInfo(matcher);
                }
                
                parser.parse(line, first, matcher);
            }
        }
        finally {
            System.out.println("Line count: " + lineCount);
        }
        
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        System.out.print("time to parse: " + elapsedTimeMillis + "ms ");
        System.out.print("number of line: " + lineCount + " ");
        System.out.println("line/msec: " + (float) (lineCount / elapsedTimeMillis));
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
    // keep track of OSINFO
    public OSInfo myOS = null;
    // this is for the GUI
    public kSarUI myUI = null;
    public kSarDesktop mydesktop = null;
    public DefaultMutableTreeNode graphtree = new DefaultMutableTreeNode("kSar");
    // local info
    public String hostName = null;
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
    net.atomique.ksar.Ps.ProcessList pslist = null;
    
    private interface IDataRetrievingSuccessfulHandler<T extends IDataRetriever> {
        
        void afterCompleted(T dataRetriever);
    }
}
