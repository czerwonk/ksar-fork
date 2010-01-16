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
public class iface1Sar extends AllGraph {

    public iface1Sar(kSar hissar, String s1) {
        super(hissar);
        Title = new String("Interface traffic");
        this.ifName = s1;
        t_rxpck = new TimeSeries("rxpck/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxpck/s", t_rxpck); 
        t_txpck = new TimeSeries("txpck/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txpck/s", t_txpck);
        t_rxbyt = new TimeSeries("rxbyt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxbyt/s", t_rxbyt);
        t_txbyt = new TimeSeries("txbyt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txbyt/s", t_txbyt);
        t_rxcmp = new TimeSeries("rxcmp/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxcmp/s", t_rxcmp);
        t_txcmp = new TimeSeries("txcmp/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " txcmp/s", t_txcmp);
        t_rxmcst = new TimeSeries("rxmcst/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IF " + s1 + " rxmcst/s", t_rxmcst);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5, Float val6, Float val7) {
        this.t_rxpck.add(now, val1, do_notify());
        this.t_txpck.add(now, val2, do_notify());
        this.t_rxbyt.add(now, val3, do_notify());
        this.t_txbyt.add(now, val4, do_notify());
        this.t_rxcmp.add(now, val5, do_notify());
        this.t_txcmp.add(now, val6, do_notify());
        this.t_rxmcst.add(now, val7, do_notify());
        number_of_sample++;
    }

    public void setifOpt(String s) {
        this.ifOpt = s;
    }

    public XYDataset createpck() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxpck);
        graphcollection.addSeries(this.t_txpck);
        return graphcollection;
    }

    public XYDataset createbyt() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxbyt);
        graphcollection.addSeries(this.t_txbyt);
        return graphcollection;
    }

    public XYDataset createcmp() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxcmp);
        graphcollection.addSeries(this.t_txcmp);
        return graphcollection;
    }

    public XYDataset createmcst() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rxmcst);
        return graphcollection;
    }

    public String getcheckBoxTitle() {
        return "Interface " + this.ifName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXIFACE1", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " " + this.ifName + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // tps
        XYDataset xydataset1 = this.createpck();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("txpck/txpck /s"), minichart1);
        // rws
        XYDataset bytset = this.createbyt();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(bytset, null, new NumberAxis("txbyt/rxbyt /s"), minichart2);
        //
        XYDataset cmpset = this.createcmp();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(cmpset, null, new NumberAxis("rxcmp/txcmp /s"), minichart3);
        //
        XYDataset mcstset = this.createmcst();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color7);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(mcstset, null, new NumberAxis("rxmcst/s"), minichart4);
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
    private TimeSeries t_rxpck;
    private TimeSeries t_txpck;
    private TimeSeries t_rxbyt;
    private TimeSeries t_txbyt;
    private TimeSeries t_rxcmp;
    private TimeSeries t_txcmp;
    private TimeSeries t_rxmcst;
    private String ifName;
    private String ifOpt = new String("");
}
