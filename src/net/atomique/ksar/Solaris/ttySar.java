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
public class ttySar extends AllGraph {

    public ttySar(final kSar hissar) {
        super(hissar);
        Title = "Tty";
        t_rawch = new TimeSeries("Rawch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Rawch/s", t_rawch);
        t_canch = new TimeSeries("Canch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Canch/s", t_canch);
        t_outch = new TimeSeries("Outch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Outch/s", t_outch);
        t_rcvin = new TimeSeries("Rcvin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Rcvin/s", t_rcvin);
        t_xmtin = new TimeSeries("Xmtin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Xmtin/s",t_xmtin);
        t_mdmin = new TimeSeries("Mdmin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Mdmin/s",t_mdmin);
        // Collection
        tscollection = new TimeSeriesCollection();
        tscollection.addSeries(this.t_rawch);
        tscollection.addSeries(this.t_canch);
        tscollection.addSeries(this.t_outch);
        tscollection.addSeries(this.t_rcvin);
        tscollection.addSeries(this.t_xmtin);
        tscollection.addSeries(this.t_mdmin);
    }

    public void add(final Second now, final Float val1Init, final Float val2Init,final Float val3Init,final Float val4Init, final Float val5Init,final Float val6Init) {
        this.t_rawch.add(now, val1Init, do_notify());
        this.t_canch.add(now, val2Init, do_notify());
        this.t_outch.add(now, val3Init, do_notify());
        this.t_rcvin.add(now, val4Init, do_notify());
        this.t_xmtin.add(now, val5Init, do_notify());
        this.t_mdmin.add(now, val6Init, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISTTY", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        mygraph = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "per second", tscollection, true, true, false);
        setbackgroundimage(mygraph);
        XYPlot xyplot = (XYPlot) mygraph.getPlot();
        XYLineAndShapeRenderer myrenderer = (XYLineAndShapeRenderer) xyplot.getRenderer();
        myrenderer.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        myrenderer.setSeriesPaint(0, kSarConfig.color1);
        myrenderer.setSeriesPaint(1, kSarConfig.color2);
        myrenderer.setSeriesPaint(2, kSarConfig.color3);
        myrenderer.setSeriesPaint(3, kSarConfig.color4);
        myrenderer.setSeriesPaint(4, kSarConfig.color5);
        myrenderer.setSeriesPaint(5, kSarConfig.color6);
        myrenderer.setSeriesPaint(6, kSarConfig.color7);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;
    }
    private final TimeSeries t_rawch;
    private final TimeSeries t_canch;
    private final TimeSeries t_outch;
    private final TimeSeries t_rcvin;
    private final TimeSeries t_xmtin;
    private final TimeSeries t_mdmin;
    private final TimeSeriesCollection tscollection;
}
