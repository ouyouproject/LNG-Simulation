package javaFiles;

public class simulation {
	//パラメータ
	private static int N = 1;
	private static double W0 = 1000000;
	private static double W = 200000;
	private static double V = 30;
	
	private static int time = 8;//8時から
	private static int day = 1;
	
	private static final int finish_year = 3;//三年で終了
	
	private static FLNG flng = new FLNG(W0);
	private static FSRU fsru = new FSRU();
	private static LNG_ship[] shipArray = new LNG_ship[N];
	private static Wave wave = new Wave();
	
	public static void main(String[] args){
		//船インスタンスを生成
		for(int i=0; i<N; i++){
			shipArray[i] = new LNG_ship(W, V);
		}
		
		//時刻ごとに実行
		while(day<=365*finish_year){
			System.out.println("========================");
			if(!wave.enableToLoad(time)){
				System.out.println("高波");
			}
			System.out.println(day+"日目"+time+"時");
			System.out.println("FSRU :" + fsru.getAmount());
			//FLNGで汲み上げ
			flng.getLNG();
			
			//各船が行動
			for(int i=0; i<N; i++){
				LNG_ship ship = shipArray[i];
				ship.action(flng,fsru,wave,time);
			}
			tick();
			
		}
		
		//結果を表示
		System.out.println(fsru.calcProfit());
		
	}
	
	//時刻を進める
	public static void tick(){
		time++;
		if(time>=24){
			time = 0;
			day++;
			if(day==30){
				System.exit(1);
			}
		}
	}
}
