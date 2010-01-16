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

/**
 *
 * @author alex
 */
public class memSar extends AllGraph {

    public memSar(final kSar hissar) {
        super(hissar);
        Title = "Memory usage";
        t_freemem = new TimeSeries("Free memory", org.jfree.data.time.Second.class);
        mysar.dispo.put("Free memory", t_freemem);
        t_freeswap = new TimeSeries("Free swap", org.jfree.data.time.Second.class);
        mysar.dispo.put("Free swap", t_freeswap);
        // Collection
        collectionmem = new TimeSeriesCollection();
        collectionmem.addSeries(this.t_freemem);
        collectionswap = new TimeSeriesCollection();
        collectionswap.addSeries(this.t_freeswap);
    }

    public void add(final Second now,final  Float memInit,final  Float swapInit) {
        this.t_freemem.add(now, memInit, do_notify());
        this.t_freeswap.add(now, swapInit, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARMEM", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        // swap
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        NumberAxis numberaxis1 = new NumberAxis("freswap");
        NumberFormat decimalformat1 = new IEEE1541Number(1024);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(collectionswap, null, numberaxis1, minichart1);
        // mem
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis2 = new NumberAxis("freemem");
        numberaxis2.setNumberFormatOverride(decimalformat1);
        XYPlot subplot2 = new XYPlot(collectionmem, null, numberaxis2, minichart2);
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
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
    private final TimeSeries t_freemem;
    private final TimeSeries t_freeswap;
    private final TimeSeriesCollection collectionmem;
    private final TimeSeriesCollection collectionswap;
}
