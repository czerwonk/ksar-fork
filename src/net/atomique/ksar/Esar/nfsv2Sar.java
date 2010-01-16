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
public class nfsv2Sar extends AllGraph {

    public nfsv2Sar(final kSar hissar) {
        super(hissar);
        Title = "NFS v2";
        t_create = new TimeSeries("create/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 create/s", t_create);
        t_link = new TimeSeries("link/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 link/s", t_link);
        t_lookup = new TimeSeries("lookup/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 lookup/s", t_lookup);
        t_mkdir = new TimeSeries("mkdir/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 mkdir/s", t_mkdir);
        t_read = new TimeSeries("read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 read/s", t_read);
        t_readdir = new TimeSeries("readdir/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 readdir/s", t_readdir);
        t_readlnk = new TimeSeries("readlnk/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 readlnk/s", t_readlnk);
        t_remove = new TimeSeries("remove/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 remove/s", t_remove);
        t_rename = new TimeSeries("rename/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 rename/s", t_rename);
        t_rmdir = new TimeSeries("rmdir/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 rmdir/s", t_rmdir);
        t_symlnk = new TimeSeries("symlnk/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 symlnk/s", t_symlnk);
        t_write = new TimeSeries("write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 write/s", t_write);
        t_getattr = new TimeSeries("getattr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 getattr/s", t_getattr);
        t_null = new TimeSeries("null/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 null/s", t_null);
        t_root = new TimeSeries("root/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 root/s", t_root);
        t_setattr = new TimeSeries("setattr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 setattr/s", t_setattr);
        t_statfs = new TimeSeries("statfs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 statfs/s", t_statfs);
        t_wrcache = new TimeSeries("wrcache/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("NFS v2 wrcache/s", t_wrcache);

        // create link lookup mkdir read readdir readlnk remove rename rmdir symlnk write getattr null root setattr statfs wrcache
        //   3*    3*    2*     3*   1*    2*       2*     3*    3*     3*     3*    1*     2*      4*    4*     3*      4*    4*        
        rw_collection = new TimeSeriesCollection();        
        rw_collection.addSeries(this.t_read);
        rw_collection.addSeries(this.t_write);

        fsr_collection = new TimeSeriesCollection();
        fsr_collection.addSeries(this.t_lookup);
        fsr_collection.addSeries(this.t_readlnk);
        fsr_collection.addSeries(this.t_getattr);
        fsr_collection.addSeries(this.t_readdir);

        fsw_collection = new TimeSeriesCollection();
        fsw_collection.addSeries(this.t_create);
        fsw_collection.addSeries(this.t_link);
        fsw_collection.addSeries(this.t_mkdir);
        fsw_collection.addSeries(this.t_remove);
        fsw_collection.addSeries(this.t_rename);
        fsw_collection.addSeries(this.t_rmdir);
        fsw_collection.addSeries(this.t_symlnk);
        fsw_collection.addSeries(this.t_setattr);
        
        proto_collection = new TimeSeriesCollection();
        proto_collection.addSeries(this.t_null);
        proto_collection.addSeries(this.t_statfs);
        proto_collection.addSeries(this.t_root);
        proto_collection.addSeries(this.t_wrcache);
    }

    //  create     link   lookup    mkdir     read  readdir  readlnk   remove   rename    rmdir   symlnk    write
    public void add(final Second now, final Float val1, final Float val2, final Float val3, final Float val4, final Float val5,
            final Float val6, final Float val7, final Float val8, final Float val9, final Float val10) {
        this.t_create.add(now, val1, do_notify());
        this.t_link.add(now, val2, do_notify());
        this.t_mkdir.add(now, val3, do_notify());
        this.t_read.add(now, val4, do_notify());
        this.t_readdir.add(now, val5, do_notify());
        this.t_remove.add(now, val6, do_notify());
        this.t_rename.add(now, val7, do_notify());
        this.t_rmdir.add(now, val8, do_notify());
        this.t_symlnk.add(now, val9, do_notify());
        this.t_write.add(now, val10, do_notify());
        number_of_sample++;
    }
    // getattr     null     root  setattr   statfs  wrcache
    public void add(final Second now, final Float val1, final Float val2, final Float val3, final Float val4, final Float val5,
            final Float val6) {
        this.t_getattr.add(now, val1, do_notify());
        this.t_null.add(now, val2, do_notify());
        this.t_root.add(now, val3, do_notify());
        this.t_setattr.add(now, val4, do_notify());
        this.t_statfs.add(now, val5, do_notify());
        this.t_wrcache.add(now, val6, do_notify());        
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARNFSV2", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        // free
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(rw_collection, null, new NumberAxis("data"), minichart1);
        // out
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setSeriesPaint(1, kSarConfig.color5);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(fsr_collection, null, new NumberAxis("fs read"), minichart2);
        // scan
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(fsw_collection, null, new NumberAxis("fs write"), minichart3);
        // ufs
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color7);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(proto_collection, null, new NumberAxis("protocol"), minichart4);
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
    private final TimeSeries t_create;
    private final TimeSeries t_link;
    private final TimeSeries t_lookup;
    private final TimeSeries t_mkdir;
    private final TimeSeries t_read;
    private final TimeSeries t_readdir;
    private final TimeSeries t_readlnk;
    private final TimeSeries t_remove;
    private final TimeSeries t_rename;
    private final TimeSeries t_rmdir;
    private final TimeSeries t_symlnk;
    private final TimeSeries t_write;
    private final TimeSeries t_getattr;
    private final TimeSeries t_null;
    private final TimeSeries t_root;
    private final TimeSeries t_setattr;
    private final TimeSeries t_statfs;
    private final TimeSeries t_wrcache;
    private final TimeSeriesCollection rw_collection;
    private final TimeSeriesCollection fsr_collection;
    private final TimeSeriesCollection fsw_collection;
    private final TimeSeriesCollection proto_collection;
}
