package javaFiles;

import java.util.PrimitiveIterator.OfDouble;


public class FLNG {
	private final double Q = 2000;//時間あたり生産量
	private double W0;//キャパシティ
	private double T1 = 50;//係船時間
	private boolean vacant = true;//係船できるか。falseなら他の船が使用中
	private double amount;//LNGの量
	public FLNG(double input_W0) {
		this.W0 = input_W0;
	}
	
	//LNGを汲み上げ
	public void getLNG(){
		System.out.println("getLNG");
		System.out.print("FLNG :"+this.amount + " →");
		this.amount += Q;
		if(this.amount>this.W0){
			this.amount = this.W0;
		}
		System.out.println(this.amount);
		System.out.println();
	}
	
	public boolean getVacant() {
		return this.vacant;
	}
	
	//係船
	public void load(LNG_ship ship) {
		System.out.println();
		System.out.println("load");
		this.vacant = false;
		ship.addLoadingTime();
		System.out.println("loadingTime :"+ship.getLoadingTime());
		double addLNG = ship.getW()/this.T1;
		if(this.amount<addLNG){
			addLNG = this.amount;
		}
		System.out.print("LNG in the ship :" +ship.getAmount()+" →" );
		ship.addAmount(addLNG);
		System.out.println(ship.getAmount());
		System.out.print("LNG in FLNG :" +this.amount+" →" );
		this.amount-=addLNG;
		System.out.println(this.amount);
		
		//係船が終わった時
		if(ship.getLoadingTime()>=this.T1){
			ship.resetLoadingTime();;
			ship.setFinishLoading();
			this.vacant = true;
		}
	}
	
}
