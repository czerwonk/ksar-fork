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
public class diskxferSar extends AllGraph {

    public diskxferSar(final kSar hissar, final String diskname, final diskName diskopt) {
        super(hissar);
        Title = "Disk Transfer";
        datain = 0;
        mydiskName = diskname;
        optdisk = diskopt;
        t_xfer = new TimeSeries("block/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " block/s", t_xfer);
        t_rw = new TimeSeries("read+write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + " read+write/s", t_rw);
        t_avserv = new TimeSeries("avserv/ms", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + mydiskName + "avserv/ms", t_avserv);
        diskavservtrigger = new Trigger(mysar, this, "avserv", t_avserv, "up");
        diskavservtrigger.setTriggerValue(kSarConfig.solarisdiskavservtrigger);
        // Collection
        xfer_collection = new TimeSeriesCollection();
        xfer_collection.addSeries(this.t_xfer);
        rw_collection = new TimeSeriesCollection();
        rw_collection.addSeries(this.t_rw);
        avserv_collection = new TimeSeriesCollection();
        avserv_collection.addSeries(this.t_avserv);
    }

    public void doclosetrigger() {
        diskavservtrigger.doclose();
    }

    public void add(final Second now,  final Float val1Init,final Float val2Init,final Float val3Init) {
        Float zerof = new Float(0);
        if ((!val1Init.equals(zerof) || !val2Init.equals(zerof) || !val3Init.equals(zerof)) && datain == 0) {
            datain = 1;
        }
        Number tmpInt = t_xfer.getValue(now);
        if (tmpInt == null) {
            t_xfer.add(now, val1Init.floatValue() * 512, do_notify());
        } else {
            t_xfer.update(now, new Float((val1Init.floatValue() * 512) + (tmpInt.floatValue())));
        }
        tmpInt = t_rw.getValue(now);
        if (tmpInt == null) {
            t_rw.add(now, val2Init, do_notify());
        } else {
            t_rw.update(now, new Float(tmpInt.floatValue() + val2Init.floatValue()));
        }
        tmpInt = t_avserv.getValue(now);
        if (tmpInt == null) {
            t_avserv.add(now, val3Init, do_notify());
            if (mysar.showtrigger) {
                diskavservtrigger.doMarker(now, val3Init);
            }
        } else {
            t_avserv.update(now, new Float((tmpInt.floatValue() + val3Init.floatValue()) / 2));
            if (mysar.showtrigger) {
                diskavservtrigger.doMarker(now, tmpInt);
            }
        }
        number_of_sample++;
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.mydiskName;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "SOLARISDISKXFER", this.Title, null));
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
        NumberAxis numberaxis1 = new NumberAxis("byte/s");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(xfer_collection, null, numberaxis1, minichart1);
        // call
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(rw_collection, null, new NumberAxis("read+write/s"), minichart2);
        // fork
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(avserv_collection, null, new NumberAxis("avserv/ms"), minichart3);
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

        diskavservtrigger.setTriggerValue(kSarConfig.solarisdiskavservtrigger);
        diskavservtrigger.tagMarker(subplot3);

        return mygraph;
    }
    private final Trigger diskavservtrigger;
    private final TimeSeries t_xfer;
    private final TimeSeries t_rw;
    private final TimeSeries t_avserv;
    private final String mydiskName;
    private final diskName optdisk;
    private final TimeSeriesCollection xfer_collection;
    private final TimeSeriesCollection rw_collection;
    private final TimeSeriesCollection avserv_collection;
}
