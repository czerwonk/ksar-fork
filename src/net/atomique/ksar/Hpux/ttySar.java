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
public class ttySar extends AllGraph {

    public ttySar(kSar hissar) {
        super(hissar);
        Title = new String("Tty");
        t_rawch = new TimeSeries("Rawch/s", org.jfree.data.time.Second.class);
        t_canch = new TimeSeries("Canch/s", org.jfree.data.time.Second.class);
        t_outch = new TimeSeries("Outch/s", org.jfree.data.time.Second.class);
        t_rcvin = new TimeSeries("Rcvin/s", org.jfree.data.time.Second.class);
        t_xmtin = new TimeSeries("Xmtin/s", org.jfree.data.time.Second.class);
        t_mdmin = new TimeSeries("Mdmin/s", org.jfree.data.time.Second.class);
    }

    public void add(Second now, Float val1Init, Float val2Init, Float val3Init, Float val4Init, Float val5Init, Float val6Init) {
        this.t_rawch.add(now, val1Init);
        this.t_canch.add(now, val2Init);
        this.t_outch.add(now, val3Init);
        this.t_rcvin.add(now, val4Init);
        this.t_xmtin.add(now, val5Init);
        this.t_mdmin.add(now, val6Init);
    }

    public XYDataset create() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_rawch);
        timeseriescollection.addSeries(this.t_canch);
        timeseriescollection.addSeries(this.t_outch);
        timeseriescollection.addSeries(this.t_rcvin);
        timeseriescollection.addSeries(this.t_xmtin);
        timeseriescollection.addSeries(this.t_mdmin);
        return timeseriescollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXTTY", this.Title, null));
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
        xylineandshaperenderer.setSeriesPaint(5, kSarConfig.color6);
        xylineandshaperenderer.setSeriesPaint(6, kSarConfig.color7);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private TimeSeries t_rawch;
    private TimeSeries t_canch;
    private TimeSeries t_outch;
    private TimeSeries t_rcvin;
    private TimeSeries t_xmtin;
    private TimeSeries t_mdmin;
}
