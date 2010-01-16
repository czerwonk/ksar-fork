/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

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
        Title = new String("Interface errors");
        this.ifName = s1;
        t_rxerr = new TimeSeries("rxerr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxerr/s", t_rxerr);
        t_txerr = new TimeSeries("txerr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txerr/s", t_txerr);
        t_coll = new TimeSeries("coll/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " coll/s", t_coll);
        t_rxdrop = new TimeSeries("rxdrop/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxdrop/s", t_rxdrop);
        t_txdrop = new TimeSeries("txdrop/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txdrop/s", t_txdrop);
        t_txcarr = new TimeSeries("txcarr/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txcarr/s", t_txcarr);
        t_rxfram = new TimeSeries("rxfram/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxfram/s", t_rxfram);
        t_rxfifo = new TimeSeries("rxfifo/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxfifo/s", t_rxfifo);
        t_txfifo = new TimeSeries("txfifo/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txfifo/s", t_txfifo);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7, Float val8, Float val9) {
        this.t_rxerr.add(now, val1, do_notify());
        this.t_txerr.add(now, val2, do_notify());
        this.t_coll.add(now, val3, do_notify());
        this.t_rxdrop.add(now, val4, do_notify());
        this.t_txdrop.add(now, val5, do_notify());
        this.t_txcarr.add(now, val6, do_notify());
        this.t_rxfram.add(now, val7, do_notify());
        this.t_rxfifo.add(now, val8, do_notify());
        this.t_txfifo.add(now, val9, do_notify());
        number_of_sample++;
    }

    public void setifOpt(String s) {
        this.ifOpt = s;
    }

    public XYDataset createerr() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxerr);
        graphcollection.addSeries(this.t_txerr);
        return graphcollection;
    }

    public XYDataset createcoll() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_coll);
        graphcollection.addSeries(this.t_txcarr);
        graphcollection.addSeries(this.t_rxfram);
        return graphcollection;
    }

    public XYDataset createfifo() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxfifo);
        graphcollection.addSeries(this.t_txfifo);
        return graphcollection;
    }

    public XYDataset createdrop() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxdrop);
        graphcollection.addSeries(this.t_txdrop);
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
        XYDataset xydataset1 = this.createerr();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("rxer/txerr /s"), minichart1);
        // rws
        XYDataset dropset = this.createdrop();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(dropset, null, new NumberAxis("txdrop/rxdrop /s"), minichart2);
        //
        XYDataset collset = this.createcoll();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setSeriesPaint(2, kSarConfig.color7);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(collset, null, new NumberAxis("coll/txcarr/rxfram /s"), minichart3);
        //
        XYDataset mcstset = this.createfifo();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color8);
        minichart4.setSeriesPaint(1, kSarConfig.color9);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(mcstset, null, new NumberAxis("rxdrop/txdrop /s"), minichart4);
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
    private TimeSeries t_rxerr;
    private TimeSeries t_txerr;
    private TimeSeries t_coll;
    private TimeSeries t_rxdrop;
    private TimeSeries t_txdrop;
    private TimeSeries t_txcarr;
    private TimeSeries t_rxfram;
    private TimeSeries t_rxfifo;
    private TimeSeries t_txfifo;
    private String ifName;
    private String ifOpt = new String("");
}
