/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import java.awt.Color;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.Trigger;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSarConfig;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class cpuSar extends AllGraph {

    public cpuSar(final kSar hissar,final String cpuID) {
        super(hissar);
        Title = new String("CPU " + cpuID);
        cpuName = new String(cpuID);
        t_usr = new TimeSeries("User", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " User", t_usr);
        t_sys = new TimeSeries("System", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " System", t_sys);
        t_wio = new TimeSeries("Waiting I/O", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " Wait I/O", t_wio);
        t_idle = new TimeSeries("Idle", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " Idle", t_idle);
        t_physc = new TimeSeries("physc", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " pysc", t_physc);
        t_entc = new TimeSeries("%entc", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID + " %entc", t_entc);
        //
        stacked_used = new TimeTableXYDataset();
        // create tiggers
        cpuidletrigger = new Trigger(mysar, this, "idle", t_idle, "down");
        cpusystemtrigger = new Trigger(mysar, this, "system", t_sys, "up");
        cpuwiotrigger = new Trigger(mysar, this, "wio", t_wio, "up");
        //
        cpuidletrigger.setTriggerValue(kSarConfig.aixcpuidletrigger);
        cpusystemtrigger.setTriggerValue(kSarConfig.aixcpusystemtrigger);
        cpuwiotrigger.setTriggerValue(kSarConfig.aixcpuwiotrigger);
    }

    public void doclosetrigger() {
        cpuidletrigger.doclose();
        cpusystemtrigger.doclose();
        cpuwiotrigger.doclose();
    }

    public void add(final Second now,final Float usrInit,final Float sysInit,final Float wioInit,final Float idleInit) {
        this.t_usr.add(now, usrInit);
        this.t_sys.add(now, sysInit);
        this.t_wio.add(now, wioInit);
        this.t_idle.add(now, idleInit);
        cpuidletrigger.doMarker(now, idleInit);
        cpusystemtrigger.doMarker(now, sysInit);
        cpuwiotrigger.doMarker(now, wioInit);
        //stacked
        stacked_used.add(now, usrInit, "User");
        stacked_used.add(now, sysInit, "System");
        stacked_used.add(now, wioInit, "Waiting I/O");
    }

    public void add(final Second now,final Float usrInit,final Float sysInit,final Float wioInit,final Float idleInit,final Float physcInit) {
        this.t_usr.add(now, usrInit);
        this.t_sys.add(now, sysInit);
        this.t_wio.add(now, wioInit);
        this.t_idle.add(now, idleInit);
        this.t_physc.add(now, physcInit);
        cpuidletrigger.doMarker(now, idleInit);
        cpusystemtrigger.doMarker(now, sysInit);
        cpuwiotrigger.doMarker(now, wioInit);
        //stacked
        stacked_used.add(now, usrInit, "User");
        stacked_used.add(now, sysInit, "System");
        stacked_used.add(now, wioInit, "Waiting I/O");
    }

    public void add(final Second now,final Float usrInit,final Float sysInit,final Float wioInit,final Float idleInit,final Float physcInit,final Float entcInit) {
        this.t_usr.add(now, usrInit);
        this.t_sys.add(now, sysInit);
        this.t_wio.add(now, wioInit);
        this.t_idle.add(now, idleInit);
        this.t_physc.add(now, physcInit);
        this.t_entc.add(now, entcInit);
        cpuidletrigger.doMarker(now, idleInit);
        cpusystemtrigger.doMarker(now, sysInit);
        cpuwiotrigger.doMarker(now, wioInit);
        // stacked
        stacked_used.add(now, usrInit, "User");
        stacked_used.add(now, sysInit, "System");
        stacked_used.add(now, wioInit, "Waiting I/O");
    }

    public void setcpuOpt(final String s) {
        this.cpuOpt = s;
    }

    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_usr);
        graphcollection.addSeries(this.t_sys);
        graphcollection.addSeries(this.t_wio);
        return graphcollection;
    }

    public XYDataset createidle() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_idle);
        return graphcollection;
    }

    public XYDataset createphysc() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_physc);
        return graphcollection;
    }

    public XYDataset createentc() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_entc);
        return graphcollection;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXCPU", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        XYPlot subplot3 = null;
        XYPlot subplot4 = null;
        XYPlot subplot1 = null;
        // used
        XYDataset xydataset1 = this.createused();
        NumberAxis usedaxis = new NumberAxis("% used cpu");
        if (mysar.show100axiscpu) {
            usedaxis.setRange(0.0D, 100D);
        }

        if (mysar.showstackedcpu) {
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            renderer.setSeriesPaint(0, kSarConfig.color1);
            renderer.setSeriesPaint(1, kSarConfig.color2);
            renderer.setSeriesPaint(2, kSarConfig.color3);
            subplot1 = new XYPlot(stacked_used, new DateAxis(null), usedaxis, renderer);
        } else {
            XYItemRenderer minichart1 = new StandardXYItemRenderer();
            minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            minichart1.setSeriesPaint(0, kSarConfig.color1);
            minichart1.setSeriesPaint(1, kSarConfig.color2);
            minichart1.setSeriesPaint(2, kSarConfig.color3);
            subplot1 = new XYPlot(xydataset1, null, usedaxis, minichart1);
        }
        // idle
        XYDataset idleset = this.createidle();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("% idle"), minichart2);
        // physc
        if ("physc".equals(cpuOpt)) {
            XYDataset physcset = this.createphysc();
            XYItemRenderer minichart3 = new StandardXYItemRenderer();
            minichart3.setSeriesPaint(0, kSarConfig.color5);
            minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot3 = new XYPlot(physcset, null, new NumberAxis("physc"), minichart3);
        }
        if ("%entc".equals(cpuOpt)) {
            XYDataset physcset = this.createphysc();
            XYItemRenderer minichart3 = new StandardXYItemRenderer();
            minichart3.setSeriesPaint(0, kSarConfig.color6);
            minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot3 = new XYPlot(physcset, null, new NumberAxis("physc"), minichart3);
            //
            XYDataset entcset = this.createentc();
            XYItemRenderer minichart4 = new StandardXYItemRenderer();
            minichart4.setSeriesPaint(0, kSarConfig.color7);
            minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot4 = new XYPlot(entcset, null, new NumberAxis("%entc"), minichart4);

        }
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 3);
        if ("physc".equals(cpuOpt)) {
            plot.add(subplot3, 1);
        }
        if ("%entc".equals(cpuOpt)) {
            plot.add(subplot4, 1);
            plot.add(subplot3, 1);
        }
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            if ("physc".equals(cpuOpt)) {
                subplot3.setBackgroundPaint(null);
            }
            if ("%entc".equals(cpuOpt)) {
                subplot3.setBackgroundPaint(null);
                subplot4.setBackgroundPaint(null);
            }
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        // idle trigger
        cpuidletrigger.setTriggerValue(kSarConfig.aixcpuidletrigger);
        cpuidletrigger.tagMarker(subplot2);
        // system trigger
        cpusystemtrigger.setTriggerValue(kSarConfig.aixcpusystemtrigger);
        cpusystemtrigger.tagMarker(subplot1);
        // wio trigger
        cpuwiotrigger.setTriggerValue(kSarConfig.aixcpuwiotrigger);
        cpuwiotrigger.tagMarker(subplot1);

        //dateaxis1.setDateFormatOverride(new SimpleDateFormat("kk:mm\ndd/MM/yyyy")); 
        return mychart;
    }
    final Trigger cpuidletrigger;
    final Trigger cpusystemtrigger;
    final Trigger cpuwiotrigger;
    final TimeTableXYDataset stacked_used;
    final TimeSeries t_usr;
    final TimeSeries t_sys;
    final TimeSeries t_wio;
    final TimeSeries t_idle;
    final TimeSeries t_physc;
    final TimeSeries t_entc;
    private String cpuOpt = new String("");
    final String cpuName;
}
