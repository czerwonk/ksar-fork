/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

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

/**
 *
 * @author alex
 */
public class cpuSar extends AllGraph {
    public cpuSar(final kSar hissar)  {
        super(hissar);
        Title = "CPU";
        t_usr = new TimeSeries("User", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU User",t_usr);
        t_sys = new TimeSeries("System", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU System", t_sys);
        t_wio = new TimeSeries("Waiting I/O", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU Wait I/O", t_wio);
        t_idle = new TimeSeries("Idle", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU Idle", t_idle);
        stacked_used = new TimeTableXYDataset();
        // create tiggers
        cpuidletrigger = new Trigger(mysar, this, "idle", t_idle, "down");
        cpusystemtrigger = new Trigger(mysar, this, "system", t_sys, "up");
        cpuwiotrigger = new Trigger(mysar, this, "wio", t_wio, "up");
        cpuusrtrigger = new Trigger(mysar, this, "usr", t_usr, "up");
        //
        cpuidletrigger.setTriggerValue(kSarConfig.solariscpuidletrigger);
        cpusystemtrigger.setTriggerValue(kSarConfig.solariscpusystemtrigger);
        cpuwiotrigger.setTriggerValue(kSarConfig.solariscpuwiotrigger);
        cpuusrtrigger.setTriggerValue(kSarConfig.solariscpuusrtrigger);
        // Collection 
        used_collection = new TimeSeriesCollection();
        used_collection.addSeries(this.t_usr);
        used_collection.addSeries(this.t_sys);
        used_collection.addSeries(this.t_wio);
        idle_collection = new TimeSeriesCollection();
        idle_collection.addSeries(this.t_idle);
    }

    public void doclosetrigger() {
        cpuidletrigger.doclose();
        cpusystemtrigger.doclose();
        cpuwiotrigger.doclose();
        cpuusrtrigger.doclose();
    }

    public void add(final Second now,final Float usrInit,final Float sysInit,final Float wioInit,final Float idleInit) {
        this.t_usr.add(now, usrInit, do_notify());
        this.t_sys.add(now, sysInit, do_notify());
        this.t_wio.add(now, wioInit, do_notify());
        this.t_idle.add(now, idleInit, do_notify());
        //trigger
        if (mysar.showtrigger) {
            cpuidletrigger.doMarker(now, idleInit);
            cpusystemtrigger.doMarker(now, sysInit);
            cpuwiotrigger.doMarker(now, wioInit);
            cpuusrtrigger.doMarker(now, usrInit);
        }
        // stacked
        stacked_used.add(now, usrInit, "User", do_notify());
        stacked_used.add(now, sysInit, "System", do_notify());
        stacked_used.add(now, wioInit, "Waiting I/O", do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISCPU", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // used
        XYPlot subplot1;
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
            minichart1.setSeriesPaint(2, kSarConfig.color2);
            subplot1 = new XYPlot(used_collection, null, usedaxis, minichart1);
        }
        // idle
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idle_collection, null, new NumberAxis("% idle cpu"), minichart2);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 3);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        // idle trigger
        cpuidletrigger.setTriggerValue(kSarConfig.solariscpuidletrigger);
        cpuidletrigger.tagMarker(subplot2);
        // system trigger
        cpusystemtrigger.setTriggerValue(kSarConfig.solariscpusystemtrigger);
        cpusystemtrigger.tagMarker(subplot1);
        // wio trigger
        cpuwiotrigger.setTriggerValue(kSarConfig.solariscpuwiotrigger);
        cpuwiotrigger.tagMarker(subplot1);
        // usr trigger
        cpuusrtrigger.setTriggerValue(kSarConfig.solariscpuusrtrigger);
        cpuusrtrigger.tagMarker(subplot1);
        //dateaxis1.setDateFormatOverride(new SimpleDateFormat("kk:mm\ndd/MM/yyyy")); 
        return mygraph;
    }
    
    private final Trigger cpuidletrigger;
    private final Trigger cpusystemtrigger;
    private final Trigger cpuwiotrigger;
    private final Trigger cpuusrtrigger;
    private final TimeTableXYDataset stacked_used;
    private final TimeSeries t_usr;
    private final TimeSeries t_sys;
    private final TimeSeries t_wio;
    private final TimeSeries t_idle;
    private final TimeSeriesCollection used_collection;
    private final TimeSeriesCollection idle_collection;
    
}
