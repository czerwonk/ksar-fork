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
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author alex
 */
public class rawipSar extends AllGraph {

    public rawipSar(kSar hissar) {
        super(hissar);
        Title = new String("RAWIP");
        t_indgm = new TimeSeries("inDgms/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawip inDgms/s", t_indgm);
        t_inerr = new TimeSeries("inErrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawip inErrs/s", t_inerr);
        t_outdgm = new TimeSeries("outDgms/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawip outDgms/s", t_outdgm);
        t_outerr = new TimeSeries("outErrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawip outErrs/s", t_outerr);
        t_chkerr = new TimeSeries("cksErrs/s", org.jfree.data.time.Second.class);
        mysar.dispo.put("rawip cksErrs/s", t_chkerr);
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4, Float val5) {
        this.t_indgm.add(now, val1);
        this.t_inerr.add(now, val2);
        this.t_outdgm.add(now, val3);
        this.t_outerr.add(now, val4);
        this.t_chkerr.add(now, val5);
        number_of_sample++;
    }

    public XYDataset createin() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_indgm);
        graphcollection.addSeries(this.t_inerr);
        return graphcollection;
    }

    public XYDataset createout() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_outdgm);
        graphcollection.addSeries(this.t_outerr);
        return graphcollection;
    }

    public XYDataset createerr() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_chkerr);
        return graphcollection;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "ESARRAWIP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        CombinedDomainXYPlot plot = null;
        // tps
        XYDataset xydataset1 = this.createin();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        minichart1.setSeriesPaint(1, kSarConfig.color2);
        XYPlot subplot1 = new XYPlot(xydataset1, null, new NumberAxis("indgm/inerr /s"), minichart1);
        // rws
        XYDataset sck = this.createout();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color3);
        minichart2.setSeriesPaint(1, kSarConfig.color4);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot2 = new XYPlot(sck, null, new NumberAxis("outdgm/outerr /s"), minichart2);
        // nice 
        XYDataset ipfrag = this.createerr();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color5);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(ipfrag, null, new NumberAxis("chkerr"), minichart3);
        // the graph
        plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot3, 1);
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
            subplot3.setBackgroundPaint(null);
        }
        return mychart;
    }
    private TimeSeries t_indgm;
    private TimeSeries t_inerr;
    private TimeSeries t_outdgm;
    private TimeSeries t_outerr;
    private TimeSeries t_chkerr;
    private String ioOpt = new String("");
}
