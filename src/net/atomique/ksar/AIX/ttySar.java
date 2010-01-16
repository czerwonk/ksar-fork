/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import java.awt.Color;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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

    public ttySar(final kSar hissar) {
        super(hissar);
        Title = new String("TTY");
        t_rawch = new TimeSeries("rawch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawch/s", t_rawch);
        t_canch = new TimeSeries("canch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("canch/s", t_canch);
        t_outch = new TimeSeries("outch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("outch/s", t_outch);
        t_rcvin = new TimeSeries("rcvin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rcvin/s", t_rcvin);
        t_xmtin = new TimeSeries("xmtin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("xmtin/s", t_xmtin);
        t_mdmin = new TimeSeries("mdmin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("mdmin/s", t_mdmin);
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init,final Float val4Init,final Float val5Init,final Float val6Init) {
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

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXTTY", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        XYDataset xydataset = this.create();
        JFreeChart mychart = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", xydataset, true, true, false);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        setbackgroundimage(mychart);
        XYPlot xyplot = (XYPlot) mychart.getPlot();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setSeriesPaint(0, kSarConfig.color1);
        xylineandshaperenderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        xylineandshaperenderer.setSeriesPaint(1, kSarConfig.color2);
        xylineandshaperenderer.setSeriesPaint(2, kSarConfig.color3);
        xylineandshaperenderer.setSeriesPaint(3, kSarConfig.color4);
        xylineandshaperenderer.setSeriesPaint(4, kSarConfig.color5);
        xylineandshaperenderer.setSeriesPaint(5, kSarConfig.color6);
        xylineandshaperenderer.setSeriesPaint(6, kSarConfig.color7);
        
        return mychart;

    }
    final TimeSeries t_rawch;
    final TimeSeries t_canch;
    final TimeSeries t_outch;
    final TimeSeries t_rcvin;
    final TimeSeries t_xmtin;
    final TimeSeries t_mdmin;
}
