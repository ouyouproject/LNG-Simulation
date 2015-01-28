package implove;

import java.util.Arrays;


public class BenchmarkSimulater implements Simulater {
	private int demention;
	public static enum FunctionType{sphere,rastrigin,rosenbrock,griewank,alpine,two_n_minima;}
	private FunctionType functionType = FunctionType.rastrigin;
	
	public  BenchmarkSimulater(FunctionType type) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.functionType = type;
	}
	
	@Override
	public double simulate(double[] x) {
		this.demention = x.length;
		switch (this.functionType) {
		case sphere:
			return calcSphere(x);
		case rastrigin:
			return calcRastrigin(x);
		case rosenbrock:
			return calcRosenbrock(x);
		case griewank:
			return calcGriewank(x);
		case alpine:
			return calcAlpine(x);
		case two_n_minima:
			return calcTwo_n_minima(x);
		}
		return 0;
	}
	//最大最小を取得
	public double[][] getRange(int dem){
		this.demention = dem;
		double[] min = new double[this.demention];
		double[] max = new double[this.demention];
		switch (functionType) {
		case sphere:
			for(int i=0; i<demention; i++){
				min[i] = -5.0;
				max[i] = 5.0;
			}
			break;
		case rastrigin:
			for(int i=0; i<demention; i++){
				min[i] = -5.0;
				max[i] = 5.0;
			}
			break;
		case rosenbrock:
			for(int i=0; i<demention; i++){
				min[i] = -5.0;
				max[i] = 10.0;
			}
			break;
		case griewank:
			for(int i=0; i<demention; i++){
				min[i] = -600.0;
				max[i] = 600.0;
			}
			break;
		case alpine:
			for(int i=0; i<demention; i++){
				min[i] = -10.0;
				max[i] = 10.0;
			}
			break;
		case two_n_minima:
			for(int i=0; i<demention; i++){
				min[i] = -5.0;
				max[i] = 5.0;
			}
			break;
		default:
			break;
		}
		double[][] result = new double[2][demention];
		result[0] = min;
		result[1] = max;
		return result;
	}
	//最適解をprint
	public void printAnser(){
		double[] best_x = new double[this.demention];
		switch (this.functionType) {
		case sphere:
			System.out.print("best_x:");
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(0);
			return;
		case rastrigin:
			System.out.print("best_x:");
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(0);
			return;
		case rosenbrock:
			System.out.print("best_x:");
			for(int i=0; i<demention; i++){
				best_x[i] = 1;
			}
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(0);
			return;
		case griewank:
			System.out.print("best_x:");
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(0);
			return;
		case alpine:
			System.out.print("best_x:");
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(0);
			return;
		case two_n_minima:
			System.out.print("best_x:");
			for(int i=0; i<demention; i++){
				best_x[i] = -2.90;
			}
			System.out.println(Arrays.toString(best_x));
			System.out.print("best_f:");
			System.out.println(-78*this.demention);
			return;
		}
		return;
	}
	
	
	
	private double calcSphere(double[] x){
		double result = 0;
		for(double one: x){
			result += one*one;
		}
		return result;
	}
	private double calcRastrigin(double[] x){
		double result = 10*this.demention;
		for(double one: x){
			result += (one*one-10*Math.cos(2*Math.PI*one));
		}
		return result;
	}
	private double calcRosenbrock(double[] x){
		double result = 0;
		for(int i=0; i<x.length-1; i++){
			result += 100*Math.pow(x[i+1]-x[i]*x[i],2)+Math.pow(1-x[i], 2);
		}
		return result;
	}
	private double calcGriewank(double[] x){
		double result = 1;
		double sum = 0;
		double mul = 1;
		for(int i=0; i<x.length; i++){
			double one = x[i];
			sum += one*one;
			mul = mul*Math.cos((one/Math.sqrt(i+1)));
		}
		result += sum/4000 - mul;
		return result;
	}
	private double calcAlpine(double[] x){
		double result = 0;
		for(double one: x){
			result += Math.abs(one*Math.sin(one)+0.1*one);
		}
		return result;
	}
	private double calcTwo_n_minima(double[] x){
		double result = 10*this.demention;
		for(double one: x){
			result += Math.pow(one, 4)-16*one*one+5*one;
		}
		return result;
	}
}
