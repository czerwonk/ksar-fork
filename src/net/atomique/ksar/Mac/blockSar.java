/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Mac;

import java.awt.Color;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
public class blockSar extends AllGraph {

    public blockSar(kSar hissar, String s1) {
        super(hissar);
        Title = new String("Block");
        this.blockName = s1;
        datain = 0;
        t_rw = new TimeSeries("r+w/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " r+w/s", t_rw);
        t_blks = new TimeSeries("Block/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "block/s", t_blks);
    }

    public void add(Second now, Float val1, Float val2) {
        Float zerof = new Float(0);
        if ((val1 != zerof || val2 != zerof) && datain == 0) {
            datain = 1;
        }
        this.t_rw.add(now, val1);
        this.t_blks.add(now, val2);
    }

    public void setioOpt(String s) {
        this.blockOpt = s;
    }

    public XYDataset createtps() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_rw);
        return graphcollection;
    }

    public XYDataset createsect() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_blks);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "MACBLOCK", this.blockName, null));
        mysar.add2tree(myroot,mynode);
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.blockName;
    }

    public String getGraphTitle() {
        return (this.Title + " " + this.blockName + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        XYPlot subplot2 = null;
        // tps
        XYDataset xydataset1 = this.createtps();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("r+w/s"), minichart1);
        XYDataset sectset = this.createsect();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        subplot2 = new XYPlot(sectset, null, new NumberAxis("Block/s"), minichart2);
        plot = new CombinedDomainXYPlot(new DateAxis(""));
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
    private TimeSeries t_rw;
    private TimeSeries t_blks;
    private kSar hissar;
    private String blockName;
    private String blockOpt = new String("");
}
