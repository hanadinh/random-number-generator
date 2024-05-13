package final_project;


import java.util.Random;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.PlotFrame;

public class Correlations extends AbstractSimulation {
	Random rand = new Random();
	int totalSteps;
	LCG lcg;
	GFSR gfsr;
	int currentStep = 0;
	int k;
	PlotFrame LCGhiddenCor = new PlotFrame("x_i", "x_(i+k)", "x_(i+k) versus x_i in LCG");
	PlotFrame GFSRhiddenCor = new PlotFrame("x_i", "x_(i+k)", "x_(i+k) versus x_i in GFSR");
	double[] lcgValues;
    double[] gfsrValues;
	
	public Correlations() {
	}
	
	public void initialize() {
		totalSteps = control.getInt("Total steps");
		k = control.getInt("k");

		LCGhiddenCor.clearData();
		GFSRhiddenCor.clearData();
		
		// GFSR
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }
		
		// Initialize random generators
        lcg = new LCG(12, 16807, 2147483647, 0);
        gfsr = new GFSR(sequence, p, q);
        
        // Generate first k elements for both generators
        lcgValues = new double[totalSteps];
        gfsrValues = new double[totalSteps];
        for (int i = 0; i < k; i++) {
            lcgValues[i] = lcg.nextRandom();
            gfsrValues[i] = gfsr.nextRandom();
        }
	}
	
	public static void main(String[] args) {
        SimulationControl.createApp(new Correlations());
    }
	
	protected void doStep() {
		if (currentStep + k < totalSteps) {
			double xi = lcgValues[currentStep];
			double xik = lcg.nextRandom();
			LCGhiddenCor.append(0, xi, xik);
			lcgValues[currentStep + k] = lcg.nextRandom();

			xi = gfsrValues[currentStep];
			xik = gfsr.nextRandom();
			GFSRhiddenCor.append(0, xi, xik);	
			gfsrValues[currentStep + k] = gfsr.nextRandom();
			
			currentStep++;
		} else {
			this.stopSimulation();
		}
	}
	
	public void reset() {
		control.setValue("Total steps", 100);
		control.setValue("k", 2);
		
		this.initialize();
    }
}
