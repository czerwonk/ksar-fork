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
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class iface2Sar extends AllGraph {

    public iface2Sar(kSar hissar, String s1) {
        super(hissar);
        Title = new String("Interface misc");
        this.ifName = s1;
        t_bcstrcv = new TimeSeries("bcstrcv/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " bcstrcv/s", t_bcstrcv);
        t_bcstxmt = new TimeSeries("bcstxmt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " bcstxmt/s", t_bcstxmt);
        t_mcstrcv = new TimeSeries("mcstrcv/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " mcstrcv/s", t_mcstrcv);
        t_mcstxmt = new TimeSeries("mcstxmt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " mcstxmt/s", t_mcstxmt);
        t_norcvbf = new TimeSeries("norcvbf/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " norcvbf/s", t_norcvbf);
        t_noxmtbf = new TimeSeries("noxmtbf/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " noxmtbf/s", t_noxmtbf);
        t_coll = new TimeSeries("coll/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " coll/s", t_coll);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7) {
        this.t_bcstrcv.add(now, val1, do_notify());
        this.t_bcstxmt.add(now, val2, do_notify());
        this.t_mcstrcv.add(now, val3, do_notify());
        this.t_mcstxmt.add(now, val4, do_notify());
        this.t_norcvbf.add(now, val5, do_notify());
        this.t_noxmtbf.add(now, val6, do_notify());
        this.t_coll.add(now, val7, do_notify());
        number_of_sample++;
    }

    public void setifOpt(String s) {
        this.ifOpt = s;
    }

    public XYDataset createbcst() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_bcstrcv);
        graphcollection.addSeries(this.t_bcstxmt);
        return graphcollection;
    }

    public XYDataset createmcst() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_mcstrcv);
        graphcollection.addSeries(this.t_mcstrcv);
        return graphcollection;
    }

    public XYDataset createbuf() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_norcvbf);
        graphcollection.addSeries(this.t_noxmtbf);
        return graphcollection;
    }

    public XYDataset createcoll() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_coll);
        return graphcollection;
    }

    public String getcheckBoxTitle() {
        return "Interface " + this.ifName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXIFACE2", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " " + this.ifName + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // tps
        XYDataset xydataset1 = this.createbcst();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("broadcast /s"), minichart1);
        // rws
        XYDataset dropset = this.createmcst();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(dropset, null, new NumberAxis("multicast /s"), minichart2);
        //
        XYDataset collset = this.createbuf();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(collset, null, new NumberAxis("buffer /s"), minichart3);
        //
        XYDataset mcstset = this.createcoll();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color8);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(mcstset, null, new NumberAxis("collision /s"), minichart4);
        //
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_bcstrcv;
    private TimeSeries t_bcstxmt;
    private TimeSeries t_mcstrcv;
    private TimeSeries t_mcstxmt;
    private TimeSeries t_norcvbf;
    private TimeSeries t_noxmtbf;
    private TimeSeries t_coll;
    private String ifName;
    private String ifOpt = new String("");
}
