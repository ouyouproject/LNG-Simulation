package javaFiles;

import java.util.Arrays;

public class ArtificalBeeColoney {
	private Simulater simulater;//目的関数
	private boolean maximize;//最大化するか否か
	private double[] x_min;//解の許容範囲（最小）
	private double[] x_max;//解の許容範囲（最大）
	private int population;//個体数
	private int genelation;//世代数
	private int[] times;//連続探索時間
	private int limit;//連続探索制限
	private int demention;//解の次元数
	private double[] probability;//相対確率
	
	//private double gamma;//内分パラメータ
	private double alpha;//突然変異率
	
	private double[][] X;//探索解群
	private double[] f;//探索解群の目的関数値
	private double[] fit;//各探索解群の適合度
	private double[] x_best;//暫定最適解
	private double fit_best;//暫定最適解の適合度
	private double f_best;//暫定最適解の目的関数値
	
	//public static enum method {ABC,AC_ABC,GS_ABC;}
	public static enum method {ABC,GS_ABC;}
	
	public ArtificalBeeColoney(	Simulater input_simulater, 
								boolean input_maximize, 
								double[] input_x_min, 
								double [] input_x_max, 
								int input_population,
								int input_genelation,
								int input_limit){
		//引数を設置
		this.simulater = input_simulater;
		this.maximize = input_maximize;
		this.x_min = input_x_min;
		this.x_max = input_x_max;
		this.population = input_population;
		this.genelation = input_genelation;
		this.limit = input_limit;
		this.demention = x_max.length;
	}
	
	public double[] solveABC(){
		return this.solve(method.ABC);
	}
	/*
	public double[] solveAC_ABC(double input_gamma){
		this.gamma = input_gamma;
		return solve(method.AC_ABC);
	}
	*/
	public double[] solveGS_ABC(double input_alpha){
		this.alpha = input_alpha;
		return this.solve(method.GS_ABC);
	}
	
	
	
	//実際の計算
	private double[] solve(method m){
		//初期化
		this.initialize();
		int g = 1;
		while(g <= this.genelation){
			System.out.println(g);
			//探索蜂による探索
			for(int i=0; i<this.population; i++){
				this.updatePoint(m, i);
			}
			//追従蜂による探索
			this.updateProbability();
			for(int i=0; i<this.population; i++){
				//ルーレット選択
				int l = this.rouletteWheelSelection();
				this.updatePoint(m, l);
			}
			
			//最良個体の更新
			int index_b = this.argMax(this.fit);
			if(this.fit[index_b] > this.fit_best){
				this.x_best = this.X[index_b];
				this.fit_best = this.fit[index_b];
				this.f_best = this.f[index_b];
				System.out.println("x: "+Arrays.toString(this.x_best));
				System.out.println("f: "+this.f_best);
			}
			
			//偵察蜂による探索
			for(int i=0; i<this.population; i++){
				if(this.times[i]>=this.limit){
					for(int j=0; j<this.demention; j++){
						//Xをランダムで設定
						this.X[i][j] = this.makeRandam(this.x_min[j],this.x_max[j]);
					}
				}
			}
		
		g++;
		}
		return this.makeReturn(this.x_best, this.f_best);
	}
	
	//初期化
	private void initialize(){
		X = new double[this.population][this.demention];
		times = new int[this.population];
		probability = new double[this.population];
		for(int i=0; i<this.population; i++){
			for(int j=0; j<this.demention; j++){
				//Xをランダムで設定
				this.X[i][j] = this.makeRandam(this.x_min[j], this.x_max[j]);
			}
			this.f = new double[population];
			this.fit = new double[population];
			this.f[i] = this.simulater.simulate(X[i]);
			this.fit[i] = this.calcFit(this.f[i]);
		}
	}
	
	//探索解を更新
	private void updatePoint(method m, int i){
		double[] x = this.X[i];
		double[] v = this.makeCandidate(m, x);
		double f_v = simulater.simulate(v);
		//候補会が優れていた場合のみ更新
		if(calcFit(f_v)>this.calcFit(f[i])){
			this.X[i] = v;
			this.fit[i] = this.calcFit(f_v);
			this.f[i] = f_v;
			this.times[i] = 0;
		}
		else{
			this.times[i]++;
		}		
	}
	//更新候補解を作成
	private double[] makeCandidate(method m,double x[]){
		double[] v = this.cloneArray(x);
		//ある個体k番目を選ぶ
		int k = (int) Math.floor(this.makeRandam(0.0, this.population));
		int j;//ある次元
		switch (m) {
		case ABC:
			//ある次元j番目を選ぶ
			j = (int) Math.floor(this.makeRandam(0.0, this.demention));
			v[j] = x[j] + this.makeRandam(-1.0, 1.0)*(x[j]-this.X[k][j]);
			//許容解内に収める
			if(v[j]>this.x_max[j]){
				v[j] = this.x_max[j];
			}
			else if(v[j]<this.x_min[j]){
				v[j] = this.x_min[j];
			}
			break;
		/*
		case AC_ABC:
			
			break;
		*/
		case GS_ABC:
			//すべての次元について
			for(j=0; j<this.demention; j++){
				if(Math.random()<=this.alpha){
					v[j] = x[j] + this.makeRandam(-1.0, 1.0)*(x[j]-this.X[k][j]);
					//許容解内に収める
					if(v[j]>this.x_max[j]){
						v[j] = this.x_max[j];
					}
					else if(v[j]<this.x_min[j]){
						v[j] = this.x_min[j];
					}
				}
			}
			break;
		}
		return v;
	}
	
	//相対確率の計算
	private void updateProbability(){
		double sum = 0;
		for(double fit_i : this.fit){
			sum += fit_i;
		}
		for(int i=0; i<this.population; i++){
			this.probability[i] = this.fit[i]/sum;
		}
	}
	//ルーレット選択
	private int rouletteWheelSelection(){
		double r = Math.random();
		double sum = 0;
		for(int i=0; i<this.population; i++){
			sum += this.probability[i];
			if(sum>r){
				return i;
			}
		}
		return this.population-1;
	}
	
	//適合度の計算
	private double calcFit(double f_input){
		//最大化の時
		if(this.maximize){
			return f_input;
		}
		//最小化の時
		else{
			if(f_input<0){
				return 1 + Math.abs(f_input);
			}
			else{
				return 1 / (1 + f_input);
			}
		}
	}
	
	//最適解とその時の値を配列にセットで格納
	private double[] makeReturn(double[] input_x, double input_f){
		double[] result = new double[input_x.length+1];
		for(int i=0; i<input_x.length; i++){
			result[i] = input_x[i];
		}
		result[input_x.length] = input_f;
		return result;
	}
	
	//a以上b未満までの乱数を作成
	private double makeRandam(double a, double b){
		double big;
		double small;
		if(a > b){
			big = a;
			small = b;
		}
		else{
			big = b;
			small = a;
		}
		double rand = Math.random();
		double delta = big - small;
		return rand*delta + small;
	}
	
	//配列をコピー
	private double[] cloneArray(double[] array){
		double[] clone = new double[array.length]; 
		for(int i=0; i<array.length; i++){
			clone[i] = array[i];
		}
		return clone;
	}
	
	//配列の値が最大値となるindexを返す
	private int argMax(double[] input){
		double max = input[0];
		int index = 0;
		for(int i=1; i<input.length; i++){
			if(max<input[i]){
				max = input[i];
				index = i;
			}
		}
		return index;
	}
}
