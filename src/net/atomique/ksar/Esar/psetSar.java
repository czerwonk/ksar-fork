/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;


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
public class psetSar extends AllGraph {

    public psetSar(kSar hissar, String psetID) {
        super(hissar);
        psetName=psetID;
        Title = "Load for Pset "+ psetID;        
        t_ldavg1 = new TimeSeries("load 1mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Pset " + psetID+" Load 1mn", t_ldavg1);
        t_ldavg5 = new TimeSeries("load 5mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Pset " + psetID+" Load 5mn", t_ldavg5);
        t_ldavg15 = new TimeSeries("load 15mn", org.jfree.data.time.Second.class);
        mysar.dispo.put("Pset " + psetID+" Load 15mn", t_ldavg15);
        t_ncpu = new TimeSeries("ncpus", org.jfree.data.time.Second.class);
        mysar.dispo.put("Pset " + psetID+" cpus", t_ncpu);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {        
        this.t_ldavg1.add(now, val1, do_notify());
        this.t_ldavg5.add(now, val2, do_notify());
        this.t_ldavg15.add(now, val3, do_notify());
        this.t_ncpu.add(now, val4, do_notify());        
        number_of_sample++;
    }

    
    public XYDataset createncpu() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_ncpu);
        return graphcollection;
    }

    public XYDataset createload() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_ldavg1);
        graphcollection.addSeries(this.t_ldavg5);        
        graphcollection.addSeries(this.t_ldavg15);        
        return graphcollection;
    }
    
    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode= new DefaultMutableTreeNode(new GraphDescription(this, "ESARPSET", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset xydataset1 = this.createncpu();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("ncpu"), minichart1);        
        //
        XYDataset loadset = this.createload();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setSeriesPaint(1, kSarConfig.color3);
        minichart2.setSeriesPaint(2, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(loadset, null, new NumberAxis("Load Average"), minichart2);
        // the graph
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
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);            
        }
        return mychart;
    }
    private TimeSeries t_ncpu;
    private TimeSeries t_ldavg1;
    private TimeSeries t_ldavg5;
    private TimeSeries t_ldavg15;  
    private String psetName;
}
