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
public class nfsdSar extends AllGraph {

    public nfsdSar(kSar hissar) {
        super(hissar);
        Title = new String("NFS Server");
        t_scall = new TimeSeries("scall/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD scall/s", t_scall);
        t_badcall = new TimeSeries("badcall/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD badcall/s", t_badcall);
        t_packet = new TimeSeries("packet/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD packet/s", t_packet);
        t_udp = new TimeSeries("udp/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD udp/s", t_udp);
        t_tcp = new TimeSeries("tcp/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD tcp/s", t_tcp);
        t_hit = new TimeSeries("hit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD hit/s", t_hit);
        t_miss = new TimeSeries("miss/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD miss/s", t_miss);
        t_sread = new TimeSeries("sread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD sread/s", t_sread);
        t_swrite = new TimeSeries("swrite/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD swrite/s", t_swrite);
        t_saccess = new TimeSeries("saccess/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD saccess/s", t_saccess);
        t_sgetatt = new TimeSeries("sgetatt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFSD sgetatt/s", t_sgetatt);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7, Float val8, Float val9, Float val10, Float val11) {
        this.t_scall.add(now, val1, do_notify());
        this.t_badcall.add(now, val2, do_notify());
        this.t_packet.add(now, val3, do_notify());
        this.t_udp.add(now, val4, do_notify());
        this.t_tcp.add(now, val5, do_notify());
        this.t_hit.add(now, val6, do_notify());
        this.t_miss.add(now, val7, do_notify());
        this.t_sread.add(now, val8, do_notify());
        this.t_swrite.add(now, val9, do_notify());
        this.t_saccess.add(now, val10, do_notify());
        this.t_sgetatt.add(now, val11, do_notify());
        number_of_sample++;
    }
    
    public XYDataset createcall() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_scall);
        graphcollection.addSeries(this.t_packet);
        return graphcollection;
    }

    public XYDataset createnet() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_packet);
        graphcollection.addSeries(this.t_udp);
        graphcollection.addSeries(this.t_tcp);
        return graphcollection;
    }

    public XYDataset createcache() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_hit);
        graphcollection.addSeries(this.t_miss);
        return graphcollection;
    }

    public XYDataset createrw() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_sread);
        graphcollection.addSeries(this.t_swrite);
        return graphcollection;
    }

    public XYDataset createatt() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_saccess);
        graphcollection.addSeries(this.t_sgetatt);
        return graphcollection;
    }

    public void setnfsOpt(String s) {
        this.nfsOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXNFSD", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createcall();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("scall/badcall /s"), minichart1);
        // idle
        XYDataset netset = this.createnet();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setSeriesPaint(1, kSarConfig.color5);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(netset, null, new NumberAxis("packet/udp/tcp /s"), minichart2);
        //
        XYDataset cacheset = this.createcache();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color6);
        minichart3.setSeriesPaint(1, kSarConfig.color7);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(cacheset, null, new NumberAxis("hit/miss /s"), minichart3);
        //
        XYDataset rwset = this.createrw();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color8);
        minichart4.setSeriesPaint(1, kSarConfig.color9);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(rwset, null, new NumberAxis("sread/swrite /s"), minichart4);
        //
        XYDataset attset = this.createatt();
        XYItemRenderer minichart5 = new StandardXYItemRenderer();
        minichart5.setSeriesPaint(0, kSarConfig.color10);
        minichart5.setSeriesPaint(1, kSarConfig.color11);
        minichart5.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot5 = new XYPlot(attset, null, new NumberAxis("saccess/sgetatt /s"), minichart5);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.add(subplot5, 1);
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
            subplot4.setBackgroundPaint(null);
            subplot5.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_scall;
    private TimeSeries t_badcall;
    private TimeSeries t_packet;
    private TimeSeries t_udp;
    private TimeSeries t_tcp;
    private TimeSeries t_hit;
    private TimeSeries t_miss;
    private TimeSeries t_sread;
    private TimeSeries t_swrite;
    private TimeSeries t_saccess;
    private TimeSeries t_sgetatt;
    private String nfsOpt = new String("");
}
