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
	private double idealAmount;//理想的なLNGの量
	public static enum LngCondition{full, normal, shortage;} 
	private LngCondition condition = LngCondition.normal;
	
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
	public LngCondition getLngCondition(){
		return this.condition;
	}
	public void setLngCondition(LngCondition input){
		this.condition = input;
	}
	public void updateIdealAmount(int day, int time, LNG_ship ship){
		int sumTime = (day-1)*24 + time-8;
		int start = (int) Math.ceil(LNG_ship.L/LNG_ship.V_max);
		if(sumTime<=start){
			this.idealAmount = sumTime*FLNG.Q;
		}
		else{
			this.idealAmount = start*FLNG.Q - (sumTime-start)*(start*FLNG.Q-(ship.getW()-FLNG.Q*FLNG.T1))/(LngSimulater.finish_year*365*24-8-start);
		}
	}
	//later後の理想LNG値を返す
	public double calcIdealAmount(int later,LNG_ship ship){
		int start = (int) Math.ceil(LNG_ship.L/LNG_ship.V_max);
		double dy = start*FLNG.Q - (ship.getW()-FLNG.Q*FLNG.T1);
		double dx = LngSimulater.finish_year*365*24-8-start;
		double steep = dy/dx;
		return this.idealAmount-steep*later;
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
			//容量を超えていた場合
			if(ship.getAmount()>ship.getW()){
				this.amount -= addLNG - (ship.getAmount()-ship.getW());
				ship.setAmount(ship.getW());
			}
			else{
				this.amount-=addLNG;
			}
			
		}
		
		
		//係船が終わった時
		if(ship.getLoadingTime()>=ship.getFinishTime()){
			ship.setFinishLoading();
			this.nextVacant = true;
		}
	}
	@Override
	public String toString() {
		return "FLNG\nLNG\t"+this.amount+"\nvacant\t"+this.vacant+"\ncondition\t"+this.condition.toString();
	}
	public String toCsv(){
		String result = this.amount+","+this.idealAmount+","+this.condition.toString()+",";
		if(this.vacant){
			result+=0;
		}
		else{
			result+=1;
		}
		return result+","+this.prevShipId;
	}
}
