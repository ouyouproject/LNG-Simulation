package implove;

import implove.FLNG.LngCondition;

public class LNG_ship {
	private int id;
	private double amount = 0;
	private int watingTime_sum = 0;//待ち時間の合計
	private double W;
	private double V;
	public static final double V_max = 40;
	public static final double L = 5000;//FLNGの座標
	private double position = 0;//FSRUからの座標
	public static enum Status{flng,sailing,fsru;}
	private Status status;//どこにいるのか
	private Status destination = Status.flng;//目的地
	private double transportCost=0;//輸送費（一往復の）
	private boolean startLoading = false;//係船の開始	
	private boolean finishLoading = false;//係船の終了
	private int loadingTime = 0;//係船開始からの時間
	private int finishTime = FLNG.T1;//係船を終える予定の時間
	private LNG_ship prevShip;
	private LNG_ship postShip;
	private double cancelLNG;//理想値の何倍を基準に途中発射をするか
	private int cancelTime;//何時間以上無駄があれば途中出発するか。最大15時間
	private double restLNG;//理想値の何倍を基準にFLNGを休ませるか
	private int stormRest;//波の荒れているとき何期遅らせるか
	
	
	private int leavingTime;//FLNG出発予定までの残り時間
	
	public LNG_ship(double input_W, double input_V, int input_id, Status input_statsu,
					double input_CancelLNG, int input_CancelTIme, double input_RestLNG, int input_StormRest) {
		this.id = input_id;
		this.W = input_W;
		this.V = input_V;
		this.status = input_statsu;
		this.cancelLNG = input_CancelLNG;
		this.cancelTime = input_CancelTIme;
		this.restLNG = input_RestLNG;
		this.stormRest = input_StormRest;
	}
	public void setPrevShip(LNG_ship input_ship){
		this.prevShip = input_ship;
	}
	public void setPostShip(LNG_ship input_ship){
		this.postShip = input_ship;
	}
	public LNG_ship getPostShip(){
		return this.postShip;
	}
	public int getId() {
		return this.id;
	}
	public Status getStatus(){
		return this.status;
	}
	public void setV(double input_V){
		this.V = input_V;
	}
	public void calcLeavingTime(Wave wave, int time, FLNG flng){
		//System.out.print("calcLeavingTime :"+this.id);
		int timeLeft;
		int hour;
		//FLNGの船
		if(this.status==Status.flng){
			//System.out.print(" FLNG");
			//作業中の船
			if(this.loadingTime < this.finishTime){
				//System.out.println(" loading");
				timeLeft = this.finishTime - this.loadingTime;//出発までの残り時間
				//時刻の影響を考える
				hour = (time + timeLeft) % 24;
				//夜間出発予定
				if(hour>17){
					timeLeft += 24 + 8 - hour;
				}
				else if(hour<8){
					timeLeft += 8 - hour;
				}
				else{
					//そのまま
				}
				
				//FLNGが溢れそうではなく、高波の場合はペースを落とす
				if(flng.getLngCondition()!=LngCondition.full&&!wave.enableToLoad()){
					timeLeft += wave.getWaveSpan()*this.stormRest;
					hour = (time+timeLeft)%24;
					//夜間出発予定
					if(hour>17){
						timeLeft += 24 + 8 - hour;
					}
					else if(hour<8){
						timeLeft += 8 - hour;
					}
					else if(hour%6!=1){
						timeLeft -= (hour%6-1);
					}
				}
				
				//波による影響を考える
				if(timeLeft<6){
					if(time/6 == (time+timeLeft-1)/6){
						if(wave.enableToLoad()){
							//そのまま
						}
						else{
							//高波であることがわかっている場合
							//17:00途中発射の場合は夜も積みこむ
							if(this.finishTime<FLNG.T1){
								this.finishTime = FLNG.T1;
								timeLeft += 8 + 24 -17;
							}
							else{
								timeLeft += 6 - (time+timeLeft)%6 +1;
								hour = (time + timeLeft) % 24;
								//夜間出発予定
								if(hour>17){
									timeLeft += 24 + 8 - hour;
								}
								else if(hour<8){
									timeLeft += 8 - hour;
								}
								else{
									//そのまま
								}
							}
						}
					}
				}
				this.leavingTime = timeLeft;
			}
			//待っている、出発直前の船
			else{
				//System.out.println("　waiting");
				if(wave.enableToLoad()){
					if(time>=8&&time<=17){
						timeLeft = 1;
					}
					else{
						timeLeft = this.leavingTime-1;
					}
				}
				else{
					timeLeft = this.leavingTime-1;
					if(timeLeft<6){
						if(time/6 == (time+timeLeft-1)/6){
							timeLeft += 6 - (time+timeLeft)%6 +1;
							hour = (time + timeLeft) % 24;
							//夜間出発予定
							if(hour>17){
								timeLeft += 24 + 8 - hour;
							}
							else if(hour<8){
								timeLeft += 8 - hour;
							}
							else{
								//そのまま
							}
						}
					}
				}
				this.leavingTime = timeLeft;
				/*
				//夜間で待っている船
				if(time<8){
					timeLeft = 8 - time;
					//波で出稿不可能な場合
					if(time>=6 && !wave.enableToLoad()){
						timeLeft = 13 - time;
					}
				}
				else if(time>17){
					timeLeft = 8 - time + 24;
				}
				//高波で待っている船
				else if(!wave.enableToLoad()){
					timeLeft = 6 - (time%6)+1;
					//夜間に出発予定の場合
					int hour = (time+timeLeft)%24;
					if(hour>17){
						timeLeft += 24 + 8 - hour;
					}
					else if(hour<8){
						timeLeft += 8 - hour;
					}
					else{
						//そのまま
					}
				}
				//次に出発できる船
				else{
					timeLeft = 1;
				}
				*/
			}
		}
		//航海中のもの
		else {
			//System.out.print(" sailing or FSRU");
			timeLeft = this.prevShip.leavingTime + FLNG.T1;
			
			//時刻の影響を考える
			hour = (time + timeLeft) % 24;
			//夜間出発予定
			if(hour>17){
				timeLeft += 24 + 8 - hour;
			}
			else if(hour<8){
				timeLeft += 8 - hour;
			}
			else{
				//そのまま
			}
			//波による影響を考える
			if(timeLeft<6){
				if(time/6 == (time+timeLeft-1)/6){
					//System.out.print(" wave");
					if(wave.enableToLoad()){
						//そのまま
						//System.out.println("◯");
					}
					else{
						//高波であることがわかっている場合
						timeLeft += 6 - (time+timeLeft)%6+1;
						hour = (time + timeLeft) % 24;
						//夜間出発予定
						if(hour>17){
							timeLeft += 24 + 8 - hour;
						}
						else if(hour<8){
							timeLeft += 8 - hour;
						}
						else{
							//そのまま
						}
						//System.out.println("☓");
					}
				}
				else{
					//System.out.print(" wave");
				}
			}
			else{
				//System.out.println();
			}
		}
		this.leavingTime = timeLeft;
	}
	//FLNGに船がなかった場合の末尾の船のLeavingTimeを計算(thisは先頭の船)
	public void calcLeavingTimeForVacant(Wave wave, int time, FLNG flng){
		//System.out.print("calcLeavingTimeForVacant :"+this.prevShip.getId());
		//最速で向かい、夜間、高波の時をさけるように速度を調整
		int timeLeft;
		if(this.status == Status.sailing){
			//System.out.print(" sailing");
			//flngに向かう
			if(this.destination == Status.flng){
				//System.out.print(" to FLNG");
				timeLeft = (int)Math.ceil((LNG_ship.L - this.position)/LNG_ship.V_max);
				int hour = (timeLeft + time)%24;
				//夜間着の場合
				if(hour>17){
					timeLeft += 24 + 8 - hour;
				}
				else if(hour<8){
					timeLeft += 8 - hour;
				}
				else{
					//そのまま
				}
				//波による影響を考える
				if(timeLeft<6){
					if(time/6 == (time+timeLeft-1)/6){
						//System.out.print(" wave");
						if(wave.enableToLoad()){
							//そのまま
							//System.out.println("◯");
						}
						else{
							//高波であることがわかっている場合
							timeLeft += 6 - (time+timeLeft)%6 +1;
							hour = (timeLeft + time)%24;
							//夜間着の場合
							if(hour>17){
								timeLeft += 24 + 8 - hour;
							}
							else if(hour<8){
								timeLeft += 8 - hour;
							}
							else{
								//そのまま
							}
							//System.out.println("☓");
						}
					}
					else{
						//System.out.println("");
					}
				}
				else{
					//System.out.println();
				}
				this.prevShip.setLeavingTime(timeLeft);
			}
			//fsruに向かう
			else{
				//System.out.println(" to FSRU");
				timeLeft = (int)Math.ceil(this.position/LNG_ship.V_max) + (int)Math.ceil(LNG_ship.L/LNG_ship.V_max) + FSRU.T2;
				this.prevShip.setLeavingTime(timeLeft);
			}
		}
		//FSRUにいる場合
		else if(this.status == Status.fsru){
			//System.out.println(" FSRU");
			timeLeft = FSRU.T2 - this.loadingTime + (int)Math.ceil(LNG_ship.L/V_max);
			this.prevShip.setLeavingTime(timeLeft);
		}
		else{
			System.out.println(simulation.day);
			System.out.println(time);
			System.out.println(this.id);
			System.out.println("error!!");
		}
	}
	
	public int getLeavingTime(){
		return this.leavingTime;
	}
	public void setLeavingTime(int input){
		this.leavingTime = input;
	}
	
	public void addTransportCost(double input_l){
		//燃料(kg/km)
		double FO = 6.87 / 100000 * (2.3693 * this.amount +1660)  * Math.pow((2.978* this.amount + 1660),-1/3)  * this.V*this.V;
		//燃料費（円/kg）
		double fuelCost = 49.0;
		//一時間あたりの輸送費
		this.transportCost += fuelCost * FO * input_l;
	}
	
	public double getTransportCost(){
		return this.transportCost;
	}
	public double getPosition(){
		return this.position;
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
	public void setAmount(double input){
		this.amount = input;
	}
	public void subAmount(double input){
		this.amount -= input;
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
	public int getFinishTime(){
		return this.finishTime;
	}
	
	//simulation.javaから呼ばれる
	public void action(FLNG flng,FSRU fsru,Wave wave,int time){
		//海上で積み込み
		if(this.status.equals(Status.flng)){
			this.actionFlng(flng,wave,time);
		}
		//移動中
		else if(this.status.equals(Status.sailing)){
			this.actionSailing(time,wave);
		}
		//地上に積み出し
		else{
			this.actionFsru(fsru,time,wave);
		}
	}
	
	//海上での行動
	private void actionFlng(FLNG flng,Wave wave,int time){
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<=17){
				//波高が低い時
				if(wave.enableToLoad()){
					if(flng.getVacant()){
						if(flng.getPrevShipId()==this.prevShip.getId()){
							//係船を開始
							this.startLoading = true;
							flng.setPrevShipId(this.id);
							flng.setVacant(false);
							this.finishTime = FLNG.T1;
							
							//FLNGの状態をセット
							int hour = (FLNG.T1+time)%24;
							int later;
							if(hour<8){
								later = FLNG.T1 + (8-hour);
							}
							else if(hour>17){
								later = FLNG.T1 + (8+24-hour);
							}
							else{
								later = FLNG.T1;
							}
							double estimateFLNG = flng.getAmount() + FLNG.Q*later - this.W;
							double idealFLNG = flng.calcIdealAmount(later,this);
							
							if(estimateFLNG>(this.W-FLNG.Q*FLNG.T1)+cancelLNG*(idealFLNG-(this.W-FLNG.Q*FLNG.T1))){
								flng.setLngCondition(LngCondition.full);
								//FLNGが溢れそうな場合
								int nightWaitingTime;//夜間の待ち時間
								if(hour>17){
									nightWaitingTime = 24 + 8 - hour;
								}
								else if(hour<8){
									nightWaitingTime = 8 - hour;
								}
								else{
									nightWaitingTime = 0;
								}
								
								//夜間出発の場合
								if(nightWaitingTime!=0){
									//無駄が多ければ17:00出発
									if(nightWaitingTime>=this.cancelTime){
										//後の船が到着可能であるか
										int nextArrival = (int)Math.ceil(LNG_ship.L-this.postShip.getPosition()/LNG_ship.V_max);
										int nextLeave = FLNG.T1 - (8-17+24-nightWaitingTime);
										if(nextLeave>=nextArrival){
											this.finishTime = nextLeave;
										}
									}
								}
							}
							else if(estimateFLNG<(this.W-FLNG.Q*FLNG.T1)+restLNG*(idealFLNG-(this.W-FLNG.Q*FLNG.T1))){
								flng.setLngCondition(LngCondition.shortage);
								//FLNGが枯渇しそうな場合
								//係船前の状態に戻す
								this.finishTime = (int)Math.ceil(this.W/FLNG.Q);
								//W-Q*T1に戻す
								if(this.restLNG==0){
									double afterLNG = flng.getAmount() + FLNG.Q*FLNG.T1 - this.W;
									this.finishTime = FLNG.T1 + (int)Math.ceil(((this.W-FLNG.Q*FLNG.T1)-afterLNG)/FLNG.Q);
								}
							}
							else{
								flng.setLngCondition(LngCondition.normal);
							}
							
							//干上がってしまう場合
							if(flng.getAmount()+FLNG.T1*FLNG.Q<this.W){
								this.finishTime = (int)Math.ceil((this.W-flng.getAmount())/FLNG.Q);
							}
							flng.load(this);
						}
						//追い抜きを禁止
						else{
							this.watingTime_sum++;
						}
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
				this.watingTime_sum++;
			}
		}
		//係船を終えた場合
		else if(this.finishLoading){
			if(time>=8&&time<=17){
				//波高が低い時
				if(wave.enableToLoad()){
					//出発
					this.loadingTime = 0;
					this.finishLoading = false;
					this.startLoading = false;
					this.destination = Status.fsru;
					this.status = Status.sailing;
					this.actionSailing(time,wave);
				}
				//波高が高い時
				else{
					this.watingTime_sum++;
				}
			}
			else{
				//17時以降にどうするか
				this.watingTime_sum++;
			}
		}
		//係船中
		else{
			flng.load(this);
		}
	}
	
	//航海中の行動
	private void actionSailing(int time,Wave wave){
		//FLNGに向かうとき
		if(this.destination.equals(Status.flng)){
			//速度を決定
			double remainLong = LNG_ship.L - this.position;
			int remainTime = prevShip.getLeavingTime();
			if(remainTime==0){
				//変更しない
			}
			else{
				this.V = remainLong/remainTime;
			}
			//間に合わない時
			if(this.V<0 || this.V>LNG_ship.V_max){
				this.V = LNG_ship.V_max;
				//最速で向かった時に係船できるかどうか
				int timeLeft = (int)Math.ceil((remainLong/LNG_ship.V_max));
				if((timeLeft<6) && (!wave.enableToLoad())){
					//高波で係船できなことがわかっている場合
					if(time/6 == (time+timeLeft-1)/6){
						timeLeft += 6 - (time+timeLeft)%6 +1;
					}
				}
				int hour = timeLeft % 24;
				//夜間着の場合
				if(hour>17){
					timeLeft += 24 + 8 - hour;
				}
				else if(hour<8){
					timeLeft += 8 - hour;
				}
				else{
					//そのまま
				}
				this.V = remainLong/timeLeft;
			}
			this.position += this.V;
			
			//到着した時
			if(this.position>=LNG_ship.L){
				this.addTransportCost(this.V-(this.position-LNG_ship.L));
				this.position = LNG_ship.L;
				this.status = this.destination;
			}
			else{
				this.addTransportCost(this.V);
			}
		}
		//FSRUに向かうとき
		else if(this.destination.equals(Status.fsru)){
			//速度を決定
			double remainLong = LNG_ship.L + this.position;
			int remainTime = prevShip.getLeavingTime();
			this.V = remainLong/(remainTime-FSRU.T2);
			if(this.V<0 || this.V>LNG_ship.V_max){
				this.V = LNG_ship.V_max;
			}
			//FSRUで待たないように速度を調整
			int remainFSRU = (int)Math.ceil(this.position/this.V);//FSRUまでの到着予定時間
			//前がFSRUで積載中であれば遅らせる
			if(this.prevShip.getStatus()==Status.fsru){
				int leaveFsruTime = FSRU.T2 - this.prevShip.getLoadingTime();
				if(leaveFsruTime>remainFSRU){
					remainFSRU = leaveFsruTime;
				}
			}
			int hour = (time + remainFSRU)%24;
			//夜間到着の場合のみ8時着に合わせる
			if(hour>17 || hour<8){
				int timeDelta;
				if(hour>17){
					timeDelta = 24 + 8 - hour;
				}
				else{
					timeDelta = 8 - hour;
				}
				remainFSRU += timeDelta;
			}
			this.V = this.position / remainFSRU;
			
			this.position -= this.V;
			//到着した時
			if(this.position<=0){
				this.addTransportCost(this.V+this.position);
				this.position = 0;
				this.status = this.destination;
				
			}
			else{
				this.addTransportCost(this.V);
			}
		}
		else{
			System.out.println("invalid value in destination");
		}
	}
	
	//港での行動
	private void actionFsru(FSRU fsru,int time, Wave wave){
		//係船をまだ開始していない場合
		if(!this.startLoading){
			if(time>=8&&time<=17){
				if(fsru.getVacant()){
					if(fsru.getPrevShipId()==this.prevShip.getId()){
						//係船を開始
						this.startLoading = true;
						fsru.addTransportCost(this.transportCost);
						fsru.setPrevShipId(this.id);
						this.transportCost = 0;
						fsru.setVacant(false);
						fsru.load(this);
					}
					else{
						this.watingTime_sum++;
					}
				}
				else{
					//待ち時間を追加
					this.watingTime_sum++;
				}
			}
			else{
				//17時以降にどうするか
				this.watingTime_sum++;
			}
		}
		//係船を終えた場合
		
		else if(this.finishLoading){
			if(time>=8&&time<=17){
				//出発
				this.loadingTime = 0;
				this.startLoading = false;
				this.finishLoading = false;
				this.destination = Status.flng;
				this.status = Status.sailing;
				this.actionSailing(time,wave);
			}
			else{
				//17時以降にどうするか
				this.watingTime_sum++;
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
			.append("\nnextGoalTime:\t").append(this.prevShip.getLeavingTime())
			.append("\n状態:\t");
		switch (this.status) {
		case flng:
			result.append("海上").append("\tloadingTime:\t").append(this.loadingTime);
			break;
		case sailing:
			result.append("移動中 to ").append(this.destination).append("\tposition:\t").append(this.position).append("\tV:\t").append(this.V);
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
		return this.position+","+this.amount+","+this.loadingTime+","+this.status.toString()+","+this.V+","+this.prevShip.getLeavingTime()+","+this.finishTime+",";
	}
}
