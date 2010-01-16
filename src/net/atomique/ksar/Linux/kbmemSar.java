/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.awt.Color;
import java.text.NumberFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.IEEE1541Number;
import net.atomique.ksar.kSar;
import net.atomique.ksar.kSarConfig;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class kbmemSar extends AllGraph {

    public kbmemSar(kSar hissar) {
        super(hissar);
        Title = new String("Memory Usage");
        t_free = new TimeSeries("memfree", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory free", t_free);
        t_used = new TimeSeries("memused", org.jfree.data.time.Second.class);
        mysar.dispo.put("Memory used", t_used);
        t_perc = new TimeSeries("%memused", org.jfree.data.time.Second.class);
        mysar.dispo.put("%Memory used", t_perc);
        //
        stacked_used = new TimeTableXYDataset();
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float kbbuffers, Float kbcached, Float kbswpused) {
        this.t_free.add(now, val1.floatValue() * 1024, do_notify());
        this.t_used.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_perc.add(now, val3, do_notify());
        double kbusedbuffadj = val2.floatValue() - kbbuffers.floatValue() - kbcached.floatValue();
        stacked_used.add(now, kbusedbuffadj * 1024, "used (buffer adjusted)", do_notify());
        stacked_used.add(now, kbbuffers.doubleValue() * 1024, "buffers", do_notify());
        stacked_used.add(now, kbcached.doubleValue() * 1024, "cached", do_notify());
        stacked_used.add(now, val1.doubleValue() * 1024, "free", do_notify());
        stacked_used.add(now, kbswpused.doubleValue() * 1024, "swap", do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        this.t_free.add(now, val1.floatValue() * 1024, do_notify());
        this.t_used.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_perc.add(now, val3, do_notify());
        number_of_sample++;
    }

    public XYDataset createfree() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_free);
        return graphcollection;
    }

    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_used);
        return graphcollection;
    }

    public XYDataset createperc() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_perc);
        return graphcollection;
    }

    public void setloadOpt(String s) {
        this.loadOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXKBMEM", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // free
        XYDataset freeset = this.createfree();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        NumberAxis numberaxis1 = new NumberAxis("memfree");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(freeset, null, numberaxis1, minichart1);
        // used
        XYPlot subplot2;
        NumberAxis numberaxis = new NumberAxis("memused");
        NumberFormat decimalformat = new IEEE1541Number(1);
        numberaxis.setNumberFormatOverride(decimalformat);
        if (mysar.showstackedmem) {
            StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2();
            renderer.setSeriesPaint(0, kSarConfig.color3);
            renderer.setSeriesPaint(1, kSarConfig.color4);
            renderer.setSeriesPaint(2, kSarConfig.color5);
            renderer.setSeriesPaint(3, kSarConfig.color6);
            renderer.setSeriesPaint(4, kSarConfig.color7);
            subplot2 = new XYPlot(stacked_used, new DateAxis(null), numberaxis, renderer);
        } else {
            XYDataset usedset = this.createused();
            XYItemRenderer minichart2 = new StandardXYItemRenderer();
            minichart2.setSeriesPaint(0, kSarConfig.color2);
            minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot2 = new XYPlot(usedset, null, numberaxis, minichart2);
        }
        //
        XYDataset percset = this.createperc();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color8);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(percset, null, new NumberAxis("%memused"), minichart3);
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
    private TimeSeries t_free;
    private TimeSeries t_used;
    private TimeSeries t_perc;
    private String loadOpt = new String("");
    private TimeTableXYDataset stacked_used;
}
