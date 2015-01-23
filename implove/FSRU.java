package implove;

public class FSRU {
	public static final int T2 = 54;//係船時間
	public static final int prepareTime = 10;//係船の準備等にかかる総時間
	private double amount = 0;//総量
	public static final double price = 70;//LNGの単価
	private int prevShipId;
	private boolean vacant = true;//係船可能かどうか
	private boolean nextVacant = true;
	private double transportCost=0;//輸送費の合計
	private double constractCost;//三年分の固定費（円）
	private double fixedCost;
	
	
	public FSRU(double input_W0, double input_W, double input_N){
		//建設費の計算
		
		//固定費の計算
		this.fixedCost = this.constractCost*0.005;
	}
	public int getPrevShipId(){
		return this.prevShipId;
	}
	public void setPrevShipId(int input){
		this.prevShipId = input;
	}
	
	public double calcProfit() {
		System.out.println("輸送費");
		System.out.println(this.transportCost);
		System.out.println("建設費");
		System.out.println(this.constractCost);
		System.out.println("固定費");
		System.out.println(this.fixedCost);
		System.out.println("LNG");
		System.out.println(this.amount);
		System.out.println("売上");
		System.out.println(FSRU.price*this.amount);
		return FSRU.price * this.amount-(this.transportCost+this.constractCost+this.fixedCost);
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
	public void addTransportCost(double input) {
		this.transportCost += input;
	}
	public void addAmount(double input){
		this.amount += input;
	}
	
	
	//LNGを受け取る
	public void load(LNG_ship ship){
		this.nextVacant = false;
		ship.addLoadingTime();
		
		//準備期間
		if(ship.getLoadingTime()<=prepareTime){
			//何もしない
		}
		//積込み期間
		else{
			double addLNG = ship.getW()/(FSRU.T2-FSRU.prepareTime);
			if(ship.getAmount()<addLNG){
				addLNG = ship.getAmount();
			}
			ship.subAmount(addLNG);
			this.amount+=addLNG;
		}
		
		//係船が終わった時
		if(ship.getLoadingTime()>=FSRU.T2){
			ship.resetAmount();
			
			//初期化
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
			result+=0;
		}
		else{
			result+=1;
		}
		return result + ","+ this.prevShipId;
	}

	
}
