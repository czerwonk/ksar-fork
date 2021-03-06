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
public class PidCpuSar extends AllGraph {

    public PidCpuSar(kSar hissar, String prog, String pid) {
        super(hissar);
        mypid = pid;
        Title = new String("Cpu PID " + pid + " " + prog);
        t_usr = new TimeSeries("%User", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " %User", t_usr);
        t_sys = new TimeSeries("%System", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " %System", t_sys);
        t_percent = new TimeSeries("%CPU", org.jfree.data.time.Second.class);
        mysar.dispo.put("PID " + pid + " " + prog + " %CPU", t_percent);

    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        this.t_usr.add(now, val1, do_notify());
        this.t_sys.add(now, val2, do_notify());
        this.t_percent.add(now, val3, do_notify());
        number_of_sample++;
    }

    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_percent);
        return graphcollection;
    }

    public XYDataset createUser() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_usr);
        return graphcollection;
    }

    public XYDataset createSystem() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_sys);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXPIDCPU", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getMypid() {
        return mypid;
    }
    
    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createused();
        XYPlot subplot1;
        NumberAxis usedaxis = new NumberAxis("% used cpu");
        if (mysar.show100axiscpu) {
            usedaxis.setRange(0.0D, 100D);
        }
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        subplot1 = new XYPlot(xydataset1, null, usedaxis, minichart1);
        // user
        XYDataset idleset = this.createUser();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(idleset, null, new NumberAxis("% User"), minichart2);
        // nice 
        XYDataset niceset = this.createSystem();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(niceset, null, new NumberAxis("% System"), minichart3);
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
    
    private String mypid;
    private TimeSeries t_usr;
    private TimeSeries t_sys;
    private TimeSeries t_percent;
    }
