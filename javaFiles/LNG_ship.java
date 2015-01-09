package javaFiles;

import java.util.concurrent.SynchronousQueue;


public class LNG_ship {
	private double amount = 0;
	private int watingTime_sum = 0;//待ち時間の合計
	private double W;
	private double V;
	private final double L = 2000;//FLNGの座標
	private double position = 0;//FSRUからの座標
	public static enum Status{flng,sailing,fsru;}
	private Status status ;//どこにいるのか
	private Status destination = Status.flng;//目的地
	private double transportCost;//輸送費（一往復の）
	private boolean startLoading = false;//係船の開始	
	private boolean finishLoading = false;//係船の終了
	private int loadingTime = 0;//係船開始からの時間
	
	public LNG_ship(double input_W, double input_V) {
		this.W = input_W;
		this.V = input_V;
		this.status = Status.sailing;
	}
	
	public void resetAmount() {
		this.amount = 0;
	}
	public void addAmount(double input) {
		this.amount += input;
	}
	public void subAmount(double input) {
		this.amount -= input;
	}
	public double getAmount() {
		return this.amount;
	}
	public double getW() {
		return this.W;
	}
	public void addLoadingTime(){
		this.loadingTime++;
	}
	public void resetLoadingTime() {
		this.loadingTime = 0;
	}
	public int getLoadingTime(){
		return this.loadingTime;
	}
	
	public void setStatus(Status s) {
		this.status = s;
	}
	public void setFinishLoading() {
		this.finishLoading = true;
	}
	public int getWaitingTime() {
		return this.watingTime_sum;
	}
	
	//simulation.javaから呼ばれる
	public void action(FLNG flng,FSRU fsru,Wave wave,int time){
		//海上で積み込み
		if(this.status.equals(Status.flng)){
			this.actionFlng(flng,wave,time);
		}
		//移動中
		else if(this.status.equals(Status.sailing)){
			this.actionSailing(time);
		}
		//地上に積み出し
		else{
			this.actionFsru(fsru,time);
		}
	}
	
	//海上での行動
	private void actionFlng(FLNG flng,Wave wave,int time){
		System.out.println("FLNG(海上)");
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<17){
				//波高が低い時
				if(wave.enableToLoad(time)){
					if(flng.getVacant()){
						//係船を開始
						System.out.println("係船開始");
						this.startLoading = true;
						flng.load(this);
					}
					else{
						//待ち時間を追加
						System.out.println("順番待ち");
						this.watingTime_sum++;
					}
				}
				//波高が高い時
				else{
					System.out.println("波高により係船不可");
					//待ち時間を追加
					this.watingTime_sum++;
				}
			}
			else{
				//17時以降にどうするか
			}
		}
		//係船を終えた場合
		else if(this.finishLoading){
			System.out.println("係船終了");
			//波高が低い時
			if(wave.enableToLoad(time)){
				//出発
				System.out.println("出発");
				this.finishLoading = false;
				this.startLoading = false;
				this.destination = Status.fsru;
				this.status = Status.sailing;
				this.actionSailing(time);
			}
			//波高が高い時
			else{
				System.out.println("波高により出発不可");
				this.watingTime_sum++;
			}
		}
		//係船中
		else{
			System.out.println("係船中");
			flng.load(this);
		}
	}
	
	//航海中の行動
	private void actionSailing(int time){
		System.out.println("移動中");
		System.out.print("position :"+this.position + "→ ");
		//FLNGに向かうとき
		if(this.destination.equals(Status.flng)){
			this.position += this.V;
			System.out.println(this.position);
			//到着した時
			if(this.position>this.L){
				this.position = this.L;
				System.out.println("FLNG到着");
				this.status = this.destination;
				//輸送費を計算
				
			}
		}
		//FSRUに向かうとき
		else if(this.destination.equals(Status.fsru)){
			this.position -= V;
			System.out.println(this.position);
			//到着した時
			if(this.position<0){
				this.position = 0;
				System.out.println("FSRU到着");
				this.status = this.destination;
				//輸送費を計算
				
			}
		}
		else{
			System.out.println("invalid value in destination");
		}
	}
	
	//港での行動
	private void actionFsru(FSRU fsru,int time){
		System.out.println("FSRU(港)");
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<17){
				if(fsru.getVacant()){
					//係船を開始
					System.out.println("係船開始");
					this.startLoading = true;
					fsru.load(this);
				}
				else{
					//待ち時間を追加
					System.out.println("順番待ち");
					this.watingTime_sum++;
				}
			}
			else{
				//17時以降にどうするか
			}
		}
		//係船を終えた場合
		else if(this.finishLoading){
			//出発
			System.out.println("係船終了");
			System.out.println("出発");
			this.startLoading = false;
			this.finishLoading = false;
			this.destination = Status.flng;
			this.status = Status.sailing;
			this.actionSailing(time);
		}
		//係船中
		else{
			System.out.println("係船中");
			fsru.load(this);
		}
	}
	
}
