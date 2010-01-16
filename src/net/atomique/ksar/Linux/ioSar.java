/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

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
public class ioSar extends AllGraph {

    public ioSar(kSar hissar) {
        super(hissar);
        Title = new String("I/O");
        t_tps = new TimeSeries("Transfer/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Transfert/s", t_tps);
        t_rtps = new TimeSeries("Read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Read/s", t_rtps);
        t_wtps = new TimeSeries("Write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Write/s", t_wtps);
        t_bread = new TimeSeries("Block read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Block read/s", t_bread);
        t_bwrit = new TimeSeries("Block write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Block write/s", t_bwrit);
        t_sect = new TimeSeries("Sect/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("IO Sect/s", t_sect);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_tps.add(now, val1, do_notify());
        this.t_rtps.add(now, val2, do_notify());
        this.t_wtps.add(now, val3, do_notify());
        this.t_bread.add(now, val4, do_notify());
        this.t_bwrit.add(now, val5, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2) {
        this.t_tps.add(now, val1, do_notify());
        this.t_sect.add(now, val2, do_notify());
        number_of_sample++;
    }

    public void setioOpt(String s) {
        this.ioOpt = s;
    }

    public XYDataset createtps() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_tps);
        return graphcollection;
    }

    public XYDataset createsect() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_sect);
        return graphcollection;
    }

    public XYDataset createrws() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_wtps);
        graphcollection.addSeries(this.t_rtps);
        return graphcollection;
    }

    public XYDataset createblock() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_bread);
        graphcollection.addSeries(this.t_bwrit);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXIO", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        // tps
        XYDataset xydataset1 = this.createtps();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("transfer/s"), minichart1);
        // rws
        XYDataset rws = this.createrws();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(rws, null, new NumberAxis("Read/Write /s"), minichart2);
        // nice 
        XYDataset blockset = this.createblock();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setSeriesPaint(1, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(blockset, null, new NumberAxis("Blocks Read/Write"), minichart3);
        // the graph
        plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot3, 1);
        plot.add(subplot2, 1);
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
        }
        return mychart;
    }
    private TimeSeries t_tps;
    private TimeSeries t_rtps;
    private TimeSeries t_wtps;
    private TimeSeries t_bread;
    private TimeSeries t_bwrit;
    private TimeSeries t_sect;
    private String ioOpt = new String("");
}
