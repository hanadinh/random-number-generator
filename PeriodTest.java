package final_project;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;
import java.util.Random;

public class PeriodTest extends AbstractSimulation {	
	PlotFrame GFSRplotFrame = new PlotFrame("Number of Steps", "Displacement", "Displacement vs Steps in GFSR");
	PlotFrame LCGplotFrame = new PlotFrame("Number of Steps", "Displacement", "Displacement vs Steps in LCG");
	
	Random rand = new Random();
	long seed = rand.nextLong();
	
	// LCG 
	LCG LCGgenerator = new LCG(seed, 16807, 2147483647, 0);
    
	
	// GFSR
	GFSR GFSRgenerator;
	
    int currentStep = 0;
	
	public PeriodTest() {
		// GFSR
		int p = 250;
	    int q = 103;
	    int[] sequence = new int[p];

	    for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }
	    
	    GFSRgenerator = new GFSR(sequence, p, q);
	}
	
	public void initialize() {
		GFSRplotFrame.setConnected(0, true); // Connect data points with lines
		GFSRplotFrame.clearData();
		LCGplotFrame.setConnected(0, true); // Connect data points with lines
		LCGplotFrame.clearData();
	}
	
    public static void main(String[] args) {
        SimulationControl.createApp(new PeriodTest());
    }

	@Override
	protected void doStep() {
      double GFSRrandomStep = GFSRgenerator.nextRandom();
      double LCGrandomStep = LCGgenerator.nextRandom();
      GFSRplotFrame.append(0, currentStep, GFSRrandomStep);
      LCGplotFrame.append(0, currentStep, LCGrandomStep);
      currentStep++;
	}
	
}
