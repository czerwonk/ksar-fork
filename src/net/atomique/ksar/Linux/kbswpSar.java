/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar.Linux;

import java.text.NumberFormat;
import javax.swing.tree.DefaultMutableTreeNode;
import net.atomique.ksar.AllGraph;
import net.atomique.ksar.GraphDescription;
import net.atomique.ksar.IEEE1541Number;
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
public class kbswpSar extends AllGraph {

    public kbswpSar(kSar hissar) {
        super(hissar);
        Title = new String("Swap usage");
        t_free = new TimeSeries("kbswpfree", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap free", t_free);
        t_used = new TimeSeries("kbswpused", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap used", t_used);
        t_perc = new TimeSeries("%swpused", org.jfree.data.time.Second.class);
        mysar.dispo.put("% Swap used", t_perc);
        t_adc = new TimeSeries("kbswpcad", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap cad", t_adc);
        t_swpcad = new TimeSeries("%swpcad", org.jfree.data.time.Second.class);
        mysar.dispo.put("Swap %swpcad", t_swpcad);
    }

    public void add(Second now, Float val1, Float val2, Float val3) {
        this.t_free.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_used.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_perc.add(now, val3, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4) {
        this.t_free.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_used.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_perc.add(now, val3, do_notify());
        this.t_adc.add(now, val4.doubleValue() * 1024, do_notify());
        number_of_sample++;
    }

    public void add(Second now, Float val1, Float val2, Float val3, Float val4,Float val5) {
        this.t_free.add(now, val1.doubleValue() * 1024, do_notify());
        this.t_used.add(now, val2.doubleValue() * 1024, do_notify());
        this.t_perc.add(now, val3, do_notify());
        this.t_adc.add(now, val4.doubleValue() * 1024, do_notify());
        this.t_swpcad.add(now, val5, do_notify());
        number_of_sample++;
    }
    
    public XYDataset createfree() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_free);
        return graphcollection;
    }

    public XYDataset createused() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_used);
        return graphcollection;
    }

    public XYDataset createperc() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_perc);
        return graphcollection;
    }

    public XYDataset createadc() {
        TimeSeriesCollection graphcollection = new TimeSeriesCollection();
        graphcollection.addSeries(this.t_adc);
        return graphcollection;
    }

    public void setswpOpt(String s) {
        this.swpOpt = s;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "LINUXKBSWP", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        // used
        XYDataset freeset = this.createfree();
        XYItemRenderer minichart1 = new StandardXYItemRenderer();
        minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        minichart1.setSeriesPaint(0, kSarConfig.color1);
        NumberAxis numberaxis1 = new NumberAxis("swpfree");
        NumberFormat decimalformat1 = new IEEE1541Number(1);
        numberaxis1.setNumberFormatOverride(decimalformat1);
        XYPlot subplot1 = new XYPlot(freeset, null, numberaxis1, minichart1);
        // idle
        XYDataset usedset = this.createused();
        XYItemRenderer minichart2 = new StandardXYItemRenderer();
        minichart2.setSeriesPaint(0, kSarConfig.color2);
        minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        NumberAxis numberaxis2 = new NumberAxis("swpused");
        numberaxis2.setNumberFormatOverride(decimalformat1);
        XYPlot subplot2 = new XYPlot(usedset, null, numberaxis2, minichart2);
        //
        XYDataset percset = this.createperc();
        XYItemRenderer minichart3 = new StandardXYItemRenderer();
        minichart3.setSeriesPaint(0, kSarConfig.color3);
        minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE);
        XYPlot subplot3 = new XYPlot(percset, null, new NumberAxis("%swpused"), minichart3);
        //
        XYPlot subplot4 = null;
        if (!swpOpt.equals("kbmemshrd")) {
            XYDataset adcset = this.createadc();
            XYItemRenderer minichart4 = new StandardXYItemRenderer();
            minichart4.setSeriesPaint(0, kSarConfig.color4);
            minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE);
            NumberAxis numberaxis3 = new NumberAxis("swpcad");
            numberaxis3.setNumberFormatOverride(decimalformat1);
            subplot4 = new XYPlot(adcset, null, numberaxis3, minichart4);
        }
        // the graph
        CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.add(subplot3, 1);
        if (!swpOpt.equals("kbmemshrd")) {
            plot.add(subplot4, 1);
        }
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
            if (!swpOpt.equals("kbmemshrd")) {
                subplot4.setBackgroundPaint(null);
            }
        }
        return mychart;
    }
    private TimeSeries t_free;
    private TimeSeries t_used;
    private TimeSeries t_perc;
    private TimeSeries t_adc;
    private TimeSeries t_swpcad;
    private String swpOpt = new String("");
}
