/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.diskName;
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
public class block2Sar extends AllGraph {

    public block2Sar(kSar hissar, String s1, final diskName diskopt) {
        super(hissar);
        Title = new String("Block Wait");
        this.blockName = s1;
        optdisk = diskopt;
        datain = 0;
        t_avgrq = new TimeSeries("avgrq-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " avgrq-sz", t_avgrq);
        t_avgqu = new TimeSeries("avgqu-sz", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " avgqu-sz", t_avgqu);
        t_await = new TimeSeries("await", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " await", t_await);
        t_svctm = new TimeSeries("svctm", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " svctm", t_svctm);
        t_util = new TimeSeries("util%", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " util%", t_util);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        Float zerof = new Float(0);
        if (( ! val1.equals(zerof) || ! val2.equals(zerof) || ! val3.equals(zerof) || !val4.equals(zerof) || !val5.equals(zerof)) && datain == 0) {
            datain = 1;
        }
        this.t_avgrq.add(now, val1, do_notify());
        this.t_avgqu.add(now, val2, do_notify());
        this.t_await.add(now, val3, do_notify());
        this.t_svctm.add(now, val4, do_notify());
        this.t_util.add(now, val5, do_notify());
        number_of_sample++;
    }

    public void setioOpt(String s) {
        this.blockOpt = s;
    }

    public XYDataset createavgrq() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_avgrq);
        return graphcollection;
    }

    public XYDataset createavgqu() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_avgqu);
        return graphcollection;
    }

    public XYDataset createawait() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_await);
        return graphcollection;
    }

    public XYDataset createutil() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_util);
        return graphcollection;
    }

    public XYDataset createsvctm() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_svctm);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXBLOCK2", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.blockName;
    }

    public String getGraphTitle() {
        return (this.Title + " " + optdisk.showTitle() + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        XYDataset avgrqset = this.createavgrq();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(avgrqset, null, new NumberAxis("avgrq-sz"), minichart1);

        XYDataset avgquset = this.createavgqu();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(avgquset, null, new NumberAxis("avgqu-sz"), minichart2);

        XYDataset awaitset = this.createawait();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(awaitset, null, new NumberAxis("await"), minichart3);

        XYDataset svctmset = this.createsvctm();
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color4);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(svctmset, null, new NumberAxis("svctm"), minichart4);

        XYDataset utilset = this.createutil();
        XYItemRenderer minichart5 = new StandardXYItemRenderer();
        minichart5.setSeriesPaint(0, kSarConfig.color5);
        minichart5.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot5 = new XYPlot(utilset, null, new NumberAxis("util%"), minichart5);

        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.add(subplot5, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);

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
            subplot5.setBackgroundPaint(null);
        }
        
        return mychart;
    }
    private TimeSeries t_avgrq;
    private TimeSeries t_avgqu;
    private TimeSeries t_await;
    private TimeSeries t_svctm;
    private TimeSeries t_util;
    private String blockName;
    private final diskName optdisk;
    private String blockOpt = new String("");
}
