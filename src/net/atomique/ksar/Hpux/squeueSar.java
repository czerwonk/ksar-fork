/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Hpux;

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
public class squeueSar extends AllGraph {

    public squeueSar(kSar hissar) {
        super(hissar);
        Title = new String("Swap queue");
        t_swpqsz = new TimeSeries("swpq-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap queue size", t_swpqsz);
        t_swpqocc = new TimeSeries("swpqocc", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap queue occupied", t_swpqocc);
    }

    public void add(Second now, Float val1Init, Float val2Init) {
        this.t_swpqsz.add(now, val1Init);
        this.t_swpqocc.add(now, val2Init);
    }

    public XYDataset createswpq1() {
        TimeSeriesCollection collectionswpq = new TimeSeriesCollection();
        collectionswpq.addSeries(this.t_swpqsz);
        return collectionswpq;
    }

    public XYDataset createswpq2() {
        TimeSeriesCollection collectionswpq = new TimeSeriesCollection();
        collectionswpq.addSeries(this.t_swpqocc);
        return collectionswpq;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXSQUEUE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // swap
        XYDataset swpq1 = this.createswpq1();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(swpq1, null, new NumberAxis(""), minichart1);
        // mem
        XYDataset swpq2 = this.createswpq2();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(swpq2, null, new NumberAxis("%"), minichart2);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private TimeSeries t_swpqsz;
    private TimeSeries t_swpqocc;
}
