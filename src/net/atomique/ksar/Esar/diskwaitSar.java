/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Esar;

import net.atomique.ksar.Solaris.*;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.Trigger;
import net.atomique.ksar.diskName;
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
public class diskwaitSar extends AllGraph {

    public diskwaitSar(final kSar hissar, final String diskname, final diskName diskopt) {
        super(hissar);
        datain = 1;
        Title = "Disk wait";
        mydiskName = diskname;
        optdisk = diskopt;
        t_avqr = new TimeSeries("avquer", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " avquer", t_avqr);
        t_avqw = new TimeSeries("avquew", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " avquew", t_avqw);        
        t_avwait = new TimeSeries("avwait", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " avwait", t_avwait);
        t_busy = new TimeSeries("%busy", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " %busy", t_busy);
        diskbusytrigger = new Trigger(mysar, this, "%busy", t_busy, "up");
        diskbusytrigger.setTriggerValue(kSarConfig.solarisdiskbusytrigger);
        // Collection
        avque_collection = new TimeSeriesCollection();
        avque_collection.addSeries(this.t_avqr);
        avque_collection.addSeries(this.t_avqw);
        avwait_collection = new TimeSeriesCollection();
        avwait_collection.addSeries(this.t_avwait);
        busy_collection = new TimeSeriesCollection();
        busy_collection.addSeries(this.t_busy);
    }

    public void doclosetrigger() {
        diskbusytrigger.doclose();        
    }

    public void add(final Second now, final Float val1, final Float val2, final Float val3, final Float val4) {
        t_busy.add(now, val1, do_notify());          
        diskbusytrigger.doMarker(now, val1);            
        t_avqr.add(now,val2, do_notify());
        t_avqw.add(now,val3, do_notify());
        t_avwait.add(now,val4,do_notify());
        number_of_sample++;
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.mydiskName;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARDISKWAIT", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " on " + optdisk.showTitle() + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(final Second g_start, final Second g_end) {
        // rw
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(avque_collection, null, new NumberAxis("avque r/w"), minichart1);
        // call
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(avwait_collection, null, new NumberAxis("avwait"), minichart2);
        // fork
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(busy_collection, null, new NumberAxis("%busy"), minichart3);

        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        mygraph = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mygraph) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);

        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mygraph.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        diskbusytrigger.setTriggerValue(kSarConfig.solarisdiskbusytrigger);
        diskbusytrigger.tagMarker(subplot3);

        return mygraph;
    }
    private final Trigger diskbusytrigger;
    private final TimeSeries t_avqw;
    private final TimeSeries t_avqr;
    private final TimeSeries t_avwait;
    private final TimeSeries t_busy;
    private final String mydiskName;
    private final diskName optdisk;
    private final TimeSeriesCollection avwait_collection;
    private final TimeSeriesCollection avque_collection;
    private final TimeSeriesCollection busy_collection;
}
