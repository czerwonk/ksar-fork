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
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author alex
 */
public class rqueueSar extends AllGraph {

    public rqueueSar(final kSar hissar) {
        super(hissar);
        Title = "Run Queue";
        t_runqsz = new TimeSeries("runq-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Run Queue Size", t_runqsz);
        t_runqocc = new TimeSeries("runqocc", org.jfree.data.time.Second.class);
        mysar.dispo.put("Run Queue Occupied", t_runqocc);
        rqueuetrigger = new Trigger(mysar, this, "Size", t_runqsz, "up");
        rqueuetrigger.setTriggerValue(kSarConfig.solarisrqueuetrigger);
        // Collection
        collectionrunq1 = new TimeSeriesCollection();
        collectionrunq1.addSeries(this.t_runqsz);
        collectionrunq2 = new TimeSeriesCollection();
        collectionrunq2.addSeries(this.t_runqocc);
    }

    public void doclosetrigger() {
        rqueuetrigger.doclose();
    }

    public void add(final Second now,final  Float val1Init,final Float val2Init) {
        this.t_runqsz.add(now, val1Init, do_notify());
        this.t_runqocc.add(now, val2Init, do_notify());
        if (mysar.showtrigger) {
            rqueuetrigger.doMarker(now, val1Init);
        }
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISRQUEUE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        // swap
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(collectionrunq1, null, new NumberAxis("Size"), minichart1);
        // mem
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(collectionrunq2, null, new NumberAxis("%occ"), minichart2);
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

        rqueuetrigger.setTriggerValue(kSarConfig.solarisrqueuetrigger);
        rqueuetrigger.tagMarker(subplot1);

        return mychart;
    }
    private final Trigger rqueuetrigger;
    private final TimeSeries t_runqsz;
    private final TimeSeries t_runqocc;
    private final TimeSeriesCollection collectionrunq1;
    private final TimeSeriesCollection collectionrunq2;

}
