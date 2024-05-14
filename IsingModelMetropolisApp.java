package final_project;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

import java.util.*;

public class IsingModelMetropolisApp extends AbstractSimulation {
    private Random rand;
    private double T_c;
    private int steps;
    private int runs;
    private int numRuns;
    private List<Double> energiesLCG;
    private List<Double> specificHeatsLCG;
    private List<Double> energiesGFSR;
    private List<Double> specificHeatsGFSR;
    private PlotFrame energyFrame;
    private PlotFrame specificHeatFrame;

    @Override
    public void initialize() {
        rand = new Random();
        T_c = 2 / Math.log(1 + Math.sqrt(2));
        steps = control.getInt("Number of steps");
        numRuns = control.getInt("Number of runs");
        energiesLCG = new ArrayList<>();
        specificHeatsLCG = new ArrayList<>();
        energiesGFSR = new ArrayList<>();
        specificHeatsGFSR = new ArrayList<>();
        energyFrame = new PlotFrame("Run", "Energy", "Energy per Spin for Each Run");
        specificHeatFrame = new PlotFrame("Run", "Specific Heat", "Specific Heat for Each Run");
        runs = 0;
    }

    @Override
    public void doStep() {
        if(runs < numRuns) {
        	int i = runs;
            long seed = rand.nextLong();

            IsingLCG modelLCG = new IsingLCG(seed, T_c);
            modelLCG.runSimulation(steps);
            double energyLCG = modelLCG.computeEnergy();
            double specificHeatLCG = modelLCG.computeSpecificHeat();
            energiesLCG.add(energyLCG);
            specificHeatsLCG.add(specificHeatLCG);
            energyFrame.append(0, i + 1, energyLCG);
            specificHeatFrame.append(0, i + 1, specificHeatLCG);

            IsingGFSR modelGFSR = new IsingGFSR(seed, T_c);
            modelGFSR.runSimulation(steps);
            double energyGFSR = modelGFSR.computeEnergy();
            double specificHeatGFSR = modelGFSR.computeSpecificHeat();
            energiesGFSR.add(energyGFSR);
            specificHeatsGFSR.add(specificHeatGFSR);
            energyFrame.append(1, i + 1, energyGFSR);
            specificHeatFrame.append(1, i + 1, specificHeatGFSR);
            runs++;
        }
        else {
        	this.stopSimulation();
        	analyzeResults();
        }
    }
    
    public void reset() {
		control.setValue("Number of steps", 10000); // 10^4 Monte Carlo steps per spin for each run
		control.setValue("Number of runs", 10);
    }

    private void analyzeResults() {
    	double meanEnergyLCG = calculateMean(energiesLCG);
        double meanSpecificHeatLCG = calculateMean(specificHeatsLCG);
        double meanEnergyGFSR = calculateMean(energiesGFSR);
        double meanSpecificHeatGFSR = calculateMean(specificHeatsGFSR);

        double stdEnergyLCG = calculateStdDev(energiesLCG, meanEnergyLCG);
        double stdSpecificHeatLCG = calculateStdDev(specificHeatsLCG, meanSpecificHeatLCG);
        double stdEnergyGFSR = calculateStdDev(energiesGFSR, meanEnergyGFSR);
        double stdSpecificHeatGFSR = calculateStdDev(specificHeatsGFSR, meanSpecificHeatGFSR);

        double exactEnergy = -1.45306;
        double exactSpecificHeat = 1.49871;

        double deltaE_LCG = Math.abs(meanEnergyLCG - exactEnergy);
        double deltaC_LCG = Math.abs(meanSpecificHeatLCG - exactSpecificHeat);
        double deltaE_GFSR = Math.abs(meanEnergyGFSR - exactEnergy);
        double deltaC_GFSR = Math.abs(meanSpecificHeatGFSR - exactSpecificHeat);

        double ratioE_LCG = deltaE_LCG / stdEnergyLCG;
        double ratioC_LCG = deltaC_LCG / stdSpecificHeatLCG;
        double ratioE_GFSR = deltaE_GFSR / stdEnergyGFSR;
        double ratioC_GFSR = deltaC_GFSR / stdSpecificHeatGFSR;

        control.println(String.format("LCG Mean Energy per Spin: %.5f", meanEnergyLCG));
        control.println(String.format("LCG Mean Specific Heat: %.5f", meanSpecificHeatLCG));
        control.println(String.format("LCG Energy Std Dev: %.5f", stdEnergyLCG));
        control.println(String.format("LCG Specific Heat Std Dev: %.5f", stdSpecificHeatLCG));
        control.println(String.format("LCG Delta E: %.5f", deltaE_LCG));
        control.println(String.format("LCG Delta C: %.5f", deltaC_LCG));
        control.println(String.format("LCG Ratio E: %.5f", ratioE_LCG));
        control.println(String.format("LCG Ratio C: %.5f", ratioC_LCG));

        control.println(String.format("GFSR Mean Energy per Spin: %.5f", meanEnergyGFSR));
        control.println(String.format("GFSR Mean Specific Heat: %.5f", meanSpecificHeatGFSR));
        control.println(String.format("GFSR Energy Std Dev: %.5f", stdEnergyGFSR));
        control.println(String.format("GFSR Specific Heat Std Dev: %.5f", stdSpecificHeatGFSR));
        control.println(String.format("GFSR Delta E: %.5f", deltaE_GFSR));
        control.println(String.format("GFSR Delta C: %.5f", deltaC_GFSR));
        control.println(String.format("GFSR Ratio E: %.5f", ratioE_GFSR));
        control.println(String.format("GFSR Ratio C: %.5f", ratioC_GFSR));

        boolean biasedLCG = (ratioE_LCG > 1.0 || ratioC_LCG > 1.0);
        boolean biasedGFSR = (ratioE_GFSR > 1.0 || ratioC_GFSR > 1.0);

        control.println(String.format("LCG Biased: %b", biasedLCG));
        control.println(String.format("GFSR Biased: %b", biasedGFSR));
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
        SimulationControl.createApp(new IsingModelMetropolisApp());
    }
}

