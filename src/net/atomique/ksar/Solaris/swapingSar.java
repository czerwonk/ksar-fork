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
public class swapingSar extends AllGraph {

    public swapingSar(final kSar hissar) {
        super(hissar);
        Title = "Swapping";
        swpin = new TimeSeries("LWP in", org.jfree.data.time.Second.class);
        mysar.dispo.put("LWP swap in", swpin);
        bswin = new TimeSeries("pages in", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap page in", bswin);
        swpot = new TimeSeries("LWP out", org.jfree.data.time.Second.class);
        mysar.dispo.put("LWP swap out", swpot);
        bswot = new TimeSeries("pages out", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap page out", bswot);
        pswch = new TimeSeries("LWP switch", org.jfree.data.time.Second.class);
        mysar.dispo.put("LWP switch", pswch);
        // Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.swpin);
        ts_collection.addSeries(this.bswin);
        ts_collection.addSeries(this.swpot);
        ts_collection.addSeries(this.bswot);
        ts_collection.addSeries(this.pswch);
        
    }

    public void add(final Second now,final Float swpinInt,final Float bswinInt,final Float swpotInt,final Float bswotInt,final Float pswchInt) {
        swpin.add(now, swpinInt, do_notify());
        bswin.add(now, bswinInt.floatValue() * 512, do_notify());
        swpot.add(now, swpotInt, do_notify());
        bswot.add(now, bswotInt.floatValue() * 512, do_notify());
        pswch.add(now, pswchInt, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISSWAP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final  Second g_end) {
        mygraph = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", ts_collection, true, true, false);
        setbackgroundimage(mygraph);
        XYPlot xyplot = (XYPlot) mygraph.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        renderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        renderer.setSeriesPaint(0, kSarConfig.color1);
        renderer.setSeriesPaint(1, kSarConfig.color2);
        renderer.setSeriesPaint(2, kSarConfig.color3);
        renderer.setSeriesPaint(3, kSarConfig.color4);
        renderer.setSeriesPaint(4, kSarConfig.color5);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;
    }
    
    private final TimeSeries swpin;
    private final TimeSeries bswin;
    private final TimeSeries swpot;
    private final TimeSeries bswot;
    private final TimeSeries pswch;
    private final  TimeSeriesCollection ts_collection;
}
