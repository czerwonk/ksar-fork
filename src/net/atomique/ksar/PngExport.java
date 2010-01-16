/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

/**
 *
 * @author alex
 */
public class PngExport implements Runnable {

    public PngExport(String p, Map<String,AllGraph> m, JProgressBar g, JDialog d, kSar s, boolean hisdohtml, int hisheight, int hiswidth) {
        mysar = s;
        mydial = d;
        progressbar = g;
        printMap = m;
        basefilename = p;
        width = hiswidth;
        height = hisheight;
        dohtml = hisdohtml;
    }

    public String crackfilename(String filename) {
        String tmp = new String(filename);
        tmp.replace('/', '_');
        tmp.replace('\\', '_');
        return tmp;
    }

    public void run() {

        if (basefilename == null) {
            return;
        }
        if (printMap.size() < 1) {
            return;
        }

        BufferedWriter out = null;
        if (dohtml) {
            try {
                out = new BufferedWriter(new FileWriter(basefilename + "_index.html"));
            } catch (IOException e) {
                out = null;
            }
        }
        if (out != null) {
            try {
                out.write("<HTML><HEAD><title>kSar : " + mysar.hostName +
                        "</title></HEAD><body bgcolor='#ffffff' link='#000000' vlink='#000000'><div align='center' valign='center'>");

            } catch (IOException e) {
            }
        }

        org.jfree.text.TextUtilities.setUseDrawRotatedStringWorkaround(false);


        Map<String,AllGraph> tmphash = new TreeMap<String,AllGraph>(printMap);
        int progressint = 0;
        for (Iterator<String> it = tmphash.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            AllGraph value = tmphash.get(key);

            if (progressbar != null) {
                progressbar.setValue(++progressint);
                progressbar.repaint();
            }

            String name = (new File(basefilename + "_" + crackfilename(key + ".png"))).getName();
            value.savePNG(mysar.startofgraph, mysar.endofgraph, basefilename + "_" + crackfilename(key + ".png"), width, height);
            if (out != null) {
                try {
                    out.write("<img src='" + name + "'>");
                } catch (IOException e) {
                }
            }

        }
        if (out != null) {
            try {
                out.write("</div></body></html>");
                out.close();
            } catch (IOException e) {
            }
        }

        if (mydial != null) {
            mydial.dispose();
        }
        return;
    }
    
    kSar mysar;
    JProgressBar progressbar;
    Map<String,AllGraph> printMap;
    JDialog mydial;
    String basefilename;
    int width;
    int height;
    boolean dohtml;

}
