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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class swapingSar extends AllGraph {

    public swapingSar(kSar hissar) {
        super(hissar);
        Title = new String("Swapping");
        swpin = new TimeSeries("LWP in", org.jfree.data.time.Second.class);
        bswin = new TimeSeries("pages in", org.jfree.data.time.Second.class);
        swpot = new TimeSeries("LWP out", org.jfree.data.time.Second.class);
        bswot = new TimeSeries("pages out", org.jfree.data.time.Second.class);
        pswch = new TimeSeries("LWP switch", org.jfree.data.time.Second.class);
    }

    public void add(org.jfree.data.time.Second now, Float swpinInt, Float bswinInt, Float swpotInt, Float bswotInt, Float pswchInt) {
        this.swpin.add(now, swpinInt);
        this.bswin.add(now, bswinInt.floatValue() * 512);
        this.swpot.add(now, swpotInt);
        this.bswot.add(now, bswotInt.floatValue() * 512);
        this.pswch.add(now, pswchInt);
    }

    public XYDataset create() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.swpin);
        timeseriescollection.addSeries(this.bswin);
        timeseriescollection.addSeries(this.swpot);
        timeseriescollection.addSeries(this.bswot);
        timeseriescollection.addSeries(this.pswch);
        return timeseriescollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXSWAP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        XYDataset xydataset = this.create();
        JFreeChart mychart = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", xydataset, true, true, false);
        setbackgroundimage(mychart);
        XYPlot xyplot = (XYPlot) mychart.getPlot();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        xylineandshaperenderer.setSeriesPaint(0, kSarConfig.color1);
        xylineandshaperenderer.setSeriesPaint(1, kSarConfig.color2);
        xylineandshaperenderer.setSeriesPaint(2, kSarConfig.color3);
        xylineandshaperenderer.setSeriesPaint(3, kSarConfig.color4);
        xylineandshaperenderer.setSeriesPaint(4, kSarConfig.color5);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private TimeSeries swpin;
    private TimeSeries bswin;
    private TimeSeries swpot;
    private TimeSeries bswot;
    private TimeSeries pswch;
}
