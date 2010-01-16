/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.MultiColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfGraphics2D;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author alex
 */
public class PdfExport extends PdfPageEventHelper implements Runnable {

    public PdfExport(String p, Map<String,AllGraph> m, JProgressBar g, JDialog d, kSar s) {
        mysar = s;
        mydial = d;
        progressbar = g;
        printMap = m;
        pdffilename = p;
    }

    public void run() {

        PdfWriter writer = null;

        if (pdffilename == null) {
            return;
        }
        if (printMap.size() < 1) {
            return;
        }
        org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(true);
         
        
        Document document = new Document(PageSize.A4.rotate());
        try {
            JFreeChart chart;
            PdfOutline kerneltree = null;
            PdfOutline disktree = null;
            PdfOutline iftree = null;
            PdfOutline cputree = null;
            PdfOutline filetree = null;
            PdfOutline scalltree = null;
            PdfOutline msgtree = null;
            PdfOutline cswchtree = null;
            Map<String,AllGraph> tmphash = new TreeMap<String,AllGraph>();
            tmphash.putAll(printMap);
            writer = PdfWriter.getInstance(document, new FileOutputStream(pdffilename));
            writer.setPageEvent(this);
            writer.setCompressionLevel(0);
            
            
            // document parameter before open
            document.addTitle("kSar Grapher");
            document.addSubject("Sar output of " + mysar.hostName);
            document.addKeywords("http://ksar.atomique.net/ ");
            document.addKeywords(mysar.hostName);
            //document.addKeywords(mysar.myOS.sarStartDate);
            //document.addKeywords(mysar.myOS.sarEndDate);
            document.addCreator("kSar Version:" + VersionNumber.getVersionNumber());
            document.addAuthor("Xavier cherif");
            
            // open the doc
            document.open();
            cb = writer.getDirectContent();
            
            PdfOutline root = cb.getRootOutline();
            totalpages = tmphash.size() + 1;
            // first page
            IndexPage(writer, document);
            TriggerPage(writer, document);
            //
            int progressint = 0;
            for (Iterator<String> it = tmphash.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                AllGraph value = tmphash.get(key);
                if (progressbar != null) {
                    progressbar.setValue(++progressint);
                    progressbar.repaint();
                }
                if (key.indexOf("wait") >= 0) {
                    value=null;
                    continue;
                }
                if (key.indexOf("-t2") >= 0) {
                    value=null;
                    continue;
                }
                if (key.indexOf("-if2") >= 0) {
                    value=null;
                    continue;                    
                }

                if (key.indexOf("-t1") >= 0) {
                    String diskn = key.substring(0, (key.length()) - 3);
                    AllGraph value2 = tmphash.get(diskn + "-t2");
                    if ( ! mysar.showemptydisk ) {
                        if ( value2 != null ) {
                            if ( value2.hasdata() == 0 && value.hasdata() == 0) {
                                value2=null;
                                value=null;
                                continue;
                            }
                        } else {
                            if (value.hasdata() == 0) {
                                value2=null;
                                value=null;
                                continue;
                            }
                        }                       
                    }
                    if (disktree == null) {
                        disktree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Disks");
                    }
                    PdfOutline thisdisk = null;
                    if (mysar.Adiskname.get(diskn) != null) {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), mysar.Adiskname.get(diskn));
                    } else {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), diskn);
                    }
                    addchart(writer, value);
                    PdfOutline thisdiskxf = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    if (value2 != null) {
                        document.newPage();
                        PdfOutline thisdiskwt = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value2.getTitle());
                        addchart(writer, value2);
                    }
                    value2=null;
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("-if1") >= 0) {
                    String ifn = key.substring(0, (key.length()) - 4);
                    if (iftree == null) {
                        iftree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Interfaces");
                    }
                    PdfOutline thisif = new PdfOutline(iftree, new PdfDestination(PdfDestination.FIT), ifn);
                    addchart(writer, value);
                    
                    PdfOutline thisif1 = new PdfOutline(thisif, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    AllGraph value2 = tmphash.get(ifn + "-if2");
                    if (value2 != null) {
                        document.newPage();
                        PdfOutline thisif2 = new PdfOutline(thisif, new PdfDestination(PdfDestination.FIT), value2.getTitle());
                        addchart(writer, value2);
                    }
                    value2=null;
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("-cpu") >= 0) {
                    String cpun = key.substring(0, (key.length()) - 4);
                    if (cputree == null) {
                        cputree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Cpus");
                    }
                    PdfOutline thiscpu = new PdfOutline(cputree, new PdfDestination(PdfDestination.FIT), cpun);
                    addchart(writer,value);
                    //PdfOutline thiscpu1 = new PdfOutline(thiscpu, new PdfDestination(PdfDestination.FIT),((AllGraph)value).getTitle());
                    document.newPage();
                    value=null;
                    continue;
                }
                if (key.indexOf("-file") >= 0) {
                    String filen = key.substring(0, (key.length()) - 5);
                    if (filetree == null) {
                        filetree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Files");
                    }
                    PdfOutline thisfile = new PdfOutline(filetree, new PdfDestination(PdfDestination.FIT), filen);
                    addchart(writer, value);
                    //PdfOutline thiscpu1 = new PdfOutline(thiscpu, new PdfDestination(PdfDestination.FIT),((AllGraph)value).getTitle());
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("-scall") >= 0) {
                    String scalln = key.substring(0, (key.length()) - 6);
                    if (scalltree == null) {
                        scalltree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Syscalls");
                    }
                    PdfOutline thisfile = new PdfOutline(scalltree, new PdfDestination(PdfDestination.FIT), scalln);
                    addchart(writer, value);
                    //PdfOutline thiscpu1 = new PdfOutline(thiscpu, new PdfDestination(PdfDestination.FIT),((AllGraph)value).getTitle());
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("-cswch") >= 0) {
                    String cswchn = key.substring(0, (key.length()) - 6);
                    if (cswchtree == null) {
                        cswchtree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Contexts");
                    }
                    PdfOutline thisfile = new PdfOutline(cswchtree, new PdfDestination(PdfDestination.FIT), cswchn);
                    addchart(writer, value);
                    //PdfOutline thiscpu1 = new PdfOutline(thiscpu, new PdfDestination(PdfDestination.FIT),((AllGraph)value).getTitle());
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("-msg") >= 0) {
                    String msgn = key.substring(0, (key.length()) - 4);
                    if (msgtree == null) {
                        msgtree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Messages & Semaphores");
                    }
                    PdfOutline thisfile = new PdfOutline(msgtree, new PdfDestination(PdfDestination.FIT), msgn);
                    addchart(writer, value);
                    //PdfOutline thiscpu1 = new PdfOutline(thiscpu, new PdfDestination(PdfDestination.FIT),((AllGraph)value).getTitle());
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("Solarisxfer") >= 0) {
                    String diskn = key.substring(0, (key.length()) - 11);
                    AllGraph value2 = tmphash.get(diskn + "Solariswait");
                    if ( ! mysar.showemptydisk ) {
                        if ( (value2.hasdata() == 0) && (value.hasdata() == 0))  {
                            value2=null;
                            value=null;
                            continue;
                        }
                    }
                    if (disktree == null) {
                        disktree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Disks");
                    }
                    PdfOutline thisdisk = null;
                    if (mysar.Adiskname.get(diskn) != null) {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), mysar.Adiskname.get(diskn));
                    } else {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), diskn);
                    }
                    addchart(writer, value);
                    PdfOutline thisdiskxf = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    if (value2 != null) {
                        document.newPage();
                        PdfOutline thisdiskwt = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value2.getTitle());
                        addchart(writer, value2);
                    }
                    value2=null;
                    value=null;
                    document.newPage();
                    continue;
                }
                if (key.indexOf("Hpuxxfer") >= 0) {
                    String diskn = key.substring(0, (key.length()) - 8);
                    AllGraph value2 = tmphash.get(diskn + "Hpuxwait");
                    if ( ! mysar.showemptydisk ) {
                        if ( (value2.hasdata() == 0)  && (value.hasdata() == 0))   {
                            value2=null;
                            value=null;
                            continue;
                        }
                    }
                    if (disktree == null) {
                        disktree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Disks");
                    }
                    PdfOutline thisdisk = null;
                    if (mysar.Adiskname.get(diskn) != null) {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), mysar.Adiskname.get(diskn));
                    } else {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), diskn);
                    }
                    addchart(writer,value);
                    PdfOutline thisdiskxf = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    if (value2 != null) {
                        document.newPage();
                        PdfOutline thisdiskwt = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT),value2.getTitle());
                        addchart(writer, value2);
                    }
                    document.newPage();
                    value2=null;
                    value=null;
                    continue;
                }
                if (key.indexOf("Aixxfer") >= 0) {
                    String diskn = key.substring(0, (key.length()) - 7);
                    AllGraph value2 = tmphash.get(diskn + "Aixwait");
                    if ( ! mysar.showemptydisk) {
                        if ( (value2.hasdata() == 0) && (value.hasdata() == 0)) {
                            value2=null;
                            value=null;
                            continue;
                        }
                    }
                    if (disktree == null) {
                        disktree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Disks");
                    }
                    PdfOutline thisdisk = null;
                    if (mysar.Adiskname.get(diskn) != null) {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), mysar.Adiskname.get(diskn));
                    } else {
                        thisdisk = new PdfOutline(disktree, new PdfDestination(PdfDestination.FIT), diskn);
                    }
                    addchart(writer, value);
                    PdfOutline thisdiskxf = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    if (value2 != null) {
                        document.newPage();
                        PdfOutline thisdiskwt = new PdfOutline(thisdisk, new PdfDestination(PdfDestination.FIT),value2.getTitle());
                        addchart(writer,  value2);
                    }
                    document.newPage();
                    value2=null;
                    value=null;
                    continue;
                }
                if (key.equals("SolariskmasmlSar")) {
                    addchart(writer, value);
                    if (kerneltree == null) {
                        kerneltree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Kernel");
                    }
                    PdfOutline out13 = new PdfOutline(kerneltree, new PdfDestination(PdfDestination.FIT),value.getTitle());
                    document.newPage();
                    value=null;
                    continue;
                }
                if (key.equals("SolariskmalgSar")) {
                    addchart(writer, value);
                    if (kerneltree == null) {
                        kerneltree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Kernel");
                    }
                    PdfOutline out14 = new PdfOutline(kerneltree, new PdfDestination(PdfDestination.FIT), value.getTitle());
                    document.newPage();
                    value=null;
                    continue;
                }
                if (key.equals("SolariskmaovzSar")) {
                    addchart(writer, value);
                    if (kerneltree == null) {
                        kerneltree = new PdfOutline(root, new PdfDestination(PdfDestination.FIT), "Kernel");
                    }
                    PdfOutline out15 = new PdfOutline(kerneltree, new PdfDestination(PdfDestination.FIT),value.getTitle());
                    document.newPage();
                    value=null;
                    continue;
                }

                addchart(writer, value);
                PdfOutline out1 = new PdfOutline(root, new PdfDestination(PdfDestination.FIT),value.getTitle());                
                document.newPage();
                value=null;
                continue;
                
            }
        } catch (DocumentException de) {
            System.err.println("Unable to write to : " + pdffilename);
            return;
        } catch (IOException ioe) {
            System.err.println("Unable to write to : " + pdffilename);
            return;
        }
     
        document.close();
        if (mydial != null) {
            mydial.dispose();
        }
        return;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        try {
            String text = "Page " + writer.getPageNumber() + "/" + totalpages;
            
            cb.beginText();
            cb.setFontAndSize(bf, 10);
            cb.setColorFill(new Color(0x00, 0x00, 0x00));
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, text, ((pdfheight - pdfmargins) - 10), 10 + pdfmargins, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_LEFT, kSarConfig.pdfbottomleft, 10 + pdfmargins, 10 + pdfmargins, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_RIGHT, kSarConfig.pdfupperright, ((pdfheight - pdfmargins) - 10), ((pdfwidth - pdfmargins) - 10), 0);
            cb.endText();

        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void IndexPage(PdfWriter writer, Document document) {
        try {
            String title = new String("SAR Statistics");
            String t_host = new String("For " + mysar.hostName);
            String t_date = new String("On " + mysar.myOS.getDate());
            cb.beginText();
            cb.setFontAndSize(bf, 48);
            cb.setColorFill(new Color(0x00, 0x00, 0x00));
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, ((pdfheight - pdfmargins) / 2), 500, 0);
            cb.setFontAndSize(bf, 36);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, t_host, ((pdfheight - pdfmargins) / 2), 400, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, t_date, ((pdfheight - pdfmargins) / 2), 300, 0);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, kSarConfig.pdfindexpage, ((pdfheight - pdfmargins) / 2), 150, 0);
            cb.endText();
            document.newPage();
        } catch (Exception de) {
            return;
        }
    }

    public void TriggerPage(PdfWriter writer, Document document) {
        if ( ! mysar.showtrigger ) {
            return;
        }
        if (mysar.DetectedBounds.size() < 1) {
            return;
        }
        try {
            MultiColumnText mct = new MultiColumnText();
            // set up 3 even columns with 10pt space between
            mct.addRegularColumns(document.left(), document.right(), 10f, 4);
            //set up the header
            String title = "Detected Bottlenecks: ";
            cb.beginText();
            cb.setFontAndSize(bf, 20);
            cb.setColorFill(new Color(0x00, 0x00, 0x00));
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, title, ((pdfheight - pdfmargins) / 2), ((pdfwidth - pdfmargins) - 10), 0);

            for (Iterator<String> it = mysar.DetectedBounds.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                //AllGraph value = mysar.DetectedBounds.get(key);
                mct.addElement(new Paragraph((String) key));
            }
            document.add(mct);
            document.newPage();
        } catch (DocumentException de) {
            return;
        }
    }

    
    public int addchart(PdfWriter writer, AllGraph graph) {
        JFreeChart chart = graph.getgraph(mysar.startofgraph, mysar.endofgraph);
        tp = cb.createTemplate(height, width);
        g2d = tp.createGraphics(height, width, mapper);
        r2d = new Rectangle2D.Double(0, 0, height, width);
        chart.draw(g2d, r2d, chartinfo);
        graph.cleargraph();
        g2d.dispose(); 
        cb.addTemplate(tp, pdfmargins, pdfmargins);
        try {
        writer.releaseTemplate(tp);
        } catch (IOException ioe) {
            System.err.println("Unable to write to : " + pdffilename);
        }
        return 0;
    }
    private int pdfheight = 842;
    private int pdfwidth = 595;
    private int pdfmargins = 10;
    int height = pdfheight - (2 * pdfmargins);
    int width = pdfwidth - (2 * pdfmargins);
    PdfTemplate tp;
    Graphics2D g2d;
    Rectangle2D r2d;
    kSar mysar;
    JProgressBar progressbar;
    String pdffilename;
    Map<String,AllGraph> printMap;
    JDialog mydial;
    private PdfContentByte cb;
    public Image headerImage;
    /** The headertable. */
    public PdfPTable table;
    /** The Graphic state */
    public PdfGState gstate;
    /** A template that will hold the total number of pages. */
    public PdfTemplate tpl;
    /** The font that will be used. */
    public BaseFont helv;
    /* nombre total de page */
    public int totalpages = 0;
    /* graphics */
    ChartRenderingInfo chartinfo=null;
    FontMapper mapper = new DefaultFontMapper();
    BaseFont bf = FontFactory.getFont(FontFactory.COURIER).getCalculatedBaseFont(false);
}
