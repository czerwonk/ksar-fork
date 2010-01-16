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
public class fileSar extends AllGraph {

    public fileSar(final kSar hissar,final String cpuID) {
        super(hissar);
        cpuName = new String(cpuID);
        Title = new String("File " + cpuID);
        iget = new TimeSeries("iget", org.jfree.data.time.Second.class);
        mysar.dispo.put("iget", iget);
        namei = new TimeSeries("lookuppn", org.jfree.data.time.Second.class);
        mysar.dispo.put("lookuppn", namei);
        dirbk = new TimeSeries("dirblk", org.jfree.data.time.Second.class);
        mysar.dispo.put("dirblk", dirbk);
    }

    public void add(final Second now,final Float val1Int,final Float val2Int,final Float val3Int) {
        this.iget.add(now, val1Int);
        this.namei.add(now, val2Int);
        this.dirbk.add(now, val3Int);
    }

    public XYDataset create() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.iget);
        timeseriescollection.addSeries(this.namei);
        timeseriescollection.addSeries(this.dirbk);
        return timeseriescollection;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXFILE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        XYDataset xydataset = this.create();
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", xydataset, true, true, false);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) jfreechart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        setbackgroundimage(jfreechart);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setSeriesPaint(0, kSarConfig.color1);
        xylineandshaperenderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        xylineandshaperenderer.setSeriesPaint(1, kSarConfig.color2);
        xylineandshaperenderer.setSeriesPaint(2, kSarConfig.color3);
        return jfreechart;

    }
    final TimeSeries iget;
    final TimeSeries namei;
    final TimeSeries dirbk;
    final String cpuName;
}
