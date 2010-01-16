/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import java.text.NumberFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.IEEE1541Number;
import net.atomique.ksar.Trigger;
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
public class diskxferSar extends AllGraph {

    public diskxferSar(final kSar hissar,final String s1) {
        super(hissar);
        Title = new String("Disk Transfer");
        this.diskName = s1;
        datain = 0;
        t_xfer = new TimeSeries("block/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " block/s", t_xfer);
        t_kbs = new TimeSeries("Kbs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " Kb/s", t_kbs);
        t_rw = new TimeSeries("read+write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " read+write/s", t_rw);
        t_avserv = new TimeSeries("avserv/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + " avser/s", t_avserv);
        //trigger
        diskavservtrigger = new Trigger(mysar, this, "avserv", t_avserv, "up");
        diskavservtrigger.setTriggerValue(kSarConfig.aixdiskavservtrigger);
    }

    public void doclosetrigger() {
        diskavservtrigger.doclose();
    }

    public void setdiskOpt(final String s) {
        this.diskOpt = s;
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init) {
        Float zerof = new Float(0);
        if (( ! val1Init.equals(zerof) || ! val2Init.equals(zerof) || val3Init.equals(zerof)) && datain == 0) {
            datain = 1;
        }
        Number tmpInt = this.t_xfer.getValue(now);
        if ("device".equals(diskOpt)) {
            tmpInt = this.t_xfer.getValue(now);
            if (tmpInt == null) {
                this.t_xfer.add(now, new Float(val1Init.floatValue() * 512));
            } else {

                this.t_xfer.update(now, new Float((val1Init.floatValue() * 512) + (tmpInt.floatValue())));
            }
        }

        if ("Kbs/s".equals(diskOpt)) {
            tmpInt = this.t_kbs.getValue(now);
            if (tmpInt == null) {
                this.t_kbs.add(now, new Float(val1Init.floatValue() * 1024));
            } else {
                this.t_kbs.update(now, new Float((val1Init.floatValue() * 1024) + (tmpInt.floatValue())));

            }
        }

        tmpInt = this.t_rw.getValue(now);
        if (tmpInt == null) {
            this.t_rw.add(now, val2Init);
        } else {
            this.t_rw.update(now, new Float(tmpInt.floatValue() + val2Init.floatValue()));
        }
        tmpInt = this.t_avserv.getValue(now);
        if (tmpInt == null) {
            this.t_avserv.add(now, val3Init);
            diskavservtrigger.doMarker(now, val3Init);

        } else {
            this.t_avserv.update(now, new Float((tmpInt.floatValue() + val3Init.floatValue()) / 2));
            diskavservtrigger.doMarker(now, tmpInt);

        }
    }

    public XYDataset createxfer() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        if ("device".equals(diskOpt)) {
            timeseriescollection.addSeries(this.t_xfer);
        }
        if ("Kbs/s".equals(diskOpt)) {
            timeseriescollection.addSeries(this.t_kbs);
        }
        return timeseriescollection;
    }

    public XYDataset createrw() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_rw);
        return timeseriescollection;
    }

    public XYDataset createavserv() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_avserv);
        return timeseriescollection;
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.diskName;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXDISKXFER", this.Title, null));
        mysar.add2tree(myroot, mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " on " + this.diskName + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // rw
        XYDataset xferset = this.createxfer();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        NumberAxis numberaxis1 = new NumberAxis("byte/s");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(xferset, null, numberaxis1, minichart1);
        // call
        XYDataset rwset = this.createrw();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(rwset, null, new NumberAxis("read+write/s"), minichart2);
        // fork
        XYDataset avservset = this.createavserv();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(avservset, null, new NumberAxis("avserv"), minichart3);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart jfreechart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(jfreechart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) jfreechart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }

        diskavservtrigger.setTriggerValue(kSarConfig.aixdiskavservtrigger);
        diskavservtrigger.tagMarker(subplot3);

        return jfreechart;
    }
    final Trigger diskavservtrigger;
    final TimeSeries t_xfer;
    final TimeSeries t_kbs;
    final TimeSeries t_rw;
    final TimeSeries t_avserv;
    final String diskName;
    private String diskOpt = new String("device");
}
