package implove;

import java.util.Arrays;

import javax.swing.JFrame;

import org.jfree.chart.plot.PlotOrientation;

public class CheckRobust {

	public static void main(String[] args) {
		//x = [n,W0,W,cancelLNG,cancelTime,restLNG,stormRest]船の数、FLNGの貯蔵量、船の貯蔵量、船の速度
		double[][] prams = new double[5][7];
		prams[0] = new double[]{1.1,250000,200000,1.8,0,0.3,1};
		prams[1] = new double[]{4.1,200000,180000,1.5,5,0.1,0};
		prams[2] = new double[]{7.1,150000,120000,1.1,7,0,2,2};
		prams[3] = new double[]{9.1,200000,80000,1.2,13,0.5,5};
		prams[4] = new double[]{12.1,150000,70000,1.5,10,0.4,8};
		
		int n = 10;//繰り返し数
		
		int count = 20;//試行回数
		double[] values = new double[count*prams.length];
		String[] series = new String[count*prams.length];
		String[] categories = new String[count*prams.length];
		
		int num = 0;
		for(int i=0; i<count; i++){
			for(int j=0; j<prams.length; j++){
				//j番目のパラメータ
				categories[num] = (i+1)+"";
				series[num] = Arrays.toString(prams[j]);
				LngSimulater lngSimulater = new LngSimulater(n);
				values[num] = lngSimulater.simulate(prams[j]);
				num++;
			}
		}
		
		LineGraph frame = new LineGraph(n+"回平均の値の推移", "n番目の試行", "総利益", PlotOrientation.VERTICAL,true, false, false, values, series, categories);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setBounds(10, 10, 500, 500);
	    frame.setTitle("n回平均の値の推移");
	    frame.setVisible(true);
	}

}
