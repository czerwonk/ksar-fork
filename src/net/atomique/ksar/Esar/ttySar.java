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
public class ttySar extends AllGraph {

    public ttySar(final kSar hissar, final String cpuID) {
        super(hissar);
        Title = "Tty for CPU " + cpuID;
        cpuName=cpuID;
        t_rawch = new TimeSeries("Rawch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Rawch/s", t_rawch);
        t_canch = new TimeSeries("Canch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Canch/s", t_canch);
        t_outch = new TimeSeries("Outch/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Outch/s", t_outch);
        t_rcvin = new TimeSeries("Rcvin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Rcvin/s", t_rcvin);
        t_xmtin = new TimeSeries("Xmtin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Xmtin/s",t_xmtin);
        t_mdmin = new TimeSeries("Mdmin/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("CPU "+ cpuID+ " Mdmin/s",t_mdmin);
        // Collection
        tscollection = new TimeSeriesCollection();
        tscollection.addSeries(this.t_rawch);
        tscollection.addSeries(this.t_canch);
        tscollection.addSeries(this.t_outch);
        tscollection.addSeries(this.t_rcvin);
        tscollection.addSeries(this.t_xmtin);
        tscollection.addSeries(this.t_mdmin);
    }

    public void add(final Second now, final Float val1Init, final Float val2Init,final Float val3Init,final Float val4Init, final Float val5Init,final Float val6Init) {
        this.t_rawch.add(now, val1Init, do_notify());
        this.t_canch.add(now, val2Init, do_notify());
        this.t_outch.add(now, val3Init, do_notify());
        this.t_rcvin.add(now, val4Init, do_notify());
        this.t_xmtin.add(now, val5Init, do_notify());
        this.t_mdmin.add(now, val6Init, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARTTY", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setSeriesPaint(2, kSarConfig.color3);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(tscollection, null, new NumberAxis("per second"), minichart1);
        
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
    
    private final TimeSeries t_rawch;
    private final TimeSeries t_canch;
    private final TimeSeries t_outch;
    private final TimeSeries t_rcvin;
    private final TimeSeries t_xmtin;
    private final TimeSeries t_mdmin;
    private final TimeSeriesCollection tscollection;
    private final String cpuName;
}
