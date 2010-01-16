/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

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

/**
 *
 * @author alex
 */
public class fileSar extends AllGraph {

    public fileSar(final kSar hissar) {
        super(hissar);
        Title = "File";
        iget = new TimeSeries("iget", org.jfree.data.time.Second.class);
        mysar.dispo.put("iget", iget);
        namei = new TimeSeries("namei", org.jfree.data.time.Second.class);
        mysar.dispo.put("namei", namei);
        dirbk = new TimeSeries("dirbk", org.jfree.data.time.Second.class);
        mysar.dispo.put("dirbk", dirbk);
        // Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.iget);
        ts_collection.addSeries(this.namei);
        ts_collection.addSeries(this.dirbk);
        
    }

    public void add(final Second now,final Float val1Int,final Float val2Int,final Float val3Int) {
        this.iget.add(now, val1Int, do_notify());
        this.namei.add(now, val2Int, do_notify());
        this.dirbk.add(now, val3Int, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISFILE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        mygraph = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", ts_collection, true, true, false);
        setbackgroundimage(mygraph);
        XYPlot xyplot = (XYPlot) mygraph.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        renderer.setSeriesPaint(0, kSarConfig.color1);
        renderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        renderer.setSeriesPaint(1, kSarConfig.color2);
        renderer.setSeriesPaint(2, kSarConfig.color3);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;

    }
    private final TimeSeries iget;
    private final TimeSeries namei;
    private final TimeSeries dirbk;
    private final TimeSeriesCollection ts_collection;
}
