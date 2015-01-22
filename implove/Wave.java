package implove;


public class Wave {
	//private final double HCR = 3.0;
	private final double p = 0.95;//以下→以下
	private final double q = 0.15;//以上→以下
	private boolean judge = true;//波高が以下であるか
	private final int waveSpan = 6;//何時間ごとに波高が変化するか

	
	public void updateWave(int time){
		//変化するとき
		if(time%this.waveSpan==0){
			double rand = Math.random();
			if(this.judge){
				if(rand>this.p){
					this.judge = false;
				}
			}
			else {
				if(rand<this.q){
					this.judge = true;
				}
			}
		}
	}
	
	public boolean enableToLoad(){
		return this.judge;
	}
	
	@Override
	public String toString() {
		if(this.enableToLoad()){
			return "低波";
		}
		else{
			return "高波";
		}
	}
	public String toCsv(){
		if(this.enableToLoad()){
			return Integer.toString(1);
		}
		else{
			return Integer.toString(0);
		}
	}
}
