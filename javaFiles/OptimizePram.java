package javaFiles;

import java.util.Arrays;

public class OptimizePram {

	public static void main(String[] args) {
		//x = [n,W0,W,V]船の数、FLNGの貯蔵量、船の貯蔵量、船の速度
		double[] x_min = new double[]{0.0, 2000.0, 2000.0, 0.0};//最小値
		double[] x_max = new double[]{20.0, 1000000.0, 200000.0, 40.0};//最小値
		Simulater simulater = new LngSimulater();
		//System.out.println(simulater.simulate(new double[]{2.0,100000.0,10000.0,30.0}));
		ArtificalBeeColoney artificalBeeColoney = new ArtificalBeeColoney(	simulater,
																			true,
																			x_min, 
																			x_max, 
																			100, 
																			500, 
																			50);
		double[] result = artificalBeeColoney.solveGS_ABC(0.7);
		System.out.println(Arrays.toString(result));
	}

}
