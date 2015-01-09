package javaFiles;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class simulation {
	//パラメータ
	private static int N = 2;
	private static double W0 = 10000;
	private static double W = 10000;
	private static double V = 30;
	
	private static int time = 8;//8時から
	private static int day = 1;
	
	private static final int finish_year = 3;//三年で終了
	
	private static FLNG flng = new FLNG(W0);
	private static FSRU fsru = new FSRU();
	private static LNG_ship[] shipArray = new LNG_ship[N];
	private static Wave wave = new Wave();
	public static void main(String[] args){
		try{
			String filePath = "data.csv";
			FileWriter fw = new FileWriter(filePath,false);//上書き
			PrintWriter pw = new PrintWriter(fw);
			//ヘッダを入力
			pw.print(" , ,wave,FSRU,,FLNG,");
			//船インスタンスを生成
			for(int i=0; i<N; i++){
				shipArray[i] = new LNG_ship(W, V, i,LNG_ship.Status.sailing);
				pw.print(",ship"+i+",,");
			}
			pw.println();
			pw.print("day,time,enableLoad,amount,loading,amount,loading");
			for(int i=0; i<N; i++){
				pw.print(",positon,amount,loadingTime");
			}
			pw.println();
			
			//時刻ごとに実行
			while(day<=365*finish_year){
				flng.updateVacant();
				fsru.updateVacant();
				wave.updateWave(time);
				pwPrint(pw);
				System.out.println("=====================================================");
				System.out.println(day+"日目"+time+"時");
				System.out.println(wave.toString());
				//FLNGで汲み上げ
				flng.getLNG();
				//各船が行動
				System.out.println("-------------");
				for(int i=0; i<N; i++){
					LNG_ship ship = shipArray[i];
					ship.action(flng,fsru,wave,time);
					System.out.println(ship.toString());
					System.out.println("-------------");
				}
				System.out.println(flng.toString());
				System.out.println();
				System.out.println(fsru.toString());
				tick();
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
	public static void tick(){
		time++;
		if(time>=24){
			time = 0;
			day++;
			//30日まで実験
			if(day>=30){
				System.exit(1);
			}
		}
	}
	//csv出力
	public static void pwPrint(PrintWriter pw) {
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
