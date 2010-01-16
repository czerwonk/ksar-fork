/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;


import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
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
public class rpctcpSar extends AllGraph {

    // RPC(tcp)  badcalls badverfs  badxids    calls cantconn  intrpts newcreds    nomem timeouts   timers
    
    public rpctcpSar(final kSar hissar) {
        super(hissar);
        Title = "RPC(tcp)";    
        t_badcalls = new TimeSeries("badcalls/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) badcalls/s", t_badcalls);        
        t_badverfs = new TimeSeries("badverfs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) badverfs/s", t_badverfs);
        t_badxids = new TimeSeries("badxids/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) badxids/s", t_badxids);
        t_calls = new TimeSeries("calls/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) calls/s", t_calls);
        t_cantconn = new TimeSeries("cantconn/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) cantconn/s", t_cantconn);
        t_intrpts = new TimeSeries("intrpts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) intrpts/s", t_intrpts);
        t_newcreds = new TimeSeries("newcreds/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) newcreds/s", t_newcreds);
        t_nomem = new TimeSeries("nomem/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) nomem/s", t_nomem);
        t_timeouts = new TimeSeries("timeouts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) timeouts/s", t_timeouts);
        t_timers = new TimeSeries("timers/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPC(tcp) timers/s", t_timers);
        // Collection
        bad_collection = new TimeSeriesCollection();
        bad_collection.addSeries(this.t_badcalls);
        bad_collection.addSeries(this.t_badverfs);
        bad_collection.addSeries(this.t_badxids);
        
        call_collection = new TimeSeriesCollection();
        call_collection.addSeries(this.t_calls);
        
        time_collection = new TimeSeriesCollection();
        time_collection.addSeries(this.t_timeouts);
        time_collection.addSeries(this.t_timers);
        
        
        misc_collection = new TimeSeriesCollection();
        misc_collection.addSeries(this.t_newcreds);
        misc_collection.addSeries(this.t_intrpts);
        misc_collection.addSeries(this.t_cantconn);
        misc_collection.addSeries(this.t_nomem);
    }

    public void add(final Second now,final Float val1,final Float val2,final Float val3,final Float val4,final Float val5,
            final Float val6,final Float val7,final Float val8,final Float val9,final Float val10) {
        this.t_badcalls.add(now, val1, do_notify());
        this.t_badverfs.add(now, val2, do_notify());
        this.t_badxids.add(now, val3, do_notify());
        this.t_calls.add(now, val4, do_notify());
        this.t_cantconn.add(now, val5, do_notify());
        this.t_intrpts.add(now, val6, do_notify());
        this.t_newcreds.add(now, val7, do_notify());
        this.t_nomem.add(now, val8, do_notify());
        this.t_timeouts.add(now, val9, do_notify());
        this.t_timers.add(now, val10, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARRPCTCP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final  Second g_end) {
        // free
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(bad_collection, null, new NumberAxis("bad"), minichart1);
        // out
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(call_collection, null, new NumberAxis("calls"), minichart2);
        // scan
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setSeriesPaint(2, kSarConfig.color7);
        minichart3.setSeriesPaint(3, kSarConfig.color8);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(misc_collection, null, new NumberAxis("misc"), minichart3);
        // ufs
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color9);
        minichart4.setSeriesPaint(1, kSarConfig.color10);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(time_collection, null, new NumberAxis("time"), minichart4);
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

        return mygraph;
    }
  
    private final TimeSeries t_badcalls;
    private final TimeSeries t_badverfs;
    private final TimeSeries t_badxids;
    private final TimeSeries t_calls;
    private final TimeSeries t_cantconn;
    private final TimeSeries t_intrpts;
    private final TimeSeries t_newcreds;
    private final TimeSeries t_nomem;
    private final TimeSeries t_timeouts;
    private final TimeSeries t_timers;
    private final TimeSeriesCollection bad_collection;
    private final TimeSeriesCollection call_collection;
    private final TimeSeriesCollection time_collection;
    private final TimeSeriesCollection misc_collection;
    
}
