package implove;

import implove.LNG_ship.Status;

import java.awt.Frame;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.plot.PlotOrientation;

public class simulation {
	//パラメータ
	private static int N = 10;
	private static double W0 = 200000;
	private static double W = 70000;
	private static double V;
	private static double CancelLNG = 1.3;//理想値の何倍を基準に途中発射をするか
	private static int CancelTime = 5;//何時間以上無駄があれば途中出発するか。最大15時間
	private static double RestLNG = 0.5;//理想値の何倍を基準にFLNGを休ませるか
	private static int StormRest = 1;//波の荒れているときに何期遅らせて到着させるか
	
	
	private static int time = 8;//8時から
	public static int day = 1;
	
	public static final int finish_year = 3;//三年で終了
	
	private static int waitingTimes = 0;//船の累計待ち時間
	private static double idealFLNG = 0;

	private static List<Double> FLNG_values = new ArrayList<Double>();
	private static List<String> FLNG_series = new ArrayList<String>();
	private static List<Double> waiting_values = new ArrayList<Double>();
	private static List<String> waiting_series = new ArrayList<String>();
	private static List<String> times = new ArrayList<String>();
	
	
	
	private static FLNG flng = new FLNG(W0);
	private static FSRU fsru = new FSRU(W,W0,N);
	private static LNG_ship[] shipArray = new LNG_ship[N];
	private static Wave wave = new Wave();
	
	public static void main(String[] args){
		try{
			
			String filePath = "/Users/yuki/Documents/LNG_Simulation/jsFiles/data3.csv";
			FileWriter fw = new FileWriter(filePath,false);//上書き
			PrintWriter pw = new PrintWriter(fw);
			//基本的なデータを書き込み
			StringBuilder prams = new StringBuilder();
			prams.append("W0,").append(W0).append(",W,").append(W).append(",N,").append(N).append(",FLNG position,").append(LNG_ship.L).append(",FSRU position,").append(0);
			pw.println(prams.toString());
			//ヘッダを入力
			pw.print(" , ,wave,FSRU,,,FLNG,,,,Ships");
			//船インスタンスを生成
			for(int i=0; i<N; i++){
				shipArray[i] = new LNG_ship(W, V, i,LNG_ship.Status.sailing);
				if(i>0){
					shipArray[i].setPrevShip(shipArray[i-1]);
					shipArray[i-1].setPostShip(shipArray[i]);
				}
				pw.print(",ship"+i+",,,,,,");
			}
			shipArray[0].setPrevShip(shipArray[N-1]);
			shipArray[N-1].setPostShip(shipArray[0]);
			fsru.setPrevShipId(N-1);
			flng.setPrevShipId(N-1);
			pw.println();
			pw.print("day,time,enableLoad,amount,loading,prevShip,amount,ideal_amount,loading,prevShip,waitingTime");
			for(int i=0; i<N; i++){
				pw.print(",positon,amount,loadingTime,status,V,nextGoalTime, ");
			}
			pw.println();
			
			
			
			//時刻ごとに実行
			while(day<=365*finish_year){
				flng.updateVacant();
				flng.updateIdealAmount(day, time);
				fsru.updateVacant();
				wave.updateWave(time);
				
				//グラフ用のデータを格納
				FLNG_values.add(flng.getAmount());
				FLNG_series.add("FLNG");
				double sum = 0;
				for(LNG_ship ship: shipArray){
					sum += ship.getWaitingTime();
				}
				waiting_values.add(sum);
				waiting_series.add("全船の停止時間");
				times.add(Integer.toString(day)+"_"+Integer.toString(time));
				
				
				//目標時間を更新
				int flngId = flng.getPrevShipId();
				LNG_ship flng_ship = shipArray[flngId]; 
				LNG_ship nextShip;
				//flngにprevShipがないとき
				if(flng_ship.getStatus()!=Status.flng){
					nextShip = flng_ship.getPostShip();
					if(nextShip.getStatus()!=Status.flng){
						nextShip.calcLeavingTimeForVacant(wave, time);
					}
					else{
						nextShip.calcLeavingTime(wave, time);
						flng_ship = nextShip;
					}
					
				}
				//flngにprevShipがあるとき
				else{
					flng_ship.calcLeavingTime(wave, time);
				}
				for(int i=1; i<N; i++){
					nextShip = flng_ship.getPostShip();
					nextShip.calcLeavingTime(wave, time);
					flng_ship = nextShip;
				}
				
				//書き込み
				pwPrint(pw);
				//System.out.println("=====================================================");
				//System.out.println(day+"日目"+time+"時");
				//System.out.println(wave.toString());
				//FLNGで汲み上げ
				flng.getLNG();
				
				//System.out.println("-------------");
				//各船が行動
				for(int i=0; i<N; i++){
					LNG_ship ship = shipArray[i];
					ship.action(flng,fsru,wave,time);
					//System.out.println(ship.toString());
					//System.out.println("-------------");
				}
				//System.out.println(flng.toString());
				//System.out.println();
				//System.out.println(fsru.toString());
				
				//待ち時間を集計
				waitingTimes = 0;
				for(LNG_ship ship: shipArray){
					waitingTimes += ship.getWaitingTime();
				}
				
				tick(pw);
			}
			//船の輸送費とLNGを回収
			for(LNG_ship ship : shipArray){
				fsru.addTransportCost(ship.getTransportCost());
				fsru.addAmount(ship.getAmount());
			}
			pw.close();
			//結果を表示
			System.out.println(fsru.calcProfit());
			plotFLNGGraph();
			plotWaitingGraph();
		}
		catch(IOException e){
			System.out.println(e);
		}
	}
	
	//時刻を進める
	public static void tick(PrintWriter pw){
		time++;
		if(time>=24){
			time = 0;
			day++;
			//30日まで実験
			
			if(day>=50){
				pw.close();
				/*
				plotFLNGGraph();
				plotWaitingGraph();
				waitForThreeSeconds(10);
				System.exit(1);
				*/
			}
			
		}
	}
	//csv出力
	public static void pwPrint(PrintWriter pw){
		pw	.append(Integer.toString(day)).append(",")
			.append(Integer.toString(time)).append(",")
			.append(wave.toCsv()).append(",")
			.append(fsru.toCsv()).append(",")
			.append(flng.toCsv()).append(",")
			.append(Integer.toString(waitingTimes)).append(",");
		for(int i=0; i<N; i++){
			pw.append(shipArray[i].toCsv()).append(",");
		}
		pw.println();
	}
	
	private static void plotFLNGGraph(){
		double[] v = new double[FLNG_values.size()];
		for(int i=0; i<FLNG_values.size(); i++){
			v[i] = FLNG_values.get(i);
		}
		LineGraph frame = new LineGraph("FLNGの残量推移", "時間", "LNG(m3)", PlotOrientation.VERTICAL, false, false, false, v, FLNG_series.toArray(new String[FLNG_series.size()]), times.toArray(new String[times.size()]));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setBounds(10, 10, 500, 500);
	    frame.setTitle("FLNGの残量推移");
	    frame.setVisible(true);
		
	}
	private static void plotWaitingGraph(){
		double[] v = new double[waiting_values.size()];
		for(int i=0; i<waiting_values.size(); i++){
			v[i] = waiting_values.get(i);
		}
		LineGraph frame = new LineGraph("累計待ち時間推移", "時間", "時間(h)", PlotOrientation.VERTICAL, false, false, false, v, waiting_series.toArray(new String[waiting_series.size()]), times.toArray(new String[times.size()]));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setBounds(10, 10, 500, 500);
	    frame.setTitle("全船の累計待ち時間");
	    frame.setVisible(true);
		
	}
	
	public static void waitForThreeSeconds(int t) {
	    try {
	        Thread.sleep(t * 1000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
}
