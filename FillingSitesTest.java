package final_project;

import java.util.Random;

import org.opensourcephysics.controls.AbstractSimulation;
import org.opensourcephysics.controls.SimulationControl;
import org.opensourcephysics.frames.PlotFrame;

public class FillingSitesTest extends AbstractSimulation {
	Random rand = new Random();
	int monteCarloSteps;
	int latticeSize;
	LCG lcg;
	GFSR gfsr;
	int currentStep = 0;
	boolean[][] LGClattice;
	boolean[][] GFSRlattice;
	PlotFrame LCGunFilledFraction = new  PlotFrame("Steps", "Unfilled Sites", "Unfilled Sites in LCG");
	PlotFrame GFSRunFilledFraction = new  PlotFrame("Steps", "Unfilled Sites", "Unfilled Sites in GFSR");
	PlotFrame LCGsites = new PlotFrame("x", "y", "LCG Lattice");
	PlotFrame GFSRsites = new PlotFrame("x", "y", "GFSR Lattice");
    int LCGfilledCount = 0;
    int GFSRfilledCount = 0;
	
	public FillingSitesTest() {
	}
	
	public void initialize() {
		monteCarloSteps = control.getInt("Monte Carlo steps");
		latticeSize = control.getInt("Lattice size");
		
		LGClattice = new boolean[latticeSize][latticeSize];
		GFSRlattice = new boolean[latticeSize][latticeSize];
		
		LCGunFilledFraction.clearData();
		GFSRunFilledFraction.clearData();
		LCGsites.clearData();
		GFSRsites.clearData();
		
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
	}
	
	public static void main(String[] args) {
        SimulationControl.createApp(new FillingSitesTest());
    }
	
	protected void doStep() {
		if (currentStep < monteCarloSteps) {
			for (int t = 0; t < latticeSize * latticeSize; t++) {
				// LCG
				double xRandom = lcg.nextRandom();
                double yRandom = lcg.nextRandom();
                int x = (int) (xRandom * (latticeSize - 1)) + 1;
                int y = (int) (yRandom * (latticeSize - 1)) + 1;
                if (!LGClattice[x - 1][y - 1]) {
                	LCGfilledCount++;
                	LGClattice[x - 1][y - 1] = true;
                	LCGsites.append(0, x, y);
                }
                
                
                // GFSR
                xRandom = gfsr.nextRandom();
                yRandom = gfsr.nextRandom();
                x = (int) (xRandom * (latticeSize - 1)) + 1;
                y = (int) (yRandom * (latticeSize - 1)) + 1;
                if (!GFSRlattice[x - 1][y - 1]) {
                	GFSRfilledCount++;
                	GFSRlattice[x - 1][y - 1] = true;
                	GFSRsites.append(0, x, y);
                }
			}
			LCGunFilledFraction.append(0, currentStep, (latticeSize * latticeSize) - LCGfilledCount);
			GFSRunFilledFraction.append(0, currentStep, (latticeSize * latticeSize) - GFSRfilledCount);
			currentStep++;
		} else {
			this.stopSimulation();
		}
	}
	
	public void reset() {
		control.setValue("Monte Carlo steps", 50);
		control.setValue("Lattice size", 20);
		
		this.initialize();
    }
}
