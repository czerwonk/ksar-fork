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
public class PidMemSar extends AllGraph {

    public PidMemSar(kSar hissar, String prog, String pid) {
        super(hissar);
        mypid = pid;
        Title = new String("Mem PID " + pid + " " + prog);
        t_VSZ = new TimeSeries("VSZ Memory", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " VSZ memory", t_VSZ);
        t_RSS = new TimeSeries("RSS Memory", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " RSS memory", t_RSS);
        t_percent = new TimeSeries("% Memory", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " %Memory", t_percent);
        t_minflt = new TimeSeries("minflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " minflt/s", t_minflt);
        t_majflt = new TimeSeries("majflt/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " majflt/s" , t_majflt);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_minflt.add(now,val1, do_notify());
        this.t_majflt.add(now,val2, do_notify());
        this.t_VSZ.add(now, val3, do_notify());
        this.t_RSS.add(now, val4, do_notify());
        this.t_percent.add(now, val5, do_notify());
        number_of_sample++;
    }

    public XYDataset createflt() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_minflt);
        graphcollection.addSeries(this.t_majflt);
        return graphcollection;
    }

    public XYDataset createVSZ() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_VSZ);
        return graphcollection;
    }
    
    public XYDataset createRSS() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_RSS);
        return graphcollection;
    }
    
    public XYDataset createpct() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_percent);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXPIDMEM", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public String getMypid() {
        return mypid;
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createflt();
        XYPlot subplot1;
        NumberAxis usedaxis = new NumberAxis("min/maj flt/s");
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        subplot1 = new XYPlot(xydataset1, null, usedaxis, minichart1);
        // user
        XYDataset idleset = this.createVSZ();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("VSZ"), minichart2);
        // nice 
        XYDataset niceset = this.createRSS();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(niceset, null, new NumberAxis("RSS"), minichart3);

        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 2);
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
    private TimeSeries t_RSS;
    private TimeSeries t_VSZ;
    private TimeSeries t_percent;
    private TimeSeries t_minflt;
    private TimeSeries t_majflt;
    private String mypid;
}
