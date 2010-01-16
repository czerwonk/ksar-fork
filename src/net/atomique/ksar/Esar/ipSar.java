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
public class ipSar extends AllGraph {
//inRcvs/s  inDlvrs/s  noPorts/s outRqsts/s
    public ipSar(kSar hissar) {
        super(hissar);
        Title = new String("IP");
        t_inrcvs = new TimeSeries("inRcvs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("ip inRcvs/s", t_inrcvs);
        t_indlvrs = new TimeSeries("inDlvrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("ip inDlvrs/s", t_indlvrs);
        t_noports = new TimeSeries("noPorts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("ip noPorts/s", t_noports);
        t_outrqsts = new TimeSeries("outRqsts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("ip outRqsts/s", t_outrqsts);        
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_inrcvs.add(now, val1);
        this.t_indlvrs.add(now, val2);
        this.t_noports.add(now, val3);
        this.t_outrqsts.add(now, val4);
        number_of_sample++;
    }

    public XYDataset createin() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_inrcvs);
        graphcollection.addSeries(this.t_indlvrs);
        return graphcollection;
    }

    public XYDataset createout() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_outrqsts);        
        return graphcollection;
    }
    
    public XYDataset createerr() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_noports);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARIP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        // tps
        XYDataset xydataset1 = this.createin();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("inRcvs/inDlvrs /s"), minichart1);
        // nice 
        XYDataset xydataset2 = this.createout();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(xydataset2, null, new NumberAxis("outRqsts/s"), minichart2);
        //
        XYDataset xydataset3 = this.createerr();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(xydataset3, null, new NumberAxis("noPorts/s"), minichart3);
        // the graph
        plot = new CombinedDomainXYPlot(new DateAxis(""));
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
        }
        return mychart;
    }
    private TimeSeries t_inrcvs;
    private TimeSeries t_indlvrs;
    private TimeSeries t_noports;
    private TimeSeries t_outrqsts;
    
    
}
