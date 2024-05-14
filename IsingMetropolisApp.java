package final_project;
import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

import java.util.*;

public class IsingMetropolisApp extends AbstractSimulation {
  Ising ising;
  Random rand;
  private int modelType;
  private int steps;
  private int runs;
  private int numRuns;
  private List<Double> energies;
  private List<Double> specificHeats;
  LatticeFrame displayFrame;

  public void initialize() {
	rand = new Random();
	steps = control.getInt("Number of steps");
    numRuns = control.getInt("Number of runs");
    modelType = control.getInt("Type of Model");
    energies = new ArrayList<>();
    specificHeats = new ArrayList<>();
    runs = 0;
  }

  public void doStep() {
	  if (runs < numRuns) {
	      long seed = rand.nextLong();
	      
	      displayFrame = new LatticeFrame("Ising Model");
	      switch (modelType) {
	      	case 1:
	      		ising = new IsingLCG(displayFrame, seed);
	      	case 2:
	      		ising = new IsingGFSR(displayFrame, seed);
	      	default:
	      		ising = new IsingDefaultRand(displayFrame, seed);
	      }
	      
	      for (int step = 0; step < steps; step++) {
		    ising.doOneMCStep();
	      }
	      
	      double energy = ising.energyPerSpin();
          double specificHeat = ising.specificHeat();
          energies.add(energy);
          specificHeats.add(specificHeat);
	      
	      runs++;
	  } else {
		  this.stopSimulation();
          analyzeResults();
	  }
  }


  public void reset() {
	control.setValue("Type of Model", 1);
	control.setValue("Number of steps", 10000); // 10^4 Monte Carlo steps per spin for each run
	control.setValue("Number of runs", 10);
  }
  

  private void analyzeResults() {
      double meanEnergy = calculateMean(energies);
      double meanSpecificHeat = calculateMean(specificHeats);
      
      double stdEnergy = calculateStdDev(energies, meanEnergy);
      double stdSpecificHeat = calculateStdDev(specificHeats, meanSpecificHeat);
      
      double exactEnergy = -1.45306;
      double exactSpecificHeat = 1.49871;
      
      double deltaE = Math.abs(meanEnergy - exactEnergy);
      double deltaC = Math.abs(meanSpecificHeat - exactSpecificHeat);
      
      double ratioE = deltaE / stdEnergy;
      double ratioC = deltaC / stdSpecificHeat;
      
      control.println(String.format("Mean Energy per Spin: %.5f", meanEnergy));
      control.println(String.format("Mean Specific Heat: %.5f", meanSpecificHeat));
      control.println(String.format("Energy Std Dev: %.5f", stdEnergy));
      control.println(String.format("Specific Heat Std Dev: %.5f", stdSpecificHeat));
      control.println(String.format("Delta E: %.5f", deltaE));
      control.println(String.format("Delta C: %.5f", deltaC));
      control.println(String.format("Ratio E: %.5f", ratioE));
      control.println(String.format("Ratio C: %.5f", ratioC));
      
      boolean biased = (ratioE > 1.0 || ratioC > 1.0);
      
      control.println(String.format("Biased: %b", biased));
  }
  
      
  private double calculateMean(List<Double> values) {
      double sum = 0.0;
      for (double value : values) {
          sum += value;
      }
      return sum / values.size();
  }

  private double calculateStdDev(List<Double> values, double mean) {
      double sum = 0.0;
      for (double value : values) {
          sum += Math.pow(value - mean, 2);
      }
      return Math.sqrt(sum / values.size());
  }

  public static void main(String[] args) {
    SimulationControl.createApp(new IsingMetropolisApp());
  }
}