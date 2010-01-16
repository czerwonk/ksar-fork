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
public class rpcdtcpSar extends AllGraph {

    // RPCD(tcp) badcalls   badlen    calls   dupchk  dupreqs  nullrcv  xdrcall
    
    public rpcdtcpSar(final kSar hissar) {
        super(hissar);
        Title = "RPCD(tcp)";    
        t_badcalls = new TimeSeries("badcalls/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) badcalls/s", t_badcalls);        
        t_badlen = new TimeSeries("badlen/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) badlen/s", t_badlen);
        t_calls = new TimeSeries("calls/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) calls/s", t_calls);
        t_dupchk = new TimeSeries("dupchk/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) dupchk/s", t_dupchk);
        t_dupreqs = new TimeSeries("dupreqs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) dupreqs/s", t_dupreqs);
        t_nullrcv = new TimeSeries("nullrcv/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) nullrcv/s", t_nullrcv);
        t_xdrcall = new TimeSeries("xdrcall/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("RPCD(tcp) xdrcall/s", t_xdrcall);
        // Collection
        bad_collection = new TimeSeriesCollection();
        bad_collection.addSeries(this.t_badcalls);
        bad_collection.addSeries(this.t_badlen);
        
        call_collection = new TimeSeriesCollection();
        call_collection.addSeries(this.t_calls);
        call_collection.addSeries(this.t_xdrcall);
        
        dup_collection = new TimeSeriesCollection();
        dup_collection.addSeries(this.t_dupchk);
        dup_collection.addSeries(this.t_dupreqs);
        
        
        null_collection = new TimeSeriesCollection();
        null_collection.addSeries(this.t_nullrcv);        
    }

    public void add(final Second now,final Float val1,final Float val2,final Float val3,final Float val4,final Float val5,
            final Float val6,final Float val7 ) {
        this.t_badcalls.add(now, val1, do_notify());
        this.t_badlen.add(now, val2, do_notify());
        this.t_calls.add(now, val3, do_notify());
        this.t_dupchk.add(now, val4, do_notify());
        this.t_dupreqs.add(now, val5, do_notify());
        this.t_nullrcv.add(now, val6, do_notify());
        this.t_xdrcall.add(now, val7, do_notify());        
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARRPCDTCP", this.Title, null));
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
        minichart2.setSeriesPaint(1, kSarConfig.color5);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(call_collection, null, new NumberAxis("calls"), minichart2);
        // scan
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(dup_collection, null, new NumberAxis("dups"), minichart3);
        // ufs
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color7);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(null_collection, null, new NumberAxis("err"), minichart4);
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
    private final TimeSeries t_badlen;
    private final TimeSeries t_calls;
    private final TimeSeries t_dupchk;
    private final TimeSeries t_dupreqs;
    private final TimeSeries t_nullrcv;
    private final TimeSeries t_xdrcall;
    private final TimeSeriesCollection bad_collection;
    private final TimeSeriesCollection call_collection;
    private final TimeSeriesCollection dup_collection;
    private final TimeSeriesCollection null_collection;
    
}
