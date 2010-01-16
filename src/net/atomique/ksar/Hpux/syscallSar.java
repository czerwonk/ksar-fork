/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.atomique.ksar.Hpux;

import java.awt.Color;
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
public class syscallSar extends AllGraph {
        public syscallSar(kSar hissar) {
                super(hissar);
                Title = new String("Syscalls");
                t_scall = new TimeSeries("Syscall/s", org.jfree.data.time.Second.class);
                t_sread = new TimeSeries("Read/s", org.jfree.data.time.Second.class);
                t_swrit = new TimeSeries("Write/s", org.jfree.data.time.Second.class);
                t_fork = new TimeSeries("Fork/s", org.jfree.data.time.Second.class);
                t_exec = new TimeSeries("Exec/s", org.jfree.data.time.Second.class);
                t_rchar = new TimeSeries("Rchar/s", org.jfree.data.time.Second.class);
                t_wchar = new TimeSeries("Wchar/s", org.jfree.data.time.Second.class);
        }

        public void add(Second now, Float val1Init, Float val2Init, Float val3Init, Float val4Init, Float val5Init, Float val6Init, Float val7Init) {
                this.t_scall.add(now, val1Init);
                this.t_sread.add(now, val2Init);
                this.t_swrit.add(now, val3Init);
                this.t_fork.add(now, val4Init);
                this.t_exec.add(now,val5Init);
                this.t_rchar.add(now,val6Init);
                this.t_wchar.add(now,val7Init);

        }

        public XYDataset createrw() {
                TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
                timeseriescollection.addSeries(this.t_sread);
                timeseriescollection.addSeries(this.t_swrit);
                return timeseriescollection;
        }

        public XYDataset createcall() {
                TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
                timeseriescollection.addSeries(this.t_scall);
                return timeseriescollection;
        }

        public XYDataset createfork() {
                TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
                timeseriescollection.addSeries(this.t_fork);
                timeseriescollection.addSeries(this.t_exec);
                return timeseriescollection;
        }

        public XYDataset createchar() {
                TimeSeriesCollection timeseriescollection = new TimeSeriesCollection();
                timeseriescollection.addSeries(this.t_rchar);
                timeseriescollection.addSeries(this.t_wchar);
                return timeseriescollection;
        }

        public void addtotree (DefaultMutableTreeNode myroot) {
                mynode = new DefaultMutableTreeNode(new GraphDescription(this, "HPUXSYSCALL", this.Title,null)); 
                mysar.add2tree(myroot,mynode);
        }

        public JFreeChart makegraph(Second g_start, Second g_end) {
                // rw
                XYDataset rwset = this.createrw();
                XYItemRenderer minichart1 = new StandardXYItemRenderer();
                minichart1.setSeriesPaint(0, kSarConfig.color1);
                minichart1.setBaseStroke(kSarConfig.DEFAULT_STROKE); 
                minichart1.setSeriesPaint(1, kSarConfig.color2); 
                XYPlot subplot1 = new XYPlot(rwset, null, new NumberAxis("read/write /s"),minichart1);
                // call
                XYDataset callset = this.createcall();
                XYItemRenderer minichart2 = new StandardXYItemRenderer();
                minichart2.setSeriesPaint(0, kSarConfig.color3);
                minichart2.setBaseStroke(kSarConfig.DEFAULT_STROKE); 
                XYPlot subplot2 = new XYPlot(callset, null, new NumberAxis("syscall/s"), minichart2);
                // fork
                XYDataset forkset = this.createfork();
                XYItemRenderer minichart3 = new StandardXYItemRenderer();
                minichart3.setSeriesPaint(0, kSarConfig.color4);
                minichart3.setSeriesPaint(1, kSarConfig.color5);
                minichart3.setBaseStroke(kSarConfig.DEFAULT_STROKE); 
                XYPlot subplot3 = new XYPlot(forkset, null, new NumberAxis("fork/exec /s"), minichart3);
                // char
                XYDataset charset = this.createchar();
                XYItemRenderer minichart4 = new StandardXYItemRenderer();
                minichart4.setSeriesPaint(0, kSarConfig.color6);
                minichart4.setSeriesPaint(1, kSarConfig.color7);
                minichart4.setBaseStroke(kSarConfig.DEFAULT_STROKE); 
                XYPlot subplot4 = new XYPlot(charset, null, new NumberAxis("rchar/wchar /s"), minichart4);

                // PANEL
                CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis(""));
                plot.add(subplot1, 1); 
                plot.add(subplot2, 1); 
                plot.add(subplot3, 1); 
                plot.add(subplot4, 1); 
                plot.setOrientation(PlotOrientation.VERTICAL);
                JFreeChart mychart = new JFreeChart(this.getGraphTitle(), kSarConfig.DEFAULT_FONT,plot,true);
                if ( setbackgroundimage(mychart) == 1 ) {
                        subplot1.setBackgroundPaint(null);
                        subplot2.setBackgroundPaint(null);
                        subplot3.setBackgroundPaint(null);
                        subplot4.setBackgroundPaint(null);
                }
                if ( g_start != null ) {
                        DateAxis dateaxis1 = (DateAxis)mychart.getXYPlot().getDomainAxis();
                        dateaxis1.setRange(g_start.getStart(),g_end.getEnd());
                }

                return mychart;

        }

        private TimeSeries t_scall;
        private TimeSeries t_sread;
        private TimeSeries t_swrit;
        private TimeSeries t_fork;
        private TimeSeries t_exec;
        private TimeSeries t_rchar;
        private TimeSeries t_wchar;

}
