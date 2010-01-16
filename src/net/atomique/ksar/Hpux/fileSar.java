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
public class fileSar extends AllGraph {

    public fileSar(kSar hissar) {
        super(hissar);
        Title = new String("File");
        iget = new TimeSeries("iget", org.jfree.data.time.Second.class);
        namei = new TimeSeries("namei", org.jfree.data.time.Second.class);
        dirbk = new TimeSeries("dirbk", org.jfree.data.time.Second.class);
    }

    public void add(Second now, Float val1Int, Float val2Int, Float val3Int) {
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

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXFILE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        XYDataset xydataset = this.create();
        JFreeChart mychart = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", xydataset, true, true, false);
        setbackgroundimage(mychart);
        XYPlot xyplot = (XYPlot) mychart.getPlot();
        XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        xylineandshaperenderer.setSeriesPaint(0, kSarConfig.color1);
        xylineandshaperenderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        xylineandshaperenderer.setSeriesPaint(1, kSarConfig.color2);
        xylineandshaperenderer.setSeriesPaint(2, kSarConfig.color3);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;

    }
    private TimeSeries iget;
    private TimeSeries namei;
    private TimeSeries dirbk;
}
