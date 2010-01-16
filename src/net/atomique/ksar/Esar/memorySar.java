/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;

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
public class memorySar extends AllGraph {

    public memorySar(kSar hissar) {
        super(hissar);
        Title = new String("Memory");
        t_kernel = new TimeSeries("kernel", org.jfree.data.time.Second.class);
        mysar.dispo.put("kernel mem", t_kernel);
        t_locked = new TimeSeries("locked", org.jfree.data.time.Second.class);
        mysar.dispo.put("locked mem", t_locked);
        t_avail = new TimeSeries("available", org.jfree.data.time.Second.class);
        mysar.dispo.put("avail mem", t_avail);
        t_free = new TimeSeries("%free", org.jfree.data.time.Second.class);
        mysar.dispo.put("%free mem", t_free);        
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        // ($2-$5)/$2*100)
        this.t_free.add(now, val4/val1*100  );
        this.t_kernel.add(now, val2);
        this.t_locked.add(now, val3);
        this.t_avail.add(now, val4);
        number_of_sample++;
    }

    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_kernel);
        graphcollection.addSeries(this.t_locked);
        return graphcollection;
    }

    public XYDataset createavail() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_avail);
        return graphcollection;
    }

    public XYDataset createfree() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_free);
        return graphcollection;
    }
    
    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARMEMORY", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        NumberAxis numberaxis1 = new NumberAxis("Used");
        NumberFormat decimalformat1 = new IEEE1541Number(1024);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        // tps
        XYDataset xydataset1 = this.createused();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, numberaxis1, minichart1);
        // avail
        XYDataset memavail = this.createavail();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis2 = new NumberAxis("available");
        NumberFormat decimalformat2 = new IEEE1541Number(1024);
        numberaxis2.setNumberFormatOverride(decimalformat2);
        XYPlot subplot2 = new XYPlot(memavail, null, numberaxis2, minichart2);
        // free 
        XYDataset memfree = this.createfree();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(memfree, null, new NumberAxis("% free"), minichart3);
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
            subplot3.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_kernel;
    private TimeSeries t_locked;
    private TimeSeries t_avail;
    private TimeSeries t_free;
    private String ioOpt = new String("");
}
