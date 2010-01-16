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
public class bufferSar extends AllGraph {

    public bufferSar(kSar hissar) {
        super(hissar);
        Title = new String("Buffers");
        t_bread = new TimeSeries("bread/s", org.jfree.data.time.Second.class);
        t_lread = new TimeSeries("lread/s", org.jfree.data.time.Second.class);
        t_rcache = new TimeSeries("%rcache", org.jfree.data.time.Second.class);
        t_bwrit = new TimeSeries("bwrit/s", org.jfree.data.time.Second.class);
        t_lwrit = new TimeSeries("lwrit/s", org.jfree.data.time.Second.class);
        t_wcache = new TimeSeries("%wcache", org.jfree.data.time.Second.class);
        t_pread = new TimeSeries("pread/s", org.jfree.data.time.Second.class);
        t_pwrit = new TimeSeries("pwrit/s", org.jfree.data.time.Second.class);
        bufferrcachetrigger = new Trigger(mysar, this, "read cache", t_rcache, "down");
        bufferrcachetrigger.setTriggerValue(kSarConfig.hpuxbufferrcachetrigger);

    }

    public void doclosetrigger() {
        bufferrcachetrigger.doclose();
    }

    public void add(Second now, Float val1Init, Float val2Init, Float val3Init, Float val4Init, Float val5Init, Float val6Init, Float val7Init, Float val8Init) {
        this.t_bread.add(now, val1Init);
        this.t_lread.add(now, val2Init);
        this.t_rcache.add(now, val3Init);
        this.t_bwrit.add(now, val4Init);
        this.t_lwrit.add(now, val5Init);
        this.t_wcache.add(now, val6Init);
        this.t_pread.add(now, val7Init);
        this.t_pwrit.add(now, val8Init);
        bufferrcachetrigger.doMarker(now, val3Init);
    }

    public XYDataset createread() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_bread);
        timeseriescollection.addSeries(this.t_lread);
        timeseriescollection.addSeries(this.t_pread);
        return timeseriescollection;
    }

    public XYDataset createwrit() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_bwrit);
        timeseriescollection.addSeries(this.t_lwrit);
        timeseriescollection.addSeries(this.t_pwrit);
        return timeseriescollection;
    }

    public XYDataset creatercache() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_rcache);
        return timeseriescollection;
    }

    public XYDataset createwcache() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_wcache);
        return timeseriescollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXBUFFER", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // read
        XYDataset readset = this.createread();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(readset, null, new NumberAxis("Read"), minichart1);
        // writ
        XYDataset writset = this.createwrit();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart1.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(writset, null, new NumberAxis("Write"), minichart2);
        // wcache
        XYDataset wcacheset = this.createwcache();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(wcacheset, null, new NumberAxis("%wcache"), minichart3);
        // rcache
        XYDataset rcacheset = this.creatercache();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color5);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(rcacheset, null, new NumberAxis("%rcache"), minichart4);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        bufferrcachetrigger.setTriggerValue(kSarConfig.hpuxbufferrcachetrigger);
        bufferrcachetrigger.tagMarker(subplot4);

        return mychart;
    }
    private Trigger bufferrcachetrigger;
    private TimeSeries t_bread;
    private TimeSeries t_lread;
    private TimeSeries t_rcache;
    private TimeSeries t_bwrit;
    private TimeSeries t_lwrit;
    private TimeSeries t_wcache;
    private TimeSeries t_pread;
    private TimeSeries t_pwrit;
}
