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
public class msgSar extends AllGraph {

    public msgSar(final kSar hissar) {
        super(hissar);
        Title = "Messages & Semaphores";
        msg = new TimeSeries("msg", org.jfree.data.time.Second.class);
        mysar.dispo.put("Messages", msg);
        sema = new TimeSeries("sema", org.jfree.data.time.Second.class);
        mysar.dispo.put("Semaphores", sema);
        //Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.msg);
        ts_collection.addSeries(this.sema);
    }

    public void add(final Second now,final  Float val1Int,final  Float val2Int) {
        this.msg.add(now, val1Int, do_notify());
        this.sema.add(now, val2Int, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISMSG", this.Title, null));
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
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;
    }
    
    private final TimeSeries msg;
    private final TimeSeries sema;
    private final TimeSeriesCollection ts_collection;
}
