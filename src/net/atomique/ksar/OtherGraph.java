/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.atomique.ksar;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BorderArrangement;
import org.jfree.chart.block.EmptyBlock;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.CompositeTitle;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author alex
 */
public class OtherGraph extends AllGraph {

    public OtherGraph(kSar hissar) {
        super(hissar);
        Title = new String("Specific Graph");
        initComponents();
        initCombo();
    }

    public void initComponents() {
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        JComboBox combo = (JComboBox) evt.getSource();
        String key = (String) combo.getSelectedItem();
        if ("None".equals(key)) {
            if (!"None".equals(oldseries1)) {
                xydataset1.removeSeries((TimeSeries) mysar.dispo.get(oldseries1));
            }
            return;
        }
        if (!"None".equals(oldseries1)) {
            xydataset1.removeSeries((TimeSeries) mysar.dispo.get(oldseries1));
        }
        xydataset1.addSeries((TimeSeries) mysar.dispo.get(key));
        oldseries1 = key;
    }

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {
        JComboBox combo = (JComboBox) evt.getSource();
        String key = (String) combo.getSelectedItem();
        if ("None".equals(key)) {
            if (!"None".equals(oldseries2)) {
                xydataset2.removeSeries((TimeSeries) mysar.dispo.get(oldseries2));
            }
            return;
        }
        if (!"None".equals(oldseries2)) {
            xydataset2.removeSeries((TimeSeries) mysar.dispo.get(oldseries2));
        }
        xydataset2.addSeries((TimeSeries) mysar.dispo.get(key));
        oldseries2 = key;
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        mysar.myUI.remove2tree(mynode);
        mysar.myUI.home2tree();
        mysar.pdfList.remove("xXx");
    }

    public void initCombo() {
        jComboBox1.addItem("None");
        jComboBox2.addItem("None");
        TreeMap<String,TimeSeries> tmphash = new TreeMap<String,TimeSeries>();
        tmphash.putAll(mysar.dispo);
        for (Iterator<String> it = tmphash.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            jComboBox1.addItem(key);
            jComboBox2.addItem(key);
        }
    }

    public JPanel run(Second g_start, Second g_end) {  
            mypanel = new JPanel();
            JPanel jPanel2 = new JPanel();
            JPanel Xgraphpanel = new ChartPanel(this.getgraph(g_start, g_end));
            mypanel.setLayout(new java.awt.BorderLayout());
            jPanel2.add(jComboBox1);
            jButton1.setText("Delete");
            jPanel2.add(jButton1);
            jPanel2.add(jComboBox2);
            mypanel.add(jPanel2, java.awt.BorderLayout.SOUTH);
            mypanel.add(Xgraphpanel, java.awt.BorderLayout.CENTER);
        
        return mypanel;
    }

    public void addtotree(DefaultMutableTreeNode myroot) {
        mynode = new DefaultMutableTreeNode(new GraphDescription(this, "XxX", this.Title, null));
        mysar.add2tree(myroot,mynode);
    }

    public JFreeChart makegraph(Second g_start, Second g_end) {
        JFreeChart mychart = ChartFactory.createTimeSeriesChart(this.getGraphTitle(), "", "", xydataset1, false, true, false);
        setbackgroundimage(mychart);
        XYPlot xyplot = (XYPlot) mychart.getPlot();

        //if (xydataset2.getSeriesCount() > 0) {
            NumberAxis numberaxis = new NumberAxis("");
            numberaxis.setAutoRangeIncludesZero(false);
            xyplot.setRangeAxis(1, numberaxis);
            xyplot.setDataset(1, xydataset2);
            xyplot.mapDatasetToRangeAxis(1, 1);
        //}

        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setSeriesPaint(0, kSarConfig.color3);
        xyitemrenderer.setSeriesPaint(1, kSarConfig.color4);
        if (xyitemrenderer instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer xylineandshaperenderer = (XYLineAndShapeRenderer) xyitemrenderer;
            xylineandshaperenderer.setBaseShapesVisible(false);
        }
        XYLineAndShapeRenderer xylineandshaperenderer1 = new XYLineAndShapeRenderer();
        xylineandshaperenderer1.setSeriesPaint(0, kSarConfig.color1);
        xylineandshaperenderer1.setSeriesPaint(1, kSarConfig.color2);
        xylineandshaperenderer1.setBaseShapesVisible(false);

        LegendTitle legendtitle = new LegendTitle(xyitemrenderer);
        BlockContainer blockcontainer = new BlockContainer(new BorderArrangement());
        blockcontainer.add(legendtitle, RectangleEdge.LEFT);

        //if (xydataset2.getSeriesCount() > 0) {
            xyplot.setRenderer(1, xylineandshaperenderer1);
            LegendTitle legendtitle1 = new LegendTitle(xylineandshaperenderer1);
            blockcontainer.add(legendtitle1, RectangleEdge.RIGHT);
        //}
        blockcontainer.add(new EmptyBlock(2000D, 0.0D));
        CompositeTitle compositetitle = new CompositeTitle(blockcontainer);
        compositetitle.setPosition(RectangleEdge.BOTTOM);
        mychart.addSubtitle(compositetitle);

        if (g_start != null) {
            DateAxis dateaxis1 = (DateAxis) mychart.getXYPlot().getDomainAxis();
            dateaxis1.setRange(g_start.getStart(), g_end.getEnd());
        }
        return mychart;
    }
    TimeSeriesCollection xydataset1 = new TimeSeriesCollection();
    TimeSeriesCollection xydataset2 = new TimeSeriesCollection();
    JComboBox jComboBox1 = new JComboBox();
    JComboBox jComboBox2 = new JComboBox();
    JButton jButton1 = new JButton();
    String oldseries1 = new String("None");
    String oldseries2 = new String("None");
    JPanel mypanel = null;
}
