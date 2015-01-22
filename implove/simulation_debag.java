package implove;

import implove.LNG_ship.Status;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class simulation_debag {
	//パラメータ
	private static int N = 10;
	private static double W0 = 200000;
	private static double W = 70000;
	private static double V;
	
	private static int time = 8;//8時から
	public static int day = 1;
	
	private static final int finish_year = 3;//三年で終了
	
	private static FLNG flng = new FLNG(W0);
	private static FSRU fsru = new FSRU(W,W0,N);
	private static LNG_ship[] shipArray = new LNG_ship[N];
	private static Wave wave = new Wave();
	
	public static void main(String[] args){
		try{
			String filePath = "/Users/yuki/Documents/LNG_Simulation/jsFiles/data2.csv";
			FileWriter fw = new FileWriter(filePath,false);//上書き
			PrintWriter pw = new PrintWriter(fw);
			//基本的なデータを書き込み
			StringBuilder prams = new StringBuilder();
			prams.append("W0,").append(W0).append(",W,").append(W).append(",N,").append(N).append(",FLNG position,").append(LNG_ship.L).append(",FSRU position,").append(0);
			pw.println(prams.toString());
			//ヘッダを入力
			pw.print(" , ,wave,FSRU,,FLNG,");
			//船インスタンスを生成
			for(int i=0; i<N; i++){
				shipArray[i] = new LNG_ship(W, V, i,LNG_ship.Status.sailing);
				if(i>0){
					shipArray[i].setPrevShip(shipArray[i-1]);
				}
				pw.print(",ship"+i+",,,,");
			}
			shipArray[0].setPrevShip(shipArray[N-1]);
			fsru.setPrevShipId(N-1);
			flng.setPrevShipId(N-1);
			pw.println();
			pw.print("day,time,enableLoad,amount,loading,amount,loading");
			for(int i=0; i<N; i++){
				pw.print(",positon,amount,loadingTime,V,nextGoalTime");
			}
			pw.println();
			
			//時刻ごとに実行
			while(day<=365*finish_year){
				flng.updateVacant();
				fsru.updateVacant();
				wave.updateWave(time);
				//目標時間を更新
				int flngId = flng.getPrevShipId();
				//flngに船がないとき
				if(shipArray[flngId].getStatus()!=Status.flng){
					int nextId;
					if(flngId==N-1){
						nextId = 0;
					}
					else{
						nextId = flngId+1;
					}
					if(shipArray[nextId].getStatus()!=Status.flng){
						shipArray[nextId].calcLeavingTimeForVacant(wave, time);
					}
					else{
						shipArray[nextId].calcLeavingTime(wave, time);
					}
				}
				//flngに船があるとき
				else{
					shipArray[flngId].calcLeavingTime(wave, time);
				}
				for(int i=1; i<N; i++){
					int shipId = flngId + i;
					if(shipId>=N){
						shipId -= N;
					}
					shipArray[shipId].calcLeavingTime(wave, time);
				}
				
				//書き込み
				pwPrint(pw);
				System.out.println("=====================================================");
				System.out.println(day+"日目"+time+"時");
				System.out.println(wave.toString());
				//FLNGで汲み上げ
				flng.getLNG();
				
				System.out.println("-------------");
				//各船が行動
				for(int i=0; i<N; i++){
					LNG_ship ship = shipArray[i];
					ship.action(flng,fsru,wave,time);
					System.out.println(ship.toString());
					System.out.println("-------------");
				}
				System.out.println(flng.toString());
				System.out.println();
				System.out.println(fsru.toString());
				
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
				System.exit(1);
			}
			
		}
	}
	//csv出力
	public static void pwPrint(PrintWriter pw){
		pw	.append(Integer.toString(day)).append(",")
			.append(Integer.toString(time)).append(",")
			.append(wave.toCsv()).append(",")
			.append(fsru.toCsv()).append(",")
			.append(flng.toCsv()).append(",");
		for(int i=0; i<N; i++){
			pw.append(shipArray[i].toCsv()).append(",");
		}
		pw.println();
	}
}
