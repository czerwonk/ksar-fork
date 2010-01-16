/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Hpux;

import java.awt.Color;
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
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class diskxferSar extends AllGraph {

    public diskxferSar(kSar hissar, String s1, diskName diskopt) {
        super(hissar);
        Title = new String("Disk Transfer");
        datain = 0;
        this.mydiskName = s1;
        optdisk = diskopt;
        t_xfer = new TimeSeries("block/s", org.jfree.data.time.Second.class);
        t_rw = new TimeSeries("read+write/s", org.jfree.data.time.Second.class);
        t_avserv = new TimeSeries("avserv/s", org.jfree.data.time.Second.class);
        diskavservtrigger = new Trigger(mysar, this, "avserv", t_avserv, "up");
        diskavservtrigger.setTriggerValue(kSarConfig.hpuxdiskavservtrigger);
    }

    public void doclosetrigger() {
        diskavservtrigger.doclose();
    }

    public void add(Second now, Float val1Init, Float val2Init, Float val3Init) {
        Float zerof = new Float(0);
        if ((val1Init != zerof || val2Init != zerof || val3Init != zerof) && datain == 0) {
            datain = 1;
        }
        Number tmpInt = this.t_xfer.getValue(now);
        if (tmpInt != null) {
            this.t_xfer.update(now, new Float((val1Init.floatValue() * 512) + (tmpInt.floatValue())));
        } else {
            this.t_xfer.add(now, val1Init.floatValue() * 512);
        }
        tmpInt = this.t_rw.getValue(now);
        if (tmpInt != null) {
            this.t_rw.update(now, new Float(tmpInt.floatValue() + val2Init.floatValue()));
        } else {
            this.t_rw.add(now, val2Init);
        }
        tmpInt = this.t_avserv.getValue(now);
        if (tmpInt != null) {
            this.t_avserv.update(now, new Float((tmpInt.floatValue() + val3Init.floatValue()) / 2));
            diskavservtrigger.doMarker(now, tmpInt);
        } else {
            this.t_avserv.add(now, val3Init);
            diskavservtrigger.doMarker(now, val3Init);
        }
    }

    public XYDataset createxfer() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_xfer);
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
        return "Disk " + this.mydiskName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXDISKXFER", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " on " + optdisk.showTitle() + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // rw
        XYDataset xferset = this.createxfer();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis1 = new NumberAxis("byte/s");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(xferset, null, numberaxis1, minichart1);
        // call
        XYDataset rwset = this.createrw();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(rwset, null, new NumberAxis("read+write/s"), minichart2);
        // fork
        XYDataset avservset = this.createavserv();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(avservset, null, new NumberAxis("avserv"), minichart3);
        // PANEL
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
            subplot3.setBackgroundPaint(null);
        }
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }


        diskavservtrigger.setTriggerValue(kSarConfig.hpuxdiskavservtrigger);
        diskavservtrigger.tagMarker(subplot3);

        return mychart;
    }
    private Trigger diskavservtrigger;
    private TimeSeries t_xfer;
    private TimeSeries t_rw;
    private TimeSeries t_avserv;
    private String mydiskName;
    private diskName optdisk;
}
