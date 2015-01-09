package javaFiles;


public class FLNG {
	private final double Q = 2000;//時間あたり生産量
	private double W0;//キャパシティ
	private double T1 = 54;//積載時間
	private boolean vacant = true;//係船できるか。falseなら他の船が使用中
	private double amount;//LNGの量
	public FLNG(double input_W0) { 
		this.W0 = input_W0;
	}
	
	//LNGを汲み上げ
	public void getLNG(){
		this.amount += Q;
		if(this.amount>this.W0){
			this.amount = this.W0;
		}
	}
	
	public boolean getVacant() {
		return this.vacant;
	}
	public double getAmount(){
		return this.amount;
	}
	//係船
	public void load(LNG_ship ship) {
		this.vacant = false;
		ship.addLoadingTime();
		double addLNG = ship.getW()/this.T1;
		if(this.amount<addLNG){
			addLNG = this.amount;
		}
		ship.addAmount(addLNG);
		this.amount-=addLNG;
		
		//係船が終わった時
		if(ship.getLoadingTime()>=this.T1){
			ship.resetLoadingTime();;
			ship.setFinishLoading();
			this.vacant = true;
		}
	}
	
}
