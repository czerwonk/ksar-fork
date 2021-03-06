/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

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
public class intrSar extends AllGraph {

    public intrSar(kSar hissar) {
        super(hissar);
        Title = new String("Interrupts");
        t_intr = new TimeSeries("intr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Interrupts/s", t_intr);
    }

    public void add(Second now, Float val1Init) {
        this.t_intr.add(now, val1Init, do_notify());
        number_of_sample++;
    }

    public XYDataset createintr() {
        TimeSeriesCollection collectionswpq = new TimeSeriesCollection();
        collectionswpq.addSeries(this.t_intr);
        return collectionswpq;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode= new DefaultMutableTreeNode(new GraphDescription(this, "LINUXINTR", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // proc
        XYDataset intr = this.createintr();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(intr, null, new NumberAxis("intr/s"), minichart1);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_intr;
}
