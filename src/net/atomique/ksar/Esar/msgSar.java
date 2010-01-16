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
public class msgSar extends AllGraph {

    public msgSar(final kSar hissar, String cpuID) {
        super(hissar);
        Title = "Messages & Semaphores for CPU "+ cpuID;
        cpuName=cpuID;
        msg = new TimeSeries("msg/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID +" Messages", msg);
        sema = new TimeSeries("sema/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID +" Semaphores", sema);
        //Collection
        ts_collection = new TimeSeriesCollection();
        ts_collection.addSeries(this.msg);
        ts_collection.addSeries(this.sema);
    }

    public void add(final Second now,final  Float val1Int,final  Float val2Int) {
        this.msg.add(now, val1Int, do_notify());
        this.sema.add(now, val2Int, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARMSG", this.Title, null));
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
    
    private final TimeSeries msg;
    private final TimeSeries sema;
    private final TimeSeriesCollection ts_collection;
    private final String cpuName;
}
