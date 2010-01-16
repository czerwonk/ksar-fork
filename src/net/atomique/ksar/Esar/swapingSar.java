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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 *
 * @author alex
 */
public class swapingSar extends AllGraph {

    public swapingSar(final kSar hissar, String cpuID) {
        super(hissar);
        Title = "Swapping for CPU " + cpuID;
        cpuName=cpuID;
        swpin = new TimeSeries("LWP in", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " LWP swap in", swpin);
        bswin = new TimeSeries("pages in", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Swap page in", bswin);
        swpot = new TimeSeries("LWP out", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " LWP swap out", swpot);
        bswot = new TimeSeries("pages out", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " Swap page out", bswot);
        pswch = new TimeSeries("LWP switch", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+cpuID+ " LWP switch", pswch);
        // Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.swpin);
        ts_collection.addSeries(this.bswin);
        ts_collection.addSeries(this.swpot);
        ts_collection.addSeries(this.bswot);
        ts_collection.addSeries(this.pswch);
        
    }

    public void add(final Second now,final Float swpinInt,final Float bswinInt,final Float swpotInt,final Float bswotInt,final Float pswchInt) {
        swpin.add(now, swpinInt, do_notify());
        bswin.add(now, bswinInt.floatValue() * 512, do_notify());
        swpot.add(now, swpotInt, do_notify());
        bswot.add(now, bswotInt.floatValue() * 512, do_notify());
        pswch.add(now, pswchInt, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARSWAP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final  Second g_end) {
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
    
    private final TimeSeries swpin;
    private final TimeSeries bswin;
    private final TimeSeries swpot;
    private final TimeSeries bswot;
    private final TimeSeries pswch;
    private final  TimeSeriesCollection ts_collection;
    private final String cpuName;
}
