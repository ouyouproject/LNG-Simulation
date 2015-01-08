package javaFiles;

public class FSRU {
	private final double T2 = 54;//積載時間
	private double amount = 0;//総量
	private final double price = 1;//LNGの単価
	private boolean judge = true;//係船可能かどうか
	
	public double calcProfit() {
		return this.price * this.amount;
	}
	
	//LNGを受け取る
	public void loadLNG(LNG_ship lng_ship){
		if(judge){
			amount += lng_ship.getAmount();
			lng_ship.setAmount(0.0);
		}
		else{
			//待ち時間あり
		}
	}
	
}
