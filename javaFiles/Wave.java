package javaFiles;

public class Wave {
	private final double HCR = 3.0;
	private final double p = 0.9;//以下→以下
	private final double q = 0.3;//以上→以下
	private boolean status = true;
	public Wave(){
		
	}
	public boolean judgeLoading(){
		double rand = Math.random();
		if(status){
			if(rand>p){
				this.status = false;
			}
		}
		else {
			if(rand<q){
				this.status = true;
			}
		}
		return status;
	}
}
