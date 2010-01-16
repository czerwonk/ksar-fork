/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;


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
public class paging1Sar extends AllGraph {

    public paging1Sar(final kSar hissar, String cpuID) {
        super(hissar);
        Title = "Paging1 for CPU " + cpuID;
        cpuName=cpuID;
        t_pgfree = new TimeSeries("pgfree/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Page free/s", t_pgfree);
        t_pgout = new TimeSeries("pgout/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Page out/s", t_pgout);
        t_ppgout = new TimeSeries("ppgout/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Priority page out/s", t_ppgout);
        t_pgscan = new TimeSeries("pgscan/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Page scan/s", t_pgscan);
        t_ufs = new TimeSeries("ufs_ipf/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " ufs_ipf/s", t_ufs);
        pagescantrigger = new Trigger(mysar, this, "scan", t_pgscan, "up");
        pagescantrigger.setTriggerValue( kSarConfig.solarispagescantrigger);
        // Collection
        free_collection = new TimeSeriesCollection();
        free_collection.addSeries(this.t_pgfree);
        out_collection = new TimeSeriesCollection();
        out_collection.addSeries(this.t_pgout);
        out_collection.addSeries(this.t_ppgout);
        scan_collection = new TimeSeriesCollection();
        scan_collection.addSeries(this.t_pgscan);
        ufs_collection = new TimeSeriesCollection();
        ufs_collection.addSeries(this.t_ufs);
    }

    public void doclosetrigger() {
        pagescantrigger.doclose();
    }

    public void add(final Second now,final  Float val1Init,final  Float val2Init,final  Float val3Init,final  Float val4Init,final  Float val5Init) {
        this.t_pgfree.add(now, val1Init, do_notify());
        this.t_pgout.add(now, val2Init, do_notify());
        this.t_ppgout.add(now, val3Init, do_notify());
        this.t_pgscan.add(now, val4Init, do_notify());
        this.t_ufs.add(now, val5Init, do_notify());
        if (mysar.showtrigger) {
            pagescantrigger.doMarker(now, val4Init);
        }
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARPAGING1", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final  Second g_end) {
        // free
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(free_collection, null, new NumberAxis("pgfree /s"), minichart1);
        // out
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart1.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(out_collection, null, new NumberAxis("pgout/s"), minichart2);
        // scan
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(scan_collection, null, new NumberAxis("pgscan/s"), minichart3);
        // ufs
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color5);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(ufs_collection, null, new NumberAxis("ufs_ipf/s"), minichart4);
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

        pagescantrigger.setTriggerValue( kSarConfig.solarispagescantrigger);
        pagescantrigger.tagMarker(subplot3);

        return mygraph;
    }
    private final Trigger pagescantrigger;
    private final TimeSeries t_pgfree;
    private final TimeSeries t_pgout;
    private final TimeSeries t_ppgout;
    private final TimeSeries t_pgscan;
    private final TimeSeries t_ufs;
    private final TimeSeriesCollection free_collection;
    private final TimeSeriesCollection out_collection;
    private final TimeSeriesCollection scan_collection;
    private final TimeSeriesCollection ufs_collection;
    private final String cpuName;
}
