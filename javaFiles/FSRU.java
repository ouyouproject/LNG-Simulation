package javaFiles;

public class FSRU {
	private final double T2 = 50;//係船時間
	private double amount = 0;//総量
	private final double price = 1;//LNGの単価
	private boolean vacant = true;//係船可能かどうか
	
	public double calcProfit() {
		return this.price * this.amount;
	}
	public boolean getVacant(){
		return this.vacant;
	}
	public double getAmount() {
		return this.amount;
	}
	
	//LNGを受け取る
	public void load(LNG_ship ship){
		System.out.println();
		System.out.println("load");
		this.vacant = false;
		ship.addLoadingTime();;
		System.out.println("loadingTime :"+ship.getLoadingTime());
		//係船が終わった時
		if(ship.getLoadingTime()>=this.T2){
			System.out.print("LNG in FSRU :" + this.amount + " →");
			this.amount += ship.getAmount();
			System.out.println(this.amount);
			ship.resetAmount();
			
			//初期化
			ship.resetLoadingTime();;
			ship.setFinishLoading();
			this.vacant = true;
		}
	}
}
