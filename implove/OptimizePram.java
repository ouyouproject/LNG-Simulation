package implove;

import java.util.Arrays;

public class OptimizePram {
	public static void main(String[] args) {
		//x = [n,W0,W,cancelLNG,cancelTime,restLNG,stormRest]船の数、FLNGの貯蔵量、船の貯蔵量、船の速度、
		double[] x_min = new double[]{1.0, 130000.0, 60000.0, 0.0, 0.0, 0.0, 0.0};//最小値
		double[] x_max = new double[]{20.0, 300000.0, 200000.0, 3.0, 16.0, 1.0, 20.0};//最大値
		Simulater simulater = new LngSimulater(10);
		//System.out.println(simulater.simulate(new double[]{2.0,100000.0,10000.0}));
		ArtificalBeeColoney artificalBeeColoney = new ArtificalBeeColoney(	simulater,
																			true,
																			x_min, 
																			x_max, 
																			30, 
																			50, 
																			20,
																			true);
		//System.out.println(simulater.simulate(new double[]{1.5961872289472403, 296775.77365864115, 81431.98544028946, 0.047763326584005394, 11.529657014801119, 0.5405906677993654, 12.056734383805257}));
		double[] result = artificalBeeColoney.solveGS_ABC(0.7);
		System.out.println(Arrays.toString(result));
	}
}
