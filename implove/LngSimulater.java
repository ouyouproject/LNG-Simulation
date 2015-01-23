package implove;



public class LngSimulater implements Simulater{
	//パラメータ
	private int N;
	private double W0;
	private double W;
	private double V;
	
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
			this.shipArray[i] = new LNG_ship(this.W, this.V, i,LNG_ship.Status.sailing);
			if(i>0){
				this.shipArray[i].setPrevShip(this.shipArray[i-1]);
			}
		}
		this.shipArray[0].setPrevShip(this.shipArray[this.N-1]);
		this.fsru.setPrevShipId(this.N-1);
		this.flng.setPrevShipId(this.N-1);
		//時刻ごとに実行
		while(this.day<=365*this.finish_year){
			this.flng.updateVacant();
			this.fsru.updateVacant();
			this.wave.updateWave(this.time);
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
		for(LNG_ship ship : shipArray){
			fsru.addTransportCost(ship.getTransportCost());
			fsru.addAmount(ship.getAmount());
		}
		//結果を返す
		return fsru.calcProfit();
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
