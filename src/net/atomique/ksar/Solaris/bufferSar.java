/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;


import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.Trigger;
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

/**
 *
 * @author alex
 */
public class bufferSar extends AllGraph {

    public bufferSar(final kSar hissar) {
        super(hissar);
        Title = "Buffers";
        t_bread = new TimeSeries("bread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers bread/s", t_bread);
        t_lread = new TimeSeries("lread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers lread/s", t_lread);
        t_rcache = new TimeSeries("%rcache", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers %rcache", t_rcache);
        t_bwrit = new TimeSeries("bwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers bwrit/s", t_bwrit);
        t_lwrit = new TimeSeries("lwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers lwrit/s", t_lwrit);
        t_wcache = new TimeSeries("%wcache", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers %wcache", t_wcache);
        t_pread = new TimeSeries("pread/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers pread/s", t_pread);
        t_pwrit = new TimeSeries("pwrit/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Buffers pwrit/s", t_pwrit);
        bufcachetrigger = new Trigger(mysar, this, "read cache", t_rcache, "down");
        bufcachetrigger.setTriggerValue(kSarConfig.solarisbufferrcachetrigger);
        // Collection
        read_collection = new TimeSeriesCollection();
        read_collection.addSeries(this.t_bread);
        read_collection.addSeries(this.t_lread);
        read_collection.addSeries(this.t_pread);
        write_collection = new TimeSeriesCollection();
        write_collection.addSeries(this.t_bwrit);
        write_collection.addSeries(this.t_lwrit);
        write_collection.addSeries(this.t_pwrit);
        rcache_collection = new TimeSeriesCollection();
        rcache_collection.addSeries(this.t_rcache);
        wcache_collection = new TimeSeriesCollection();
        wcache_collection.addSeries(this.t_wcache);
    }

    public void doclosetrigger() {
        bufcachetrigger.doclose();
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init,final Float val4Init,final Float val5Init,final Float val6Init,final Float val7Init,final Float val8Init) {
        this.t_bread.add(now, val1Init, do_notify());
        this.t_lread.add(now, val2Init, do_notify());
        this.t_rcache.add(now, val3Init, do_notify());
        this.t_bwrit.add(now, val4Init, do_notify());
        this.t_lwrit.add(now, val5Init, do_notify());
        this.t_wcache.add(now, val6Init, do_notify());
        this.t_pread.add(now, val7Init, do_notify());
        this.t_pwrit.add(now, val8Init, do_notify());
        if (mysar.showtrigger) {
            bufcachetrigger.doMarker(now, val3Init);
        }
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISBUFFER", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // read
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(read_collection, null, new NumberAxis("Read"), minichart1);
        // writ
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart1.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(write_collection, null, new NumberAxis("Write"), minichart2);
        // wcache
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(wcache_collection, null, new NumberAxis("%wcache"), minichart3);
        // rcache
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color5);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(rcache_collection, null, new NumberAxis("%rcache"), minichart4);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        bufcachetrigger.setTriggerValue(kSarConfig.solarisbufferrcachetrigger);
        bufcachetrigger.tagMarker(subplot4);

        return mygraph;
    }
    
    private final Trigger bufcachetrigger;
    private final TimeSeries t_bread;
    private final TimeSeries t_lread;
    private final TimeSeries t_rcache;
    private final TimeSeries t_bwrit;
    private final TimeSeries t_lwrit;
    private final TimeSeries t_wcache;
    private final TimeSeries t_pread;
    private final TimeSeries t_pwrit;
    private final TimeSeriesCollection read_collection;
    private final TimeSeriesCollection write_collection;
    private final TimeSeriesCollection rcache_collection;
    private final TimeSeriesCollection wcache_collection;
}
