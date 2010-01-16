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
public class cswchSar extends AllGraph {

    public cswchSar(final kSar hissar,final String cpuID) {
        super(hissar);
        cpuName = new String(cpuID);
        Title = new String("Contexts " + cpuID);
        t_cswch = new TimeSeries("cswch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Contexts CPU " + cpuID, t_cswch);
    }

    public void add(final Second now,final Float val1Init) {
        this.t_cswch.add(now, val1Init);
    }

    public XYDataset createcswch() {
        TimeSeriesCollection collectionswpq = new TimeSeriesCollection();
        collectionswpq.addSeries(this.t_cswch);
        return collectionswpq;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXCSWCH", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // proc
        XYDataset cswch = this.createcswch();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(cswch, null, new NumberAxis("cswch/s"), minichart1);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private final TimeSeries t_cswch;
    private final String cpuName ;
}
