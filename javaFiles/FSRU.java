package javaFiles;

public class FSRU {
	private final double T2 = 54;//積載時間
	private double amount = 0;//総量
	private final double price = 1;//LNGの単価
	private boolean vacant = true;//係船可能かどうか
	
	public double calcProfit() {
		return this.price * this.amount;
	}
	public boolean getVacant(){
		return this.vacant;
	}
	
	//LNGを受け取る
	public void load(LNG_ship ship){
		this.vacant = false;
		ship.addLoadingTime();;
		
		//係船が終わった時
		if(ship.getLoadingTime()>=this.T2){
			this.amount += ship.getAmount();
			ship.resetAmount();
			
			//初期化
			ship.resetLoadingTime();;
			ship.setFinishLoading();
			this.vacant = true;
		}
	}
	
}
