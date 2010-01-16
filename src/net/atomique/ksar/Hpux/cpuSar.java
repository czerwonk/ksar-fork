/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Hpux;

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

    public cpuSar(kSar hissar) {
        super(hissar);
        Title = new String("CPU");
        t_usr = new TimeSeries("User", org.jfree.data.time.Second.class);
        t_sys = new TimeSeries("System", org.jfree.data.time.Second.class);
        t_wio = new TimeSeries("Waiting I/O", org.jfree.data.time.Second.class);
        t_idle = new TimeSeries("Idle", org.jfree.data.time.Second.class);
        stacked_used = new TimeTableXYDataset();
        // create tiggers
        cpuidletrigger = new Trigger(mysar, this, "idle", t_idle, "down");
        cpusystemtrigger = new Trigger(mysar, this, "system", t_sys, "up");
        cpuwiotrigger = new Trigger(mysar, this, "wio", t_wio, "up");
        cpuusrtrigger = new Trigger(mysar, this, "usr", t_usr, "up");
        //
        cpuidletrigger.setTriggerValue(kSarConfig.hpuxcpuidletrigger);
        cpusystemtrigger.setTriggerValue(kSarConfig.hpuxcpusystemtrigger);
        cpuwiotrigger.setTriggerValue(kSarConfig.hpuxcpuwiotrigger);
        cpuusrtrigger.setTriggerValue(kSarConfig.hpuxcpuusrtrigger);

    }

    public void doclosetrigger() {
        cpuidletrigger.doclose();
        cpusystemtrigger.doclose();
        cpuwiotrigger.doclose();
        cpuusrtrigger.doclose();
    }

    public void add(Second now, Float usrInit, Float sysInit, Float wioInit, Float idleInit) {
        this.t_usr.add(now, usrInit);
        this.t_sys.add(now, sysInit);
        this.t_wio.add(now, wioInit);
        this.t_idle.add(now, idleInit);
        //trigger
        cpuidletrigger.doMarker(now, idleInit);
        cpusystemtrigger.doMarker(now, sysInit);
        cpuwiotrigger.doMarker(now, wioInit);
        cpuusrtrigger.doMarker(now, usrInit);
        // stacked
        stacked_used.add(now, usrInit, "User");
        stacked_used.add(now, sysInit, "System");
        stacked_used.add(now, wioInit, "Waiting I/O");
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

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXCPU", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createused();
        XYPlot subplot1;
        NumberAxis usedaxis = new NumberAxis("% used cpu");
        if (mysar.show100axiscpu) {
            usedaxis.setRange(0.0D, 100D);
        }
        if (mysar.showstackedcpu) {
            XYItemRenderer minichart1 = new StandardXYItemRenderer();
            minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            minichart1.setSeriesPaint(0, kSarConfig.color1);
            minichart1.setSeriesPaint(1, kSarConfig.color2);
            minichart1.setSeriesPaint(2, kSarConfig.color3);
            subplot1 = new XYPlot(xydataset1, null, usedaxis, minichart1);
        } else {
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            renderer.setSeriesPaint(0, kSarConfig.color1);
            renderer.setSeriesPaint(1, kSarConfig.color2);
            renderer.setSeriesPaint(2, kSarConfig.color3);
            subplot1 = new XYPlot(stacked_used, new DateAxis(null), usedaxis, renderer);            
        }
        // idle
        XYDataset idleset = this.createidle();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("% idle cpu"), minichart2);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 3);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = null;
        mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        // idle trigger
        cpuidletrigger.setTriggerValue(kSarConfig.hpuxcpuidletrigger);
        cpuidletrigger.tagMarker(subplot2);
        // system trigger
        cpusystemtrigger.setTriggerValue(kSarConfig.hpuxcpusystemtrigger);
        cpusystemtrigger.tagMarker(subplot1);
        // wio trigger
        cpuwiotrigger.setTriggerValue(kSarConfig.hpuxcpuwiotrigger);
        cpuwiotrigger.tagMarker(subplot1);
        // usr trigger
        cpuusrtrigger.setTriggerValue(kSarConfig.hpuxcpuusrtrigger);
        cpuusrtrigger.tagMarker(subplot1);
        //dateaxis1.setDateFormatOverride(new SimpleDateFormat("kk:mm\ndd/MM/yyyy")); 
        return mychart;
    }
    private Trigger cpuidletrigger;
    private Trigger cpusystemtrigger;
    private Trigger cpuwiotrigger;
    private Trigger cpuusrtrigger;
    private TimeTableXYDataset stacked_used;
    private TimeSeries t_usr;
    private TimeSeries t_sys;
    private TimeSeries t_wio;
    private TimeSeries t_idle;
}
