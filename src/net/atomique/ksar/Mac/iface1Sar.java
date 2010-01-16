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
public class iface1Sar extends AllGraph {

    public iface1Sar(kSar hissar, String s1) {
        super(hissar);
        Title = new String("Interface traffic");
        this.ifName = s1;
        t_rxpck = new TimeSeries("Ipkts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Interface " + s1  + " ipkts/s" , t_rxpck);
        t_txpck = new TimeSeries("Opkts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Interface " + s1  + " opkts/s", t_txpck);
        t_rxbyt = new TimeSeries("Ibytes/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Interface " + s1  + " ibyte/s", t_rxbyt);
        t_txbyt = new TimeSeries("Obytes/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Interface " + s1  + " obytes/s", t_txbyt);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_rxpck.add(now, val1);
        this.t_txpck.add(now, val3);
        this.t_rxbyt.add(now, val2);
        this.t_txbyt.add(now, val4);
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

    public String getcheckBoxTitle() {
        return "Interface " + this.ifName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "MACIFACE1", this.Title, null));
       mysar.add2tree(myroot,mynode);
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
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("Ipkts/Opkts /s"), minichart1);
        // rws
        XYDataset bytset = this.createbyt();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(bytset, null, new NumberAxis("Ibytes/Obytes /s"), minichart2);
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
    private TimeSeries t_rxpck;
    private TimeSeries t_txpck;
    private TimeSeries t_rxbyt;
    private TimeSeries t_txbyt;
    private kSar hissar;
    private String ifName;
    private String ifOpt = new String("");
}
