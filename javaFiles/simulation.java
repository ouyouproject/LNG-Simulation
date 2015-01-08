package javaFiles;

public class simulation {
	private static int N = 5;
	private static double W0 = 10000;
	private static double W = 10000;
	private static double V = 30;
	
	private static int time = 8;//8時から17時
	private static int day = 0;
	
	private static final int finish_year = 3;//三年で終了
	
	private static FLNG flng = new FLNG(W0);
	private static FSRU fsru = new FSRU();
	private static LNG_ship[] shipArray = new LNG_ship[N];
	private static Wave wave = new Wave();
	
	
	public static void main(){
		//船インスタンスを生成
		for(int i=0; i<N; i++){
			shipArray[i] = new LNG_ship(W, V);
		}
		
		//時刻ごとに実行
		while(day<365*finish_year){
			//FLNGで汲み上げ
			flng.getLNG();
			//各船が行動
			for(int i=0; i<N; i++){
				LNG_ship ship = shipArray[i];
				ship.action(flng,fsru,wave);
			}
			tick(shipArray);
		}
		
		//結果を表示
		System.out.println(fsru.calcProfit());
		
	}
	
	//時刻を進める
	public static void tick(LNG_ship[] shipArray){
		if(time>=17){
			time = 8;
			day++;
			//各船に処理を行う
			
			
		}
		else{
			time++;
		}
	}
}
