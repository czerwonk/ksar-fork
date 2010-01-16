/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;


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

    public cpuSar(kSar hissar, String cpuID) {
        super(hissar);
        Title = new String("CPU " + cpuID);
        cpuName = cpuID;
        t_usr = new TimeSeries("User", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " User", t_usr);
        t_sys = new TimeSeries("System", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " System", t_sys);
        t_wio = new TimeSeries("Waiting I/O", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " Wait I/O", t_wio);
        t_idle = new TimeSeries("Idle", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " Idle", t_idle);
        t_nice = new TimeSeries("Nice", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " Nice", t_nice);
        t_steal = new TimeSeries("Steal", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " Steal", t_steal);
        //
        t_irq = new TimeSeries("%irq", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " %irq", t_irq);
        t_soft = new TimeSeries("%soft", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " %soft", t_soft);
        t_guest = new TimeSeries("%guest", org.jfree.data.time.Second.class);
        mysar.dispo.put(Title + " %guest", t_guest);
        //
        stacked_used = new TimeTableXYDataset();
        // trigger
        cpuidletrigger = new Trigger(mysar, this, "idle", t_idle, "down");
        cpusystemtrigger = new Trigger(mysar, this, "system", t_sys, "up");
        cpuwiotrigger = new Trigger(mysar, this, "wio", t_wio, "up");
        cpuusrtrigger = new Trigger(mysar, this, "usr", t_usr, "up");
        //
        cpuidletrigger.setTriggerValue(kSarConfig.linuxcpuidletrigger);
        cpusystemtrigger.setTriggerValue(kSarConfig.linuxcpusystemtrigger);
        cpuwiotrigger.setTriggerValue(kSarConfig.linuxcpuwiotrigger);
        cpuusrtrigger.setTriggerValue(kSarConfig.linuxcpuusrtrigger);

    }

    public void doclosetrigger() {
        cpuidletrigger.doclose();
        cpusystemtrigger.doclose();
        cpuwiotrigger.doclose();
        cpuusrtrigger.doclose();
    }

    public void setcpuOpt(String s) {
        this.cpuOpt = s;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_usr.add(now, val1, do_notify());
        this.t_sys.add(now, val3, do_notify());
        this.t_idle.add(now, val4, do_notify());
        this.t_nice.add(now, val2, do_notify());
        cpuidletrigger.doMarker(now, val4);
        cpusystemtrigger.doMarker(now, val3);
        cpuusrtrigger.doMarker(now, val1);
        stacked_used.add(now, val1, "User");
        stacked_used.add(now, val3, "System");
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_usr.add(now, val1, do_notify());
        this.t_sys.add(now, val3, do_notify());
        this.t_wio.add(now, val4, do_notify());
        this.t_idle.add(now, val5, do_notify());
        this.t_nice.add(now, val2, do_notify());
        cpuidletrigger.doMarker(now, val5);
        cpusystemtrigger.doMarker(now, val3);
        cpuwiotrigger.doMarker(now, val4);
        cpuusrtrigger.doMarker(now, val1);
        stacked_used.add(now, val1, "User");
        stacked_used.add(now, val3, "System");
        stacked_used.add(now, val4, "Waiting I/O");
        number_of_sample++;
    }

    
    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6) {
        this.t_usr.add(now, val1, do_notify());
        this.t_sys.add(now, val3, do_notify());
        this.t_wio.add(now, val4, do_notify());
        this.t_idle.add(now, val6, do_notify());
        this.t_nice.add(now, val2, do_notify());
        this.t_steal.add(now, val5, do_notify());
        cpuidletrigger.doMarker(now, val6);
        cpusystemtrigger.doMarker(now, val3);
        cpuwiotrigger.doMarker(now, val4);
        cpuusrtrigger.doMarker(now, val1);
        stacked_used.add(now, val1, "User");
        stacked_used.add(now, val3, "System");
        stacked_used.add(now, val4, "Waiting I/O");
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6,Float val7, Float val8, Float val9) {
        this.t_usr.add(now, val1, do_notify());
        this.t_sys.add(now, val3, do_notify());
        this.t_wio.add(now, val4, do_notify());
        this.t_idle.add(now, val9, do_notify());
        this.t_nice.add(now, val2, do_notify());
        this.t_steal.add(now, val5, do_notify());
        this.t_irq.add(now,val7, do_notify());
        this.t_soft.add(now,val8, do_notify());
        this.t_guest.add(now,val9, do_notify());
        cpuidletrigger.doMarker(now, val6);
        cpusystemtrigger.doMarker(now, val3);
        cpuwiotrigger.doMarker(now, val4);
        cpuusrtrigger.doMarker(now, val1);
        stacked_used.add(now, val1, "User");
        stacked_used.add(now, val3, "System");
        stacked_used.add(now, val4, "Waiting I/O");
        number_of_sample++;
    }
    
    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_usr);
        graphcollection.addSeries(this.t_sys);
        if (cpuOpt.equals("%iowait")) {
            graphcollection.addSeries(this.t_wio);
        }
        if (cpuOpt.equals("%steal")) {
            graphcollection.addSeries(this.t_wio);
            graphcollection.addSeries(this.t_steal);
        }
        return graphcollection;
    }

    public XYDataset createnice() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_nice);
        return graphcollection;
    }

    public XYDataset createidle() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_idle);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXCPU", this.Title, null));
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
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            renderer.setSeriesPaint(0, kSarConfig.color1);
            renderer.setSeriesPaint(1, kSarConfig.color2);
            renderer.setSeriesPaint(2, kSarConfig.color3);
            renderer.setSeriesPaint(3, kSarConfig.color4);
            subplot1 = new XYPlot(stacked_used, new DateAxis(null), usedaxis, renderer);

        } else {
            XYItemRenderer minichart1 = new StandardXYItemRenderer();
            minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            minichart1.setSeriesPaint(0, kSarConfig.color1);
            minichart1.setSeriesPaint(1, kSarConfig.color2);
            minichart1.setSeriesPaint(2, kSarConfig.color3);
            minichart1.setSeriesPaint(2, kSarConfig.color4);
            subplot1 = new XYPlot(xydataset1, null, usedaxis, minichart1);
        }
        // idle
        XYDataset idleset = this.createidle();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color5);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("% idle"), minichart2);
        // nice 
        XYDataset niceset = this.createnice();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(niceset, null, new NumberAxis("% niced"), minichart3);

        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 2);
        plot.add(subplot3, 1);
        plot.add(subplot2, 1);
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

        // idle trigger
        cpuidletrigger.setTriggerValue(kSarConfig.linuxcpuidletrigger);
        cpuidletrigger.tagMarker(subplot2);
        // system trigger
        cpusystemtrigger.setTriggerValue(kSarConfig.linuxcpusystemtrigger);
        cpusystemtrigger.tagMarker(subplot1);
        // wio trigger
        if (cpuOpt.equals("%iowait") || cpuOpt.equals("%steal")) {
            cpuwiotrigger.setTriggerValue(kSarConfig.linuxcpuwiotrigger);
            cpuwiotrigger.tagMarker(subplot1);
        }
        // usr trigger
        cpuusrtrigger.setTriggerValue(kSarConfig.linuxcpuusrtrigger);
        cpuusrtrigger.tagMarker(subplot1);
        //
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
    private TimeSeries t_steal;
    private TimeSeries t_nice;
    private TimeSeries t_guest;
    private TimeSeries t_irq;
    private TimeSeries t_soft;
    private String cpuOpt = new String("");
    private String cpuName = new String("");
}
