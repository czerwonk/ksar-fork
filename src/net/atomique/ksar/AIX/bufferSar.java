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

    public bufferSar(final kSar hissar) {
        super(hissar);
        Title = new String("Buffers");
        t_bread = new TimeSeries("bread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers bread/s", t_bread);
        t_lread = new TimeSeries("lread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers lread/s", t_lread);
        t_rcache = new TimeSeries("%rcache", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers %rcache", t_rcache);
        t_bwrit = new TimeSeries("bwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers bwrit/s", t_bwrit);
        t_lwrit = new TimeSeries("lwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers lwrit/s", t_lwrit);
        t_wcache = new TimeSeries("%wcache", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers %wcache", t_wcache);
        t_pread = new TimeSeries("pread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers pread/s", t_pread);
        t_pwrit = new TimeSeries("pwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers pwrit/s", t_pwrit);
        // trigger
        bufferrcachetrigger = new Trigger(mysar, this, "read cache", t_rcache, "down");
        bufferrcachetrigger.setTriggerValue(kSarConfig.aixbufferrcachetrigger);
    }

    public void doclosetrigger() {
        bufferrcachetrigger.doclose();
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init,final Float val4Init,final Float val5Init,final Float val6Init,final Float val7Init,final Float val8Init) {
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

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXBUFFER", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // read
        XYDataset readset = this.createread();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(readset, null, new NumberAxis("Read"), minichart1);
        // writ
        XYDataset writset = this.createwrit();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color1);
        minichart2.setSeriesPaint(1, kSarConfig.color2);
        minichart2.setSeriesPaint(2, kSarConfig.color3);
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
        JFreeChart jfreechart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(jfreechart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }

        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) jfreechart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        //trigger
        bufferrcachetrigger.setTriggerValue(kSarConfig.aixbufferrcachetrigger);
        bufferrcachetrigger.tagMarker(subplot4);

        return jfreechart;
    }
    final Trigger bufferrcachetrigger;
    final TimeSeries t_bread;
    final TimeSeries t_lread;
    final TimeSeries t_rcache;
    final TimeSeries t_bwrit;
    final TimeSeries t_lwrit;
    final TimeSeries t_wcache;
    final TimeSeries t_pread;
    final TimeSeries t_pwrit;
}
