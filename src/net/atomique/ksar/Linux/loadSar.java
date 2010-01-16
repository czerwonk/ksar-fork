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
public class loadSar extends AllGraph {

    public loadSar(kSar hissar) {
        super(hissar);
        Title = new String("Load");
        t_runq = new TimeSeries("runq-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Run queue size", t_runq);
        t_plist = new TimeSeries("plist-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Process size", t_plist);
        t_ldavg1 = new TimeSeries("load 1mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Load 1mn", t_ldavg1);
        t_ldavg5 = new TimeSeries("load 5mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Load 5mn", t_ldavg5);
        t_ldavg15 = new TimeSeries("load 15mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Load 15mn", t_ldavg15);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_runq.add(now, val1, do_notify());
        this.t_plist.add(now, val2, do_notify());
        this.t_ldavg1.add(now, val3, do_notify());
        this.t_ldavg5.add(now, val4, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_runq.add(now, val1, do_notify());
        this.t_plist.add(now, val2, do_notify());
        this.t_ldavg1.add(now, val3, do_notify());
        this.t_ldavg5.add(now, val4, do_notify());
        this.t_ldavg15.add(now, val5, do_notify());
        number_of_sample++;
    }

    public XYDataset createrunq() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_runq);
        return graphcollection;
    }

    public XYDataset createplist() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_plist);
        return graphcollection;
    }

    public XYDataset createload() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_ldavg1);
        graphcollection.addSeries(this.t_ldavg5);
        if (loadOpt.equals("ldavg-15")) {
            graphcollection.addSeries(this.t_ldavg15);
        }
        return graphcollection;
    }

    public void setloadOpt(String s) {
        this.loadOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode= new DefaultMutableTreeNode(new GraphDescription(this, "LINUXKBMISC", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createrunq();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("runq-sz"), minichart1);
        // idle
        XYDataset plistset = this.createplist();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(plistset, null, new NumberAxis("plist-sz"), minichart2);
        //
        XYDataset loadset = this.createload();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setSeriesPaint(1, kSarConfig.color4);
        minichart3.setSeriesPaint(2, kSarConfig.color5);
        minichart3.setSeriesPaint(3, kSarConfig.color6);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(loadset, null, new NumberAxis("Load Average"), minichart3);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
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
    private TimeSeries t_runq;
    private TimeSeries t_plist;
    private TimeSeries t_ldavg1;
    private TimeSeries t_ldavg5;
    private TimeSeries t_ldavg15;
    private String loadOpt = new String("");
}
