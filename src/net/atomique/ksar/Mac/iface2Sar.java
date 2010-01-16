/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Mac;

import java.awt.Color;
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
        t_rxerr = new TimeSeries("Ierrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Intreface " +s1 + " ierr/s", t_rxerr);
        t_txerr = new TimeSeries("Oerrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Intreface " +s1 + " oerr/s", t_txerr);
        t_coll = new TimeSeries("Coll/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Intreface " +s1 + " coll/s", t_coll);
        t_drop = new TimeSeries("Drop/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Intreface " +s1 + " drop/s", t_drop);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_rxerr.add(now, val1);
        this.t_txerr.add(now, val2);
        this.t_coll.add(now, val3);
        this.t_drop.add(now, val4);
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
        graphcollection.addSeries(this.t_drop);
        return graphcollection;
    }

    public String getcheckBoxTitle() {
        return "Interface " + this.ifName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "MACIFACE2", this.Title, null));
        mysar.add2tree(myroot,mynode);
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
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("Ierrs/Oerrs /s"), minichart1);
        // rws
        XYDataset dropset = this.createcoll();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(dropset, null, new NumberAxis("Coll/Drop /s"), minichart2);
        //
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    private TimeSeries t_rxerr;
    private TimeSeries t_txerr;
    private TimeSeries t_coll;
    private TimeSeries t_drop;
    private kSar hissar;
    private String ifName;
    private String ifOpt = new String("");
}
