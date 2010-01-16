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
public class pginSar extends AllGraph {

    public pginSar(kSar hissar) {
        super(hissar);
        Title = new String("Page In");
        t_pgin = new TimeSeries("pgin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page in/s", t_pgin);
        t_pflt = new TimeSeries("pflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page fault/s", t_pflt);
        t_vflt = new TimeSeries("vflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page vfault/s", t_vflt);
    }

    public void add(Second now, Float val1Init, Float val2Init, Float val3Init) {
        this.t_pgin.add(now, val1Init);
        this.t_pflt.add(now, val2Init);
        this.t_vflt.add(now, val3Init);
    }

    public XYDataset createin() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_pgin);
        return timeseriescollection;
    }

    public XYDataset createflt() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_pflt);
        timeseriescollection.addSeries(this.t_vflt);
        return timeseriescollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "MACPGIN", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        XYDataset inset = this.createin();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color1);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(inset, null, new NumberAxis("pgin/s"), minichart2);
        // flt
        XYDataset fltset = this.createflt();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color2);
        minichart3.setSeriesPaint(1, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(fltset, null, new NumberAxis("pflt/vflt /s"), minichart3);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart jfreechart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) jfreechart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        return jfreechart;
    }
    private TimeSeries t_pgin;
    private TimeSeries t_pflt;
    private TimeSeries t_vflt;
}
