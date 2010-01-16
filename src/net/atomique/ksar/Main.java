/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 *
 * @author alex
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        int i = 0;
        String arg;
/*
        Locale[] locales1 = Locale.getAvailableLocales();
                    for (i=0; i < locales1.length; i++ ) {
                        System.out.println("" + locales1[i].getDisplayName() + " " + locales1[i].getCountry());
                    }
        System.exit(0);
        */
        if (args.length > 0) {
            while (i < args.length && args[i].startsWith("-")) {
                arg = args[i++];
                // HELP
                if ("-help".equals(arg)) {
                    usage();
                    continue;
                }
                if ("-input".equals(arg)) {
                    if (i < args.length) {
                        inputfile.add(args[i++]);
                    } else {
                        exit_error("-input requires an option");
                    }
                    continue;
                }
                if ("-graph".equals(arg)) {
                    if (i < args.length) {
                        graphs = new String(" " + args[i++] + " ");
                    } else {
                        exit_error("-graph requires a graph list");
                    }
                    continue;
                }
                // SHOW CPU STACKED
                if ("-showCPUstacked".equals(arg)) {
                    usecpustacked = true;
                    continue;
                }
                // KB MEME STACKED
                if ("-showMEMstacked".equals(arg)) {
                    usememorystacked = true;
                    continue;
                }
                // SHOW CPU WITH AXIS 0-100
                if ("-cpuFixedAxis".equals(arg)) {
                    usecpufixedaxis = true;
                    continue;
                }
                // SHOW INTERRUPT AS STACKED
                if ("-showIntrListstacked".equals(arg)) {
                    useintrliststacked = true;
                    continue;
                }
                // SHOW TRIGGER
                if ("-showTrigger".equals(arg)) {
                    usetrigger = true;
                    continue;
                }
                // SHOW EMPTYDISK
                if ("-noEmptyDisk".equals(arg)) {
                    useemptydisk = false;
                    continue;
                }
                // DOTILE
                if ("-tile".equals(arg)) {
                    dotile = true;
                    continue;
                }
                //USEPREF
                if ("-userPrefs".equals(arg)) {
                    usepref = true;
                    continue;
                }
                // SHOWONLYGRAPHNAME
                if ("-showOnlygraphName".equals(arg)) {
                    onlygraphname = true;
                    continue;
                }
                // ADD HTML
                if ("-addHTML".equals(arg)) {
                    addhtml = true;
                    continue;
                }
                // OUTPUTPDF
                if ("-output".equals(arg) || "-outputPDF".equals(arg)) {
                    if (i < args.length) {
                        outputpdffile = args[i++];
                        if (new File(outputpdffile).exists()) {
                            exit_error("The output filename already exist.....exiting");
                        }
                        if ( "-output".equals(arg) ) {
                            System.err.println("please use -outputPDF from now");
                        }
                    } else {
                        exit_error("-output|-outputPDF requires a filename");
                    }
                    continue;
                }
                // OUTPUTJPG
                if ("-outputJPG".equals(arg)) {
                    if (i < args.length) {
                        outputjpgbase = args[i++];
                    } else {
                        exit_error("-outputJPG requires a base filename");
                    }
                    continue;
                }
                // OUTPUTPNG
                if ("-outputPNG".equals(arg)) {
                    if (i < args.length) {
                        outputpngbase = args[i++];
                    } else {
                        exit_error("-outputPNG requires a base filename");
                    }
                    continue;
                }
                // OUTPUTPNG
                if ("-outputCSV".equals(arg)) {
                    if (i < args.length) {
                        outputcsvfile = args[i++];
                    } else {
                        exit_error("-outputCSV requires a base filename");
                    }
                    continue;
                }
                // WIDTH
                if ("-width".equals(arg)) {
                    if (i < args.length) {
                        try {
                            width = Integer.valueOf(args[i++]);
                        } catch (NumberFormatException e) {
                            exit_error("-width and -height must be followed by an integer value");
                        }
                    } else {
                        exit_error("-height and -width requires a integer value");
                    }
                    continue;
                }
                // HEIGHT
                if ("-height".equals(arg)) {
                    if (i < args.length) {
                        try {
                            height = Integer.valueOf(args[i++]);
                        } catch (NumberFormatException e) {
                            exit_error("-width and -height must be followed by an integer value");
                        }
                    } else {
                        exit_error("-height and -width requires a integer value");
                    }
                    continue;
                }
                // solarisPagesize
                if ("-solarisPagesize".equals(arg)) {
                    if (i < args.length) {
                        try {
                            pagesize = Integer.valueOf(args[i++]);
                        } catch (NumberFormatException e) {
                            exit_error("-solarisPagesize must be followed by an integer value");
                        }
                    } else {
                        exit_error("-solarisPagesize requires a integer value");
                    }
                    continue;
                }
                if ("-startdate".equals(arg)) {
                    if (i < args.length) {
                        startdate = args[i++];
                    } else {
                        exit_error("startdate requires an argument");
                    }
                    continue;
                }
                if ("-enddate".equals(arg)) {
                    if (i < args.length) {
                        enddate = args[i++];
                    } else {
                        exit_error("-enddate requires an argument");
                    }
                    continue;
                }
                if ("-flushPrefs".equals(arg)) {
                    if (i < args.length) {
                        kSarConfig.flush_prefs(args[i++]);
                        kSarConfig.writeDefault();
                        System.exit(0);
                    } else {
                        exit_error("-flushPrefs requires an argument");
                    }
                }
                if ("-ClearAllPrefs".equals(arg)) {
                    kSarConfig.clear_all();
                    continue;
                }
                if ("-wizard".equals(arg)) {
                    use_wizard = true;
                    continue;
                }
                if ( "-startup".equals(arg) ) {
                    open_at_startup=true;
                    continue;
                }
                if ("-version".equals(arg)) {
                    show_version();
                }
                if ("-replaceShortcut".equals(arg)) {
                    if (i < args.length) {
                        xmlfile = args[i++];
                        tmpfile = new File(xmlfile);
                        if ( ! tmpfile.canRead() || ! tmpfile.isFile() ) {
                            xmlfile = null;
                            exit_error("Unable to open xml file (" + xmlfile + ")");
                        }
                        replaceshortcut = true;
                    } else {
                        exit_error("-replaceShortcut requires a filename");
                    }
                    continue;
                }
                if ("-addShortcut".equals(arg)) {
                    if (i < args.length) {
                        xmlfile = args[i++];
                        tmpfile = new File(xmlfile);
                        if ( ! tmpfile.canRead()  || ! tmpfile.isFile() ) {
                            xmlfile = null;
                            exit_error("Unable to open xml file (" + xmlfile + ")");
                        }
                    } else {
                        exit_error("-addShortcut requires a filename");
                    }
                    continue;
                }
                if ("-showLocale".equals(arg)) {                
                    Locale[] locales = Locale.getAvailableLocales();
                    for (i=0; i < locales.length; i++ ) {
                        System.out.println("" + locales[i].getCountry());
                    }
                    exit_error("debug");
                    continue;
                }
                exit_error("unknown argument: " + arg);
            }
        }

        // test if output was asked
        if (outputpdffile != null || outputjpgbase != null || outputpngbase != null || outputcsvfile != null) {
            // output given
            nogui = true;
            // no input file
            if (inputfile.size() > 1 || inputfile == null || inputfile.size() < 0) {
                exit_error("-input must be specify once for using -output|outputPDF|outputPNG|outputJPG");
            }
            // no good choice for output
            if (outputcsvfile != null && outputpdffile != null && outputjpgbase != null && outputpngbase != null && onlygraphname) {
                exit_error("You cannot specify -outputPDF -outpufPNG -output -outputJPG -outputCSV -showOnlygraphName together");
            }
        }
        //
        // shortcut editing
        if (xmlfile != null) {
            XMLConfig tmp = new XMLConfig(xmlfile, replaceshortcut);
            tmp = null;
        }
        // NOGUI
        if ( ! nogui ) {
            start_gui();
        } else {
            System.setProperty("java.awt.headless", "true");
            final ListIterator listItr = inputfile.listIterator();
            while (listItr.hasNext()) {
                kSar mysar = new kSar(listItr.next().toString());

                if (usetrigger) {
                    mysar.showtrigger = true;
                }
                if ( ! useemptydisk ) {
                    mysar.showemptydisk = false;
                }
                if (usecpustacked) {
                    mysar.showstackedcpu = true;
                }
                if (usecpufixedaxis) {
                    mysar.show100axiscpu = true;
                }
                if ( useintrliststacked ) {
                    mysar.showstackedintrlist=true;
                }
                try {
                    mysar.launched_command.join();
                } catch (Exception e) {
                    
                }
                if ( outputpdffile != null) {
                    mysar.outputPdf(outputpdffile, usepref, graphs);
                }
                if ( outputjpgbase != null) {
                    mysar.outputJpg(outputjpgbase, usepref, addhtml, graphs, height, width);
                }
                if ( outputpngbase != null) {
                    mysar.outputPng(outputpngbase, usepref, addhtml, graphs, height, width);
                }
                if ( outputcsvfile != null) {
                    mysar.outputCsv(outputcsvfile);
                }
                
            }

        }

    }

    public static void show_version() {
        System.out.println("kSar version: " + VersionNumber.getVersionNumber());
        System.exit(0);
    }

    public static void usage() {
        System.out.println("kSar version: " + VersionNumber.getVersionNumber());
        System.err.println("Usage: ");
        System.err.println("-version: show kSar version number");
        System.err.println("-help: show this help");
        System.err.println("-input <arg>: argument must be either ssh://user@host/command or cmd://command or file://path/to/file or just /path/to/file");
        System.err.println("-graph <graph list>: space separated list of graph you want to be outputed");
        System.err.println("-showCPUstacked: will make the CPU used graph as stacked");
        System.err.println("-showMEMstacked: will make the Memory graph as stacked (linux only)");
        System.err.println("-cpuFixedAxis:  will graph CPU used with fixed axis from 0% to 100%");
        System.err.println("-showIntrListstacked : will make the Interrupt List graph as stacked");
        System.err.println("-showTrigger:  will show trigger on graph (disabled by default)");
        System.err.println("-noEmptyDisk: will not export disk with no data");
        System.err.println("-tile: will tile window");
        System.err.println("-userPrefs: will use the userPrefs for outputing graphs (last export of this host)");
        System.err.println("-showOnlygraphName: will only print graph name available for that data (to be use for -graph)");
        System.err.println("-addHTML: will create an html page with PNG/JPG image");
        System.err.println("-outputPDF <pdf file> : output the pdf report to the pdf file");
        System.err.println("-outputPNG <base filename> : output the graphs to PNG file using argument as base filename");
        System.err.println("-outputJPG <base filename> : output the graphs to JPG file using argument as base filename");
        System.err.println("-outputCSV <CSV file> : output the CSV file");
        System.err.println("-width <size> : make JPG/PNG with specified width size (default: 800)");
        System.err.println("-height <size> : make JPG/PNG with specified height size (default: 600)");
        System.err.println("-startdate <date> : will graph the range begining at that time");
        System.err.println("-enddate <date> : will graph the range until that date");
        System.err.println("-solarisPagesize <pagesize in B>: will set solaris pagesize");
        System.err.println("-wizard: open with unified login popup");
        System.err.println("-replaceShortcut <xml file>: replace all shortcut with those in the xml file");
        System.err.println("-addShortcut <xml file>: add shortcut from the xml file");
        System.err.println("-startup: open window marked for opening at startup");
        System.exit(0);
    }

    public static void exit_error(final String message) {
        System.err.println(message);
        System.exit(1);
    }

    public static void doSplashScreen() {
        SplashScreen mysplash = new SplashScreen("/logo_ksar.jpg", null, 3000);
        while (mysplash.isVisible()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }

    }

    public static void make_window(String command) {
        kSar new_window = mydesktop.make_new_window(command);
        if (usetrigger) {
            new_window.showtrigger = true;
        }
        if ( ! useemptydisk ) {
            new_window.showemptydisk = false;
        }
        if (usecpufixedaxis ) {
            new_window.show100axiscpu = true;
        }
        if (usecpustacked ) {
            new_window.showstackedcpu = true;
        }
        if (usememorystacked ) {
            new_window.showstackedmem = true;
        }
        if ( useintrliststacked ) {
            new_window.showstackedintrlist = true;
        }
        if ( pagesize.intValue() != -1) {
            new_window.solarispagesize = pagesize.intValue();
        }
    }

    private static void set_lookandfeel() {
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
            if (kSarConfig.landf.equals(laf.getName())) {
                try {
                    UIManager.setLookAndFeel(laf.getClassName());
                } catch (Exception e) {
                }

            }
        }
    }

    private static void show_lookandfeel() {
        System.out.println("Default L&F:");
        System.out.println("  " + UIManager.getLookAndFeel().getName());

        UIManager.LookAndFeelInfo[] inst = UIManager.getInstalledLookAndFeels();
        System.out.println("Installed L&Fs: ");
        for (int i = 0; i < inst.length; i++) {
            System.out.println("  " + inst[i].getName() + " = " + inst[i].getClass());
        }

        LookAndFeel[] aux = UIManager.getAuxiliaryLookAndFeels();
        System.out.println("Auxiliary L&Fs: ");
        if (aux != null) {
            for (int i = 0; i < aux.length; i++) {
                System.out.println("  " + aux[i].getName() + " = " + aux[i].getClass());
            }
        } else {
            System.out.println("  <NONE>");
        }

        System.out.println("Cross-Platform:");
        System.out.println("  " + UIManager.getCrossPlatformLookAndFeelClassName());

        System.out.println("System:");
        System.out.println("  " + UIManager.getSystemLookAndFeelClassName());
    }

    public static void start_gui() {

        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        set_lookandfeel();
        doSplashScreen();
        //lookandfeel();
        //try {
        //    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        //} catch (Exception e) {
        //}
        mydesktop = new kSarDesktop();
        mydesktop.setVisible(true);

        if (inputfile.size() > 0 && ! use_wizard) {
            ListIterator listItr = inputfile.listIterator();
            while (listItr.hasNext()) {
                make_window(listItr.next().toString());
            }
            // tile if needed
            mydesktop.do_tile();
        } else {
            Wizard wizardwind = null;
            if (use_wizard) {
                wizardwind = new Wizard(mydesktop, true);
                wizardwind.setVisible(true);
                wizardwind.setLocationRelativeTo(mydesktop);
            }

            if (wizardwind != null) {
                if (wizardwind.openinigraph.isSelected()) {
                    // open the graph selected
                    for (Iterator<String> it = kSarConfig.shortcut_window_list.keySet().iterator(); it.hasNext();) {
                        String key = it.next();
                        String value = kSarConfig.shortcut_window_list.get(key);
                        if (kSarConfig.startup_windows_list != null) {
                            if (kSarConfig.startup_windows_list.contains(value)) {
                                make_window((String) key);
                            }
                        }
                    }
                } else {
                    make_window("Empty");
                }
            } else {
                if ( open_at_startup ) {
                    for (Iterator<String> it = kSarConfig.shortcut_window_list.keySet().iterator(); it.hasNext();) {
                        String key = it.next();
                        String value = kSarConfig.shortcut_window_list.get(key);
                        if (kSarConfig.startup_windows_list != null) {
                            if (kSarConfig.startup_windows_list.contains(value)) {
                                make_window((String) key);
                            }
                        }
                    }
                } else {
                    make_window("Empty");
                }
            }
        }

        if (kSarConfig.tile_at_startup) {
            mydesktop.do_tile();
        }

    }
    private static kSarDesktop mydesktop = null;
    private static boolean nogui = false;
    //
    private static ArrayList<String> inputfile = new ArrayList<String>();
    private static String graphs = null;
    private static boolean usepref = false;
    private static boolean usetrigger = false;
    private static boolean useemptydisk = true;
    private static boolean usecpustacked = false;
    private static boolean linuxhack = false;
    private static boolean onlygraphname = false;
    private static boolean usecpufixedaxis = false;
    private static boolean addhtml = false;
    private static boolean usememorystacked = false;
    private static boolean useintrliststacked = false;
    private static boolean use_wizard = false;
    private static String outputpdffile = null;
    private static String outputpngbase = null;
    private static String outputjpgbase = null;
    private static String outputcsvfile = null;
    private static Integer width = new Integer(800);
    private static Integer height = new Integer(600);
    private static String startdate = null;
    private static String enddate = null;
    private static Integer pagesize = new Integer(-1);
    private static boolean dotile = false;
    private static boolean replaceshortcut = false;
    private static boolean open_at_startup = false;
    private static File tmpfile = null;
    private static String xmlfile = null;
    // err list
}
