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
public class paging2Sar extends AllGraph {

    public paging2Sar(final kSar hissar) {
        super(hissar);
        Title = "Paging2";
        t_atch = new TimeSeries("acth/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page Attach/s", t_atch);
        t_pgin = new TimeSeries("pgin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page In/s", t_pgin);
        t_ppgin = new TimeSeries("ppgin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Priority page in/s", t_ppgin);
        t_pflt = new TimeSeries("pflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page fault/s", t_pflt);
        t_vflt = new TimeSeries("vflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Valid Page fault/s", t_vflt);
        t_slock = new TimeSeries("slock/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page software lock/s", t_slock);
        // Collection
        atch_collection = new TimeSeriesCollection();
        atch_collection.addSeries(this.t_atch);
        pgin_collection = new TimeSeriesCollection();
        pgin_collection.addSeries(this.t_pgin);
        pgin_collection.addSeries(this.t_ppgin);
        flt_collection = new TimeSeriesCollection();
        flt_collection.addSeries(this.t_pflt);
        flt_collection.addSeries(this.t_vflt);
        lock_collection = new TimeSeriesCollection();
        lock_collection.addSeries(this.t_slock);

    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init,final Float val4Init,final Float val5Init,final Float val6Init) {
        this.t_atch.add(now, val1Init, do_notify());
        this.t_pgin.add(now, val2Init, do_notify());
        this.t_ppgin.add(now, val3Init, do_notify());
        this.t_pflt.add(now, val4Init, do_notify());
        this.t_vflt.add(now, val5Init, do_notify());
        this.t_slock.add(now, val6Init, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISPAGING2", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // atch
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(atch_collection, null, new NumberAxis("atch/s"), minichart1);
        // in
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart1.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(pgin_collection, null, new NumberAxis("pgin/ppgin /s"), minichart2);
        // flt
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setSeriesPaint(1, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(flt_collection, null, new NumberAxis("pflt/vflt /s"), minichart3);
        // slock
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color6);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(lock_collection, null, new NumberAxis("slock/s"), minichart4);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private final TimeSeries t_atch;
    private final TimeSeries t_pgin;
    private final TimeSeries t_ppgin;
    private final TimeSeries t_pflt;
    private final TimeSeries t_vflt;
    private final TimeSeries t_slock;
    private final TimeSeriesCollection atch_collection;
    private final TimeSeriesCollection pgin_collection;
    private final TimeSeriesCollection flt_collection;
    private final TimeSeriesCollection lock_collection;
}
