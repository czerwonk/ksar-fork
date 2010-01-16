/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;

/**
 *
 * @author alex
 */
public abstract class AllGraph {

    public AllGraph(final kSar hissar) {
        mysar = hissar;
    }

    public JPanel run(final Second g_start,final Second g_end) {
        return new ChartPanel(this.getgraph(g_start, g_end));
    }

    public int savePNG(final Second g_start,final Second g_end,final String filename,final int width,final int height) {
        try {
            ChartUtilities.saveChartAsPNG(new File(filename), this.makegraph(g_start, g_end), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public int saveJPG(final Second g_start,final Second g_end,final String filename,final int width,final int height) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(filename), this.makegraph(g_start, g_end), width, height);
        } catch (IOException e) {
            System.err.println("Unable to write to : " + filename);
            return -1;
        }
        return 0;
    }

    public String getGraphTitle() {
        return (Title + " for " + mysar.hostName);
        
    }

    public String getTitle() {
        return Title;
    }

    public int hasdata() {
        return datain;
    }

    public String getcheckBoxTitle() {
        return Title;
    }

    public String getToolTipText() {
        return Title;
    }
    
    public void setGraphLink(final String val) {
        GraphLink = val;
    }

    public void setnotifygraph(final boolean val) {
        notifygraph = val;
    }
    
    public String getGraphLink() {
        return GraphLink;
    }

    public void doclosetrigger(){
    }
    
    public void cleargraph() {
        mygraph=null;
    }
    
    public JFreeChart getgraph(final Second g_start,final Second g_end) {
        /*
        if ( mygraph == null ) {
            mygraph = makegraph(g_start,g_end);
        }
        return mygraph;
        */
         return makegraph(g_start,g_end);
    }
    
    protected boolean do_notify() {
        if ( ! notifygraph ) {
            return false;
        }
        if ( number_of_sample <= kSarConfig.alwaysrefresh ) {
            return true;
        }
        if ( number_of_sample <= kSarConfig.somerefresh ) {
            if ( number_of_sample % kSarConfig.somerefresh_time == 1 ) {
                return true;
            } else {
                return false;
            }
        }
        if ( number_of_sample <= kSarConfig.lessrefresh ) {
            if (number_of_sample % kSarConfig.lessrefresh_time == 1) {
                return true;
            } else {
                return false;
            }
        }
        if ( number_of_sample >= kSarConfig.norefresh ) {            
                return false;
        }
        return false;
    }
    
    public int setbackgroundimage(final JFreeChart mychart) {
        if (kSarConfig.background_image == null) {
            mychart.setBackgroundPaint(Color.white);            
        } else {
            ImageIcon icon = new javax.swing.ImageIcon(kSarConfig.background_image.toString());
            if (icon == null) {
                mychart.setBackgroundPaint(Color.white);
            } else {
                mychart.setBackgroundImage(icon.getImage());                
            }
            return 1;
        }
        return 0;
    }
    public int setbackgroundimage(final XYPlot myplot) {
        if (kSarConfig.background_image == null) {
            myplot.setBackgroundPaint(Color.white);            
        } else {
            ImageIcon icon = new javax.swing.ImageIcon(kSarConfig.background_image.toString());
            if (icon == null) {
                myplot.setBackgroundPaint(Color.white);
            } else {
                myplot.setBackgroundImage(icon.getImage());                
            }
            return 1;
        }
        return 0;
    }
    
    protected JFreeChart mygraph = null;
    abstract public JFreeChart makegraph(Second g_start, Second g_end);
    protected String Title = "Not Defined";
    protected String GraphLink = null;
    protected kSar mysar = null;
    protected int isEmpty = 1;
    protected int datain = 0;
    protected DefaultMutableTreeNode mynode = null;
    protected boolean notifygraph = false;
    protected long number_of_sample = 0;
}
