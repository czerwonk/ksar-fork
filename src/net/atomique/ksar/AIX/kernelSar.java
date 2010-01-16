/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import java.awt.BasicStroke;
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
public class kernelSar extends AllGraph {

    public kernelSar(final kSar hissar) {
        super(hissar);
        Title = new String("Kernel Process");
        ksched = new TimeSeries("ksched/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("ksched/s", ksched);
        kproc = new TimeSeries("kproc-ov", org.jfree.data.time.Second.class);
        mysar.dispo.put("kproc-ov", kproc);
        kexit = new TimeSeries("kexit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("kexit/s", kexit);
    }

    public void add(final Second now,final Float val1Int,final Float val2Int,final Float val3Int) {
        ksched.add(now, val1Int);
        kproc.add(now, val2Int);
        kexit.add(now, val3Int);
    }

    public XYDataset create() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(ksched);
        timeseriescollection.addSeries(kexit);
        return timeseriescollection;
    }

    public XYDataset create2() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(kproc);
        return timeseriescollection;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXKPROC", Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        XYDataset xydataset1 = create();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(new BasicStroke(1.0F));
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("ksched/kexit /s"), minichart1);
        XYDataset idleset = create2();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(new BasicStroke(1.0F));
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("kproc-ov"), minichart2);
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 3);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart mychart = new JFreeChart(getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
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
    final TimeSeries ksched;
    final TimeSeries kproc;
    final TimeSeries kexit;
}
