package javaFiles;

public class FSRU {
	private final double T2 = 54;//積載時間
	private double amount = 0;//総量
	private final double price = 1;//LNGの単価
	private boolean vacant = true;//係船可能かどうか
	private boolean nextVacant = true;
	
	public double calcProfit() {
		return this.price * this.amount;
	}
	public boolean getVacant(){
		return this.vacant;
	}
	public void updateVacant(){
		this.vacant = this.nextVacant;
	}
	public void setVacant(boolean input){
		this.vacant = input;
	}
	
	//LNGを受け取る
	public void load(LNG_ship ship){
		this.nextVacant = false;
		ship.addLoadingTime();;
		
		//係船が終わった時
		if(ship.getLoadingTime()>=this.T2){
			this.amount += ship.getAmount();
			ship.resetAmount();
			
			//初期化
			ship.resetLoadingTime();
			ship.setFinishLoading();
			this.nextVacant = true;
		}
	}
	@Override
	public String toString() {
		return "FSRU\nLNG\t"+this.amount+"\nvacant\t"+this.vacant;
	}
	public String toCsv(){
		String result = this.amount+",";
		if(this.vacant){
			return result+0;
		}
		else{
			return result+1;
		}
	}
}
