/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Solaris;

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

/**
 *
 * @author alex
 */
public class kmaovzSar extends AllGraph {

    public kmaovzSar(final kSar hissar) {
        super(hissar);
        Title = "Kernel oversize page";
        t_alloc = new TimeSeries("Allocated", org.jfree.data.time.Second.class);
        mysar.dispo.put("Kernel oversize page Allocated", t_alloc);
        t_failed = new TimeSeries("Failed", org.jfree.data.time.Second.class);
        mysar.dispo.put("Kernel oversize page Failed", t_failed);
        // Collection
        alloc_collection = new TimeSeriesCollection();
        alloc_collection.addSeries(this.t_alloc);
        failed_collection  = new TimeSeriesCollection();
        failed_collection.addSeries(this.t_failed);
    }

    public void add(final Second now,final Float val1Init,final Float val2Init) {
        this.t_alloc.add(now, val1Init, do_notify());
        this.t_failed.add(now, val2Init, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISKMAOVZ", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // free
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis1 = new NumberAxis("Allocated");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(alloc_collection, null, numberaxis1, minichart1);
        // out
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(failed_collection, null, new NumberAxis("Failed"), minichart2);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 3);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mygraph;
    }
    private final TimeSeries t_alloc;
    private final TimeSeries t_failed;
    private final TimeSeriesCollection alloc_collection;
    private final TimeSeriesCollection failed_collection;
}
