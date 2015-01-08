package javaFiles;

public class FLNG {
	private final double Q = 2000;//供給量
	private double W0;//キャパシティ
	private double T1 = 54;//積載時間
	private boolean judge = true;//積載できるか。falseなら他の船が使用中
	private double amount;//LNGの量
	public FLNG(double input_W0) {
		this.W0 = input_W0;
	}
	
	//LNGを汲み上げ
	public void getLNG(){
		amount += Q;
		if(amount>W0){
			amount = W0;
		}
	}
	
	
}
