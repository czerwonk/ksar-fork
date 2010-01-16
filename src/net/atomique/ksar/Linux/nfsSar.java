/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.awt.Color;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSarConfig;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class nfsSar extends AllGraph {

    public nfsSar(kSar hissar) {
        super(hissar);
        Title = new String("NFS client");
        t_call = new TimeSeries("call/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS call/s", t_call);
        t_retrans = new TimeSeries("retrans/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS retrans/s", t_retrans);
        t_read = new TimeSeries("read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS read/s", t_read);
        t_write = new TimeSeries("write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS write/s", t_write);
        t_access = new TimeSeries("access/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS access/s", t_access);
        t_getatt = new TimeSeries("getatt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS getatt/s", t_getatt);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6) {
        this.t_call.add(now, val1);
        this.t_retrans.add(now, val2);
        this.t_read.add(now, val3);
        this.t_write.add(now, val4);
        this.t_access.add(now, val5);
        this.t_getatt.add(now, val6);
        number_of_sample++;
    }

    public XYDataset createcall() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_call);
        graphcollection.addSeries(this.t_retrans);
        return graphcollection;
    }

    public XYDataset createrw() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_read);
        graphcollection.addSeries(this.t_write);
        return graphcollection;
    }

    public XYDataset createatt() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_access);
        graphcollection.addSeries(this.t_getatt);
        return graphcollection;
    }

    public void setnfsOpt(String s) {
        this.nfsOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXNFS", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createcall();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("call/retrans /s"), minichart1);
        // idle
        XYDataset rwset = this.createrw();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(rwset, null, new NumberAxis("write/read /s"), minichart2);
        //
        XYDataset attset = this.createatt();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(attset, null, new NumberAxis("access/getattr /s"), minichart3);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_call;
    private TimeSeries t_retrans;
    private TimeSeries t_read;
    private TimeSeries t_write;
    private TimeSeries t_access;
    private TimeSeries t_getatt;
    private String nfsOpt = new String("");
}
