package implove;

import implove.BenchmarkSimulater.FunctionType;

import java.util.Arrays;

public class TestArtificalBeeColony {
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		int dem = 30;//次元数
		double[] x_max = new double[dem];
		double[] x_min = new double[dem];
		for(int i=0; i<dem; i++){
			x_max[i] = 5.12;
			x_min[i] = -5.12;
		}
		BenchmarkSimulater rastrign = new BenchmarkSimulater(FunctionType.two_n_minima);
		double[][] range = rastrign.getRange(dem);
		ArtificalBeeColoney artificalBeeColoney = new ArtificalBeeColoney(	(Simulater)rastrign,
																			false,
																			range[0], 
																			range[1], 
																			1000, 
																			1000, 
																			100,
																			true);
		double[] result = artificalBeeColoney.solveGS_ABC(0.1);
		System.out.println(Arrays.toString(result));
		rastrign.printAnser();
	}
}
