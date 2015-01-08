package javaFiles;


public class Wave {
	//private final double HCR = 3.0;
	private final double p = 0.9;//以下→以下
	private final double q = 0.3;//以上→以下
	private boolean judge = true;//波高が以下であるか
	private final int waveSpan = 6;//何時間ごとに波高が変化するか
	
	public boolean enableToLoad(int time){
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
			return this.judge;
		}
		//波の変化なし
		else{
			return this.judge;
		}
	}
}
