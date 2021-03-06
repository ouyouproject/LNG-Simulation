package javaFiles;



public class LNG_ship {
	private int id;
	private double amount = 0;
	private int watingTime_sum = 0;//待ち時間の合計
	private double W;
	private double V;
	private final double L = 2000;//FLNGの座標
	private double position = 0;//FSRUからの座標
	public static enum Status{flng,sailing,fsru,wait;}
	private Status status;//どこにいるのか
	private Status destination = Status.flng;//目的地
	private double transportCost;//輸送費（一往復の）
	private boolean startLoading = false;//係船の開始	
	private boolean finishLoading = false;//係船の終了
	private int loadingTime = 0;//係船開始からの時間
	
	public LNG_ship(double input_W, double input_V, int input_id, Status input_statsu) {
		this.id = input_id;
		this.W = input_W;
		this.V = input_V;
		this.status = input_statsu;
	}
	public int getId() {
		return this.id;
	}
	
	public void resetAmount() {
		this.amount = 0;
	}
	public void addAmount(double input) {
		this.amount += input;
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
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<17){
				//波高が低い時
				if(wave.enableToLoad()){
					if(flng.getVacant()){
						//係船を開始
						this.startLoading = true;
						flng.setVacant(false);
						flng.load(this);
					}
					else{
						//待ち時間を追加
						this.watingTime_sum++;
					}
				}
				//波高が高い時
				else{
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
			if(time>=8&&time<17){
				//波高が低い時
				if(wave.enableToLoad()){
					//出発
					this.finishLoading = false;
					this.startLoading = false;
					this.destination = Status.fsru;
					this.status = Status.sailing;
					this.actionSailing(time);
				}
				//波高が高い時
				else{
					this.watingTime_sum++;
				}
			}
			else{
				//17時以降にどうするか
			}
		}
		//係船中
		else{
			flng.load(this);
		}
	}
	
	//航海中の行動
	private void actionSailing(int time){
		//FLNGに向かうとき
		if(this.destination.equals(Status.flng)){
			this.position += this.V;
			//到着した時
			if(this.position>this.L){
				this.position = this.L;
				this.status = this.destination;
				//輸送費を計算
				
			}
		}
		//FSRUに向かうとき
		else if(this.destination.equals(Status.fsru)){
			this.position -= V;
			//到着した時
			if(this.position<0){
				this.position = 0;
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
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<17){
				if(fsru.getVacant()){
					//係船を開始
					this.startLoading = true;
					fsru.setVacant(false);
					fsru.load(this);
				}
				else{
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
			if(time>=8&&time<17){
				//出発
				this.startLoading = false;
				this.finishLoading = false;
				this.destination = Status.flng;
				this.status = Status.sailing;
				this.actionSailing(time);
			}
			else{
				//17時以降にどうするか
			}
		}
		//係船中
		else{
			fsru.load(this);
		}
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("id:\t").append(this.id)
			.append("\nLNG:\t").append(this.amount)
			.append("\n状態:\t");
		switch (this.status) {
		case flng:
			result.append("海上").append("\tloadingTime:\t").append(this.loadingTime);
			break;
		case sailing:
			result.append("移動中 to ").append(this.destination).append("\tposition:\t").append(this.position);
			break;
		case fsru:
			result.append("港").append("\tloadingTime:\t").append(this.loadingTime);
			break;
			
		default:
			break;
		}
		return result.toString();
	}
	public String toCsv() {
		return this.position+","+this.amount+","+this.loadingTime;
	}
}
