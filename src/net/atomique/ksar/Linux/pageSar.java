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
public class pageSar extends AllGraph {

    public pageSar(kSar hissar) {
        super(hissar);
        Title = new String("Page");
        t_frmpg = new TimeSeries("frmpg/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page frmpg/s", t_frmpg);
        t_shmpg = new TimeSeries("shmpg/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page shmpg/s", t_shmpg);
        t_bufpg = new TimeSeries("bufpg/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page bufpg/s", t_bufpg);
        t_campg = new TimeSeries("campg/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Page campg/s", t_campg);
    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        this.t_frmpg.add(now, val1, do_notify());
        this.t_bufpg.add(now, val2, do_notify());
        this.t_campg.add(now, val3, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_frmpg.add(now, val1, do_notify());
        this.t_shmpg.add(now, val2, do_notify());
        this.t_bufpg.add(now, val3, do_notify());
        this.t_campg.add(now, val4, do_notify());
        number_of_sample++;
    }

    public XYDataset createfrmpg() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_frmpg);
        return graphcollection;
    }

    public XYDataset createshmpg() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_shmpg);
        return graphcollection;
    }

    public XYDataset createbufpg() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_bufpg);
        return graphcollection;
    }

    public XYDataset createcampg() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_campg);
        return graphcollection;
    }

    public void setpageOpt(String s) {
        this.pageOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXPAGE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createfrmpg();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("frmpg/s"), minichart1);
        // idle
        XYDataset bufpgset = this.createbufpg();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(bufpgset, null, new NumberAxis("bufpg/s"), minichart2);
        //
        XYDataset campgset = this.createcampg();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(campgset, null, new NumberAxis("campg/s"), minichart3);
        //
        XYPlot subplot4 = null;
        if (pageOpt.equals("shmpg/s")) {
            XYDataset shmpgset = this.createshmpg();
            XYItemRenderer minichart4 = new StandardXYItemRenderer();
            minichart4.setSeriesPaint(0, kSarConfig.color4);
            minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            NumberAxis axis = new NumberAxis("shmpg/s");
            axis.setAutoRangeIncludesZero(true);
            subplot4 = new XYPlot(shmpgset, null, axis, minichart4);

        }
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        if (pageOpt.equals("shmpg/s")) {
            plot.add(subplot4, 1);
        }
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
            if (pageOpt.equals("shmpg/s")) {
                subplot4.setBackgroundPaint(null);
            }
        }
        return mychart;
    }
    private TimeSeries t_shmpg;
    private TimeSeries t_bufpg;
    private TimeSeries t_campg;
    private TimeSeries t_frmpg;
    private String pageOpt = new String("");
}
