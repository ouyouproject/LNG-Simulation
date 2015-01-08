package javaFiles;

public class LNG_ship {
	private double amount = 0;
	private double W;
	private double V;
	public static enum Status{flng,sailing,fsru;}
	private Status status;//どこにいるのか
	
	public LNG_ship(double input_W, double input_V) {
		this.W = input_W;
		this.V = input_V;
		this.status = Status.fsru;
	}
	
	public void setAmount(double input) {
		this.amount = input;
	}
	
	public double getAmount() {
		return this.amount;
	}
	
	public void setStatus(Status s) {
		this.status = s;
	}
	
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
			this.actionFsru(fsru,wave,time);
		}
	}
	
	private void actionFlng(FLNG flng,Wave wave,int time){
		
	}
	
	private void actionSailing(int time){
		
	}
	
	private void actionFsru(FSRU fsru,Wave wave,int time){
		
	}
	
}
