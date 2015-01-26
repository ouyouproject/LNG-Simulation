package implove;

import implove.LNG_ship.Status;



public class LngSimulater implements Simulater{
	//パラメータ
	private int N;
	private double W0;
	private double W;
	private double V;
	private double CancelLNG;//理想値の何倍を基準に途中発射をするか
	private int CancelTime;//何時間以上無駄があれば途中出発するか。最大15時間
	private double RestLNG;//理想値の何倍を基準にFLNGを休ませるか
	private int StormRest;//波の荒れているときに何期遅らせて到着させるか
	
	
	private final int n = 10; //モンテカルロの繰り返し回数
	
	private int time;//8時から
	private int day;
	
	public static final int finish_year = 3;//三年で終了
	
	private FLNG flng;
	private FSRU fsru;
	private LNG_ship[] shipArray;
	private Wave wave;
	
	
	//n回シミュレーションした結果を返す
	@Override
	public double simulate(double[] x){
		//初期化
		this.time = 8;
		this.day = 1;
		this.N = (int) Math.ceil(x[0]);
		if(this.N ==0){
			this.N = 1;
		}
		this.W0 = x[1];
		this.W = x[2];
		this.CancelLNG = x[3];
		this.CancelTime = (int) Math.floor(x[4]);
		this.RestLNG = x[5];
		this.StormRest = (int) Math.floor(x[6]);
		
		this.flng = new FLNG(this.W0);
		this.fsru = new FSRU(this.W0, this.W, this.N);
		this.shipArray = new LNG_ship[this.N];
		this.wave = new Wave();
		
		//実行
		double result = 0;
		for(int i=0; i<this.n; i++){
			result += this.oneSimulate();
		}
		return result/this.n;
	}
	
	private double oneSimulate(){
		//船インスタンスを生成
		for(int i=0; i<this.N; i++){
			this.shipArray[i] = new LNG_ship(this.W, this.V, i,LNG_ship.Status.sailing,this.CancelLNG,this.CancelTime,this.RestLNG,this.StormRest);
			if(i>0){
				this.shipArray[i].setPrevShip(this.shipArray[i-1]);
				this.shipArray[i-1].setPostShip(this.shipArray[i]);
			}
		}
		this.shipArray[0].setPrevShip(this.shipArray[N-1]);
		this.shipArray[N-1].setPostShip(this.shipArray[0]);
		this.fsru.setPrevShipId(N-1);
		this.flng.setPrevShipId(N-1);
		
		
		//時刻ごとに実行
		while(this.day<=365*LngSimulater.finish_year){
			this.flng.updateVacant();
			this.fsru.updateVacant();
			this.wave.updateWave(this.time);
			this.flng.updateIdealAmount(this.day, this.time, this.shipArray[0]);
			
			//目標時間を更新
			int flngId = this.flng.getPrevShipId();
			LNG_ship flng_ship = this.shipArray[flngId]; 
			LNG_ship nextShip;
			//flngにprevShipがないとき
			if(flng_ship.getStatus()!=Status.flng){
				nextShip = flng_ship.getPostShip();
				if(nextShip.getStatus()!=Status.flng){
					nextShip.calcLeavingTimeForVacant(this.wave, this.time, this.flng);
				}
				else{
					nextShip.calcLeavingTime(this.wave, this.time, this.flng);
					flng_ship = nextShip;
				}
				
			}
			//flngにprevShipがあるとき
			else{
				flng_ship.calcLeavingTime(this.wave, this.time, this.flng);
			}
			for(int i=1; i<this.N; i++){
				nextShip = flng_ship.getPostShip();
				nextShip.calcLeavingTime(this.wave, this.time, this.flng);
				flng_ship = nextShip;
			}
			
			
			//FLNGで汲み上げ
			this.flng.getLNG();
			//各船が行動
			for(int i=0; i<this.N; i++){
				LNG_ship ship = this.shipArray[i];
				ship.action(this.flng,this.fsru,this.wave,this.time);
			}
			tick();
		}
		
		//船の輸送費とLNGを回収
		for(LNG_ship ship : this.shipArray){
			this.fsru.addTransportCost(ship.getTransportCost());
			this.fsru.addAmount(ship.getAmount());
		}
		//結果を返す
		return this.fsru.calcProfit();
	}
		
		//時刻を進める
	private void tick(){
		this.time++;
		if(this.time>=24){
			this.time = 0;
			this.day++;
		}
	}

}
