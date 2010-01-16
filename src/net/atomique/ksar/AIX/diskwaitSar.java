/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.AIX;

import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
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
public class diskwaitSar extends AllGraph {

    public diskwaitSar(final kSar hissar,final String s1) {
        super(hissar);
        Title = new String("Disk wait");
        this.diskName = new String(s1);
        datain = 0;
        t_avque = new TimeSeries("avque", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "avque", t_avque);
        t_avwait = new TimeSeries("avwait", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "avwait", t_avwait);
        t_busy = new TimeSeries("%busy", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "%busy", t_busy);
        //trigger
        diskbusytrigger = new Trigger(mysar, this, "%busy", t_busy, "up");
        diskbusytrigger.setTriggerValue(kSarConfig.aixdiskbusytrigger);
        diskavquetrigger = new Trigger(mysar, this, "avque", t_avque, "up");
        diskavquetrigger.setTriggerValue(kSarConfig.aixdiskavquetrigger);
    }

    public void doclosetrigger() {
        diskbusytrigger.doclose();
        diskavquetrigger.doclose();
    }

    public void add(final Second now,final Float val1Init,final Float val2Init,final Float val3Init) {
        Float zerof = new Float(0);
        if (( ! val1Init.equals(zerof) || ! val2Init.equals(zerof) || ! val3Init.equals(zerof))  && datain == 0) {
            datain = 1;
        }
        /* this test if for FCAL loop with 2-attachement */
        Number tmpInt = this.t_avque.getValue(now);
        Float newval;
        if (tmpInt ==null) {
            this.t_avque.add(now, val1Init);
            diskavquetrigger.doMarker(now, val1Init);
        } else {
            newval = new Float(tmpInt.floatValue() + val1Init.floatValue());
            this.t_avque.update(now, newval);
            diskavquetrigger.doMarker(now, newval);
        }
        tmpInt = this.t_avwait.getValue(now);
        if (tmpInt == null) {
            this.t_avwait.add(now, val2Init);
        } else {
            newval = new Float((tmpInt.floatValue() + val2Init.floatValue()) / 2);
            this.t_avwait.update(now, newval);
        }
        tmpInt = this.t_busy.getValue(now);
        if (tmpInt == null) {
            this.t_busy.add(now, val3Init);
            diskbusytrigger.doMarker(now, val3Init);
        } else {
            newval = new Float((tmpInt.floatValue() + val3Init.floatValue()) / 2);
            this.t_busy.update(now, newval);
            diskbusytrigger.doMarker(now, newval); 
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
        return "Disk " + this.diskName;
    }

    public void addtotree(final DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "AIXDISKWAIT", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public String getGraphTitle() {
        return (this.Title + " on " + this.diskName + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(final Second g_start,final Second g_end) {
        // rw
        XYDataset avqueset = this.createavque();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(avqueset, null, new NumberAxis("avque"), minichart1);
        // call
        XYDataset avwaitset = this.createavwait();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(avwaitset, null, new NumberAxis("avwait"), minichart2);
        // fork
        XYDataset busyset = this.createbusy();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color4);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(busyset, null, new NumberAxis("%busy"), minichart3);

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

        diskbusytrigger.setTriggerValue(kSarConfig.aixdiskbusytrigger);
        diskbusytrigger.tagMarker(subplot3);

        diskavquetrigger.setTriggerValue(kSarConfig.aixdiskavquetrigger);
        diskavquetrigger.tagMarker(subplot1);


        return jfreechart;
    }
    final Trigger diskbusytrigger;
    final Trigger diskavquetrigger;
    final TimeSeries t_avque;
    final TimeSeries t_avwait;
    final TimeSeries t_busy;
    final String diskName;
}
