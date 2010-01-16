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

/**
 *
 * @author alex
 */
public class tcpSar extends AllGraph {

    public tcpSar(final kSar hissar) {
        super(hissar);
        Title = "TCP";    
        t_actvOpens = new TimeSeries("actvOpens/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("actvOpens/s", t_actvOpens);
        
        t_atmptFails = new TimeSeries("atmptFails/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("atmptFails/s", t_atmptFails);
        t_currEstab = new TimeSeries("currEstab", org.jfree.data.time.Second.class);
        mysar.dispo.put("currEstab", t_currEstab);
        t_estabRsts = new TimeSeries("estabRsts/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("estabRsts/s", t_estabRsts);
        t_hlfOpenDrp = new TimeSeries("hlfOpenDrp/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("hlfOpenDrp/s", t_hlfOpenDrp);
        t_listenDrop = new TimeSeries("istenDrop/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("listenDrop/s", t_listenDrop);
        t_listDropQ0 = new TimeSeries("listenDropQ0/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("listenDropQ0/s", t_listDropQ0);
        t_passvOpens = new TimeSeries("passvOpens/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("passvOpens/s", t_passvOpens);
        // Collection
        in_collection = new TimeSeriesCollection();
        in_collection.addSeries(this.t_passvOpens);
        in_collection.addSeries(this.t_atmptFails);
        
        out_collection = new TimeSeriesCollection();
        out_collection.addSeries(this.t_actvOpens);
        out_collection.addSeries(this.t_estabRsts);
        
        lst_collection = new TimeSeriesCollection();
        lst_collection.addSeries(this.t_hlfOpenDrp);
        lst_collection.addSeries(this.t_listenDrop);
        lst_collection.addSeries(this.t_listDropQ0);
        
        cur_collection = new TimeSeriesCollection();
        cur_collection.addSeries(this.t_currEstab);
    }

    public void add(final Second now,final Float val1,final Float val2,final Float val3,final Float val4,final Float val5,final Float val6,final Float val7,final Float val8) {
        this.t_actvOpens.add(now, val1, do_notify());
        this.t_atmptFails.add(now, val2, do_notify());
        this.t_currEstab.add(now, val3, do_notify());
        this.t_estabRsts.add(now, val4, do_notify());
        this.t_hlfOpenDrp.add(now, val5, do_notify());
        this.t_listenDrop.add(now, val6, do_notify());
        this.t_listDropQ0.add(now, val7, do_notify());
        this.t_passvOpens.add(now, val8, do_notify());
        number_of_sample++;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARTCP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(final Second g_start,final  Second g_end) {
        // free
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(in_collection, null, new NumberAxis("in"), minichart1);
        // out
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart1.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(out_collection, null, new NumberAxis("out"), minichart2);
        // scan
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setSeriesPaint(1, kSarConfig.color6);
        minichart3.setSeriesPaint(2, kSarConfig.color7);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(lst_collection, null, new NumberAxis("queue"), minichart3);
        // ufs
        XYItemRenderer minichart4 = new StandardXYItemRenderer();
        minichart4.setSeriesPaint(0, kSarConfig.color8);
        minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot4 = new XYPlot(cur_collection, null, new NumberAxis("current"), minichart4);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.add(subplot4, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
            subplot4.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        return mygraph;
    }
    
    private final TimeSeries t_actvOpens;
    private final TimeSeries t_atmptFails;
    private final TimeSeries t_currEstab;
    private final TimeSeries t_estabRsts;
    private final TimeSeries t_hlfOpenDrp;
    private final TimeSeries t_listenDrop;
    private final TimeSeries t_listDropQ0;
    private final TimeSeries t_passvOpens;
    private final TimeSeriesCollection in_collection;
    private final TimeSeriesCollection out_collection;
    private final TimeSeriesCollection lst_collection;
    private final TimeSeriesCollection cur_collection;
    
}
