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
import org.jfree.chart.ChartFactory;
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

/**
 *
 * @author alex
 */
public class fileSar extends AllGraph {

    public fileSar(final kSar hissar, String cpuID) {
        super(hissar);
        Title = "File for CPU " + cpuID;
        cpuName=cpuID;
        iget = new TimeSeries("iget/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID +" iget/s", iget);
        namei = new TimeSeries("namei/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID +" namei/s", namei);
        dirbk = new TimeSeries("dirbk/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU " + cpuID +" dirbk/s", dirbk);
        // Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.iget);
        ts_collection.addSeries(this.namei);
        ts_collection.addSeries(this.dirbk);
        
    }

    public void add(final Second now,final Float val1Int,final Float val2Int,final Float val3Int) {
        this.iget.add(now, val1Int, do_notify());
        this.namei.add(now, val2Int, do_notify());
        this.dirbk.add(now, val3Int, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARFILE", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(ts_collection, null, new NumberAxis("per second"), minichart1);
        
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;

    }
    private final TimeSeries iget;
    private final TimeSeries namei;
    private final TimeSeries dirbk;
    private final TimeSeriesCollection ts_collection;
    private final String cpuName;
}
