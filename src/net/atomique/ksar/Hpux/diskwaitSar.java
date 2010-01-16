/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Hpux;

import java.awt.Color;
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
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class diskwaitSar extends AllGraph {

    public diskwaitSar(kSar hissar, String s1, diskName diskopt) {
        super(hissar);
        datain = 0;
        Title = new String("Disk wait");
        this.mydiskName = s1;
        optdisk = diskopt;
        t_avque = new TimeSeries("avque", org.jfree.data.time.Second.class);
        t_avwait = new TimeSeries("avwait", org.jfree.data.time.Second.class);
        t_busy = new TimeSeries("%busy", org.jfree.data.time.Second.class);
        diskbusytrigger = new Trigger(mysar, this, "%busy", t_busy, "up");
        diskbusytrigger.setTriggerValue(kSarConfig.hpuxdiskbusytrigger);
        diskavquetrigger = new Trigger(mysar, this, "avque", t_avque, "up");
        diskavquetrigger.setTriggerValue(kSarConfig.hpuxdiskavquetrigger);
    }

    public void doclosetrigger() {
        diskbusytrigger.doclose();
        diskavquetrigger.doclose();
    }

    public void add(Second now, Float val1Init, Float val2Init, Float val3Init) {
        Float zerof = new Float(0);
        if ((val1Init != zerof || val2Init != zerof || val3Init != zerof) && datain == 0) {
            datain = 1;
        }
        /* this test if for FCAL loop with 2-attachement */
        Number tmpInt = this.t_avque.getValue(now);
        Float newval;
        if (tmpInt != null) {
            newval = new Float(tmpInt.floatValue() + val1Init.floatValue());
            this.t_avque.update(now, newval);
            diskavquetrigger.doMarker(now, newval);
        } else {
            this.t_avque.add(now, val1Init);
            diskavquetrigger.doMarker(now, val1Init);
        }
        tmpInt = this.t_avwait.getValue(now);
        if (tmpInt != null) {
            newval = new Float((tmpInt.floatValue() + val2Init.floatValue()) / 2);
            this.t_avwait.update(now, newval);
        } else {
            this.t_avwait.add(now, val2Init.floatValue());
        }
        tmpInt = this.t_busy.getValue(now);
        if (tmpInt != null) {
            newval = new Float((tmpInt.floatValue() + val3Init.floatValue()) / 2);
            this.t_busy.update(now, newval);
            diskbusytrigger.doMarker(now, newval);
        } else {
            this.t_busy.add(now, val3Init);
            diskbusytrigger.doMarker(now, val3Init);
        }
    }

    public XYDataset createavque() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_avque);
        return timeseriescollection;
    }

    public XYDataset createavwait() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_avwait);
        return timeseriescollection;
    }

    public XYDataset createbusy() {
        TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
        timeseriescollection.addSeries(this.t_busy);
        return timeseriescollection;
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.mydiskName;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXDISKWAIT", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " on " + optdisk.showTitle() + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // rw
        XYDataset avqueset = this.createavque();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot1 = new XYPlot(avqueset, null, new NumberAxis("avque"), minichart1);
        // call
        XYDataset avwaitset = this.createavwait();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(avwaitset, null, new NumberAxis("avwait"), minichart2);
        // fork
        XYDataset busyset = this.createbusy();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(busyset, null, new NumberAxis("%busy"), minichart3);

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

        diskbusytrigger.setTriggerValue(kSarConfig.hpuxdiskbusytrigger);
        diskbusytrigger.tagMarker(subplot3);

        diskavquetrigger.setTriggerValue(kSarConfig.hpuxdiskavquetrigger);
        diskavquetrigger.tagMarker(subplot1);

        return mychart;
    }
    private Trigger diskbusytrigger;
    private Trigger diskavquetrigger;
    private TimeSeries t_avque;
    private TimeSeries t_avwait;
    private TimeSeries t_busy;
    private String mydiskName;
    private diskName optdisk;
}
