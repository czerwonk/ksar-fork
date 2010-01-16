/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

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
public class slotSar extends AllGraph {

    public slotSar(final kSar hissar) {
        super(hissar);
        Title = new String("Slot");
        t_slot = new TimeSeries("slots", org.jfree.data.time.Second.class);
        mysar.dispo.put("slots", t_slot);
        t_cycle = new TimeSeries("cycle/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("cycle/s", t_cycle);
        t_fault = new TimeSeries("fault/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("fault/s", t_fault);
        t_odio = new TimeSeries("odio/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("odio/s", t_odio);
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init,final Float val4Init) {
        this.t_slot.add(now, val1Init);
        this.t_cycle.add(now, val2Init);
        this.t_fault.add(now, val3Init);
        this.t_odio.add(now, val4Init);
    }

    public XYDataset createrw() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_slot);
        return timeseriescollection;
    }

    public XYDataset createcall() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_cycle);
        return timeseriescollection;
    }

    public XYDataset createfork() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_fault);
        return timeseriescollection;
    }

    public XYDataset createchar() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_odio);
        return timeseriescollection;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXSLOT", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // rw
        XYDataset rwset = this.createrw();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(rwset, null, new NumberAxis("slots"), minichart1);
        // call
        XYDataset callset = this.createcall();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(callset, null, new NumberAxis("cycle/s"), minichart2);
        // fork
        XYDataset forkset = this.createfork();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setSeriesPaint(1, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(forkset, null, new NumberAxis("fault/s"), minichart3);
        // char
        XYDataset charset = this.createchar();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color6);
        minichart4.setSeriesPaint(1, kSarConfig.color7);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(charset, null, new NumberAxis("odio/s"), minichart4);

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

        return jfreechart;

    }
    final TimeSeries t_slot;
    final TimeSeries t_cycle;
    final TimeSeries t_fault;
    final TimeSeries t_odio;
}
