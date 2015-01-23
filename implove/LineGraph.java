package implove;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineGraph extends JFrame{
	/*
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setBounds(10, 10, 500, 500);
	    this.setTitle("グラフサンプル");
	    this.setVisible(true);
	    のようにして使うことを想定
	 */
	public LineGraph(String title,String x_label, String y_label, 
					PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls,
					double[] values, String[] series, String[] category){
		JFreeChart chart =  ChartFactory.createLineChart(	title,
											x_label,
											y_label,
											createData(values,series,category),
											orientation,
											legend,
											tooltips,
											urls);//legendは汎例
		
		CategoryPlot plot = chart.getCategoryPlot();
	    plot.setBackgroundPaint(Color.YELLOW);

	    ChartPanel cpanel = new ChartPanel(chart);
	    getContentPane().add(cpanel, BorderLayout.CENTER);
	}
	private DefaultCategoryDataset createData(double[] values, String[] series, String[] category){
		DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
		for(int i=0; i<values.length; i++){
			defaultCategoryDataset.addValue(values[i], series[i], category[i]);
		}
		return defaultCategoryDataset;
	}
}
