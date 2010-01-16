/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
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
public class blockSar extends AllGraph {

    public blockSar(kSar hissar, String s1,  final diskName diskopt) {
        super(hissar);
        Title = new String("Block Transfer");
        this.blockName = s1;
        datain = 0;
        optdisk = diskopt;
        t_tps = new TimeSeries("Transfer/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "Transfer/s", t_tps);
        t_rdsec = new TimeSeries("Read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "Read/s", t_rdsec);
        t_wrsec = new TimeSeries("Write/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "Write/s", t_wrsec);
        t_sect = new TimeSeries("Bytes read/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("Disk " + s1 + "Bytes read/s", t_sect);
    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        Float zerof = new Float(0);
        if (( ! val1.equals(zerof) || ! val2.equals(zerof) || ! val3.equals(zerof) ) && datain == 0) {
            datain = 1;
        }
        Number tmpInt = this.t_tps.getValue(now);
        Float newval;
        if (tmpInt != null) {
            newval = new Float(tmpInt.floatValue() + val1.floatValue());
            this.t_tps.update(now, newval);
        } else {
            this.t_tps.add(now, val1, do_notify());
        }
        if (tmpInt != null) {
            newval = new Float(tmpInt.floatValue() + val2.floatValue());
            this.t_rdsec.update(now, newval);
        } else {
            this.t_rdsec.add(now, val2, do_notify());
        }
        if (tmpInt != null) {
            newval = new Float(tmpInt.floatValue() + val3.floatValue());
            this.t_wrsec.update(now, newval);
        } else {
            this.t_wrsec.add(now, val3, do_notify());
        }
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2) {
        Float zerof = new Float(0);
        if (( ! val1.equals(zerof) || ! val2.equals(zerof) ) && datain == 0) {
            datain = 1;
        }
        this.t_tps.add(now, val1, do_notify());
        this.t_sect.add(now, val2, do_notify());
        number_of_sample++;
    }

    public void setioOpt(String s) {
        this.blockOpt = s;
    }

    public XYDataset createtps() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_tps);
        return graphcollection;
    }

    public XYDataset createsect() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_sect);
        return graphcollection;
    }

    public XYDataset createrws() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_wrsec);
        graphcollection.addSeries(this.t_rdsec);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        if (blockOpt.equals("rd_sec/s") || blockOpt.equals("avgrq-sz")) {
            mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXBLOCK", this.Title, null));
        } else {
            mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXBLOCK", this.blockName, null));
        }
        mysar.add2tree(myroot, mynode);
    }

    public String getcheckBoxTitle() {
        return "Disk " + this.blockName;
    }

    public String getGraphTitle() {
        return (this.Title + " " + optdisk.showTitle() + " for " + mysar.hostName);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        XYPlot subplot2 = null;
        // tps
        XYDataset xydataset1 = this.createtps();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("tps/s"), minichart1);
        // rws
        if (blockOpt.equals("rd_sec/s") || blockOpt.equals("avgrq-sz")) {
            XYDataset rws = this.createrws();
            XYItemRenderer minichart2 = new StandardXYItemRenderer();
            minichart2.setSeriesPaint(0, kSarConfig.color2);
            minichart2.setSeriesPaint(1, kSarConfig.color3);
            minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot2 = new XYPlot(rws, null, new NumberAxis("Read/Write /s"), minichart2);
        } else {
            XYDataset sectset = this.createsect();
            XYItemRenderer minichart2 = new StandardXYItemRenderer();
            minichart2.setSeriesPaint(0, kSarConfig.color4);
            minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            subplot2 = new XYPlot(sectset, null, new NumberAxis("Sect/s"), minichart2);
        }
        plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        // the graph
        JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT, plot, true);
        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        if (setbackgroundimage(mychart) == 1) {
            subplot1.setBackgroundPaint(null);
            subplot2.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_tps;
    private TimeSeries t_rdsec;
    private TimeSeries t_wrsec;
    private TimeSeries t_sect;
    private String blockName;
    private String blockOpt = new String("");
    private final diskName optdisk;
}
