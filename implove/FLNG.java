package implove;



public class FLNG {
	public static final double Q = 1000;//時間あたり生産量
	private double W0;//キャパシティ
	public static final int T1 = 54;//係船時間
	public static final int prepareTime = 10;//係船準備の総時間
	private boolean vacant = true;//係船できるか。falseなら他の船が使用中
	private int prevShipId;
	private boolean nextVacant = true;//次の時間にvacantがtrueか
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
	public int getPrevShipId(){
		return this.prevShipId;
	}
	public void setPrevShipId(int input){
		this.prevShipId = input;
	}
	
	public boolean getVacant() {
		return this.vacant;
	}
	public double getAmount(){
		return this.amount;
	}
	public void updateVacant(){
		this.vacant = this.nextVacant;
	}
	public void setVacant(boolean input){
		this.vacant = input;
	}
	
	//係船
	public void load(LNG_ship ship) {
		this.nextVacant = false;
		ship.addLoadingTime();
		//準備中
		if(ship.getLoadingTime()<=prepareTime){
			
		}
		else{
			double addLNG = ship.getW()/(FLNG.T1-FLNG.prepareTime);
			if(this.amount<addLNG){
				addLNG = this.amount;
			}
			ship.addAmount(addLNG);
			this.amount-=addLNG;
		}
		
		
		//係船が終わった時
		if(ship.getLoadingTime()>=ship.getFinishTime()){
			ship.setFinishLoading();
			this.nextVacant = true;
		}
	}
	@Override
	public String toString() {
		return "FLNG\nLNG\t"+this.amount+"\nvacant\t"+this.vacant;
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
