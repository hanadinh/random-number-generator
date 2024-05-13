package final_project;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

import java.util.*;

public class UniformityTest extends AbstractSimulation {	
	HistogramFrame LCGdistribution = new HistogramFrame("x", "H(x)", "LCG Histogram");
	HistogramFrame GFSRdistribution = new HistogramFrame("x", "H(x)", "GFSR Histogram");
	Random rand = new Random();
	long seed = rand.nextLong();
	LCG lcg = new LCG(seed, 16807, 2147483647, 0);
	GFSR gfsr;
	static int expectedFrequency;
	int numBins;
	int totalSteps;
	int currentStep = 0;
	int[] lcgFrequencies;
    int[] gfsrFrequencies;
	
	public UniformityTest() {
		int p = 250;
	    int q = 103;
	    int[] sequence = new int[p];
	    for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
	    }
	    gfsr = new GFSR(sequence, p, q);
	}
	
	public void initialize() {
		expectedFrequency = control.getInt("Expected frequency");
		numBins = control.getInt("Number of bins");
		totalSteps = control.getInt("Total steps");
		
		lcgFrequencies = new int[numBins];
		gfsrFrequencies = new int[numBins];
		LCGdistribution.setBinWidth(1.00 / numBins); 
		GFSRdistribution.setBinWidth(1.00 / numBins);
	}
	
    public static void main(String[] args) {
        SimulationControl.createApp(new UniformityTest());
    }

	@Override
	protected void doStep() {
		if (currentStep < totalSteps) {
			control.println("Iteration " + currentStep);
		      double LCGrandomStep = lcg.nextRandom();
		      double GFSRrandomStep = gfsr.nextRandom();
		
		      LCGdistribution.append(LCGrandomStep);
		      GFSRdistribution.append(GFSRrandomStep);
		      
		      // Update frequencies
	        int lcgBinIndex = (int) (LCGrandomStep * numBins);
	        int gfsrBinIndex = (int) (GFSRrandomStep * numBins);
	        lcgFrequencies[lcgBinIndex]++;
	        gfsrFrequencies[gfsrBinIndex]++;
	        
	        currentStep++;
		} else {
			this.stopSimulation();
    		control.println("Chi Square for LCG: " + calculateChiSquare(lcgFrequencies));
        	control.println("Chi Square for GFSR: " + calculateChiSquare(gfsrFrequencies));
		}
	}
	
	public void reset() {
		control.setValue("Expected frequency", 100);
		control.setValue("Number of bins", 100);
		control.setValue("Total steps", 10000);
		
		Arrays.fill(lcgFrequencies, 0);
        Arrays.fill(gfsrFrequencies, 0);
    }
	
	private static double calculateChiSquare(int[] observed) {
        double chiSquare = 0;
        for (int obs : observed) {
            chiSquare += Math.pow(obs - expectedFrequency, 2) / expectedFrequency;
        }
        return chiSquare;
    }
}
