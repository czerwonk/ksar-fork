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
public class rqueueSar extends AllGraph {

    public rqueueSar(final kSar hissar) {
        super(hissar);
        Title = new String("Run Queue");
        t_runqsz = new TimeSeries("runq-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Run queue size",t_runqsz);
        t_runqocc = new TimeSeries("runqocc", org.jfree.data.time.Second.class);
        mysar.dispo.put("Run queue occupied", t_runqocc);
        //trigger
        rqueuetrigger = new Trigger(mysar, this, "Size", t_runqsz, "up");
        rqueuetrigger.setTriggerValue(kSarConfig.aixrqueuetrigger);
    }

    public void add(final Second now,final Float val1Init,final Float val2Init) {
        this.t_runqsz.add(now, val1Init);
        this.t_runqocc.add(now, val2Init);
        rqueuetrigger.doMarker(now, val1Init);
    }

    public void doclosetrigger() {
        rqueuetrigger.doclose();
    }

    public XYDataset createrunq1() {
        TimeSeriesCollection collectionrunq = new TimeSeriesCollection();
        collectionrunq.addSeries(this.t_runqsz);
        return collectionrunq;
    }

    public XYDataset createrunq2() {
        TimeSeriesCollection collectionrunq = new TimeSeriesCollection();
        collectionrunq.addSeries(this.t_runqocc);
        return collectionrunq;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXRQUEUE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // swap
        XYDataset runq1 = this.createrunq1();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(runq1, null, new NumberAxis(""), minichart1);
        // mem
        XYDataset runq2 = this.createrunq2();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(runq2, null, new NumberAxis("%"), minichart2);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
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
        }
        rqueuetrigger.setTriggerValue(kSarConfig.aixrqueuetrigger);
        rqueuetrigger.tagMarker(subplot1);
        return mychart;
    }
    final Trigger rqueuetrigger;
    final TimeSeries t_runqsz;
    final TimeSeries t_runqocc;
}
