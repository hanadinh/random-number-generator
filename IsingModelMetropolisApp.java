package final_project;

import org.opensourcephysics.controls.*;
import org.opensourcephysics.frames.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

            IsingModelMetropolisLCG modelLCG = new IsingModelMetropolisLCG(seed, T_c);
            modelLCG.runSimulation(steps);
            double energyLCG = modelLCG.computeEnergy();
            double specificHeatLCG = modelLCG.computeSpecificHeat();
            energiesLCG.add(energyLCG);
            specificHeatsLCG.add(specificHeatLCG);
            energyFrame.append(0, i + 1, energyLCG);
            specificHeatFrame.append(0, i + 1, specificHeatLCG);

            IsingModelMetropolisGFSR modelGFSR = new IsingModelMetropolisGFSR(seed, T_c);
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
        double meanEnergyLCG = energiesLCG.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double meanSpecificHeatLCG = specificHeatsLCG.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double meanEnergyGFSR = energiesGFSR.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double meanSpecificHeatGFSR = specificHeatsGFSR.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double stdEnergyLCG = Math.sqrt(energiesLCG.stream().mapToDouble(e -> Math.pow(e - meanEnergyLCG, 2)).sum() / numRuns);
        double stdSpecificHeatLCG = Math.sqrt(specificHeatsLCG.stream().mapToDouble(c -> Math.pow(c - meanSpecificHeatLCG, 2)).sum() / numRuns);
        double stdEnergyGFSR = Math.sqrt(energiesGFSR.stream().mapToDouble(e -> Math.pow(e - meanEnergyGFSR, 2)).sum() / numRuns);
        double stdSpecificHeatGFSR = Math.sqrt(specificHeatsGFSR.stream().mapToDouble(c -> Math.pow(c - meanSpecificHeatGFSR, 2)).sum() / numRuns);

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

        System.out.printf("LCG Mean Energy per Spin: %.5f\n", meanEnergyLCG);
        System.out.printf("LCG Mean Specific Heat: %.5f\n", meanSpecificHeatLCG);
        System.out.printf("LCG Energy Std Dev: %.5f\n", stdEnergyLCG);
        System.out.printf("LCG Specific Heat Std Dev: %.5f\n", stdSpecificHeatLCG);
        System.out.printf("LCG Delta E: %.5f\n", deltaE_LCG);
        System.out.printf("LCG Delta C: %.5f\n", deltaC_LCG);
        System.out.printf("LCG Ratio E: %.5f\n", ratioE_LCG);
        System.out.printf("LCG Ratio C: %.5f\n", ratioC_LCG);

        System.out.printf("GFSR Mean Energy per Spin: %.5f\n", meanEnergyGFSR);
        System.out.printf("GFSR Mean Specific Heat: %.5f\n", meanSpecificHeatGFSR);
        System.out.printf("GFSR Energy Std Dev: %.5f\n", stdEnergyGFSR);
        System.out.printf("GFSR Specific Heat Std Dev: %.5f\n", stdSpecificHeatGFSR);
        System.out.printf("GFSR Delta E: %.5f\n", deltaE_GFSR);
        System.out.printf("GFSR Delta C: %.5f\n", deltaC_GFSR);
        System.out.printf("GFSR Ratio E: %.5f\n", ratioE_GFSR);
        System.out.printf("GFSR Ratio C: %.5f\n", ratioC_GFSR);

        boolean biasedLCG = (ratioE_LCG > 1.0 || ratioC_LCG > 1.0);
        boolean biasedGFSR = (ratioE_GFSR > 1.0 || ratioC_GFSR > 1.0);

        System.out.printf("LCG Biased: %b\n", biasedLCG);
        System.out.printf("GFSR Biased: %b\n", biasedGFSR);
    }

    public static void main(String[] args) {
        SimulationControl.createApp(new IsingModelMetropolisApp());
    }
}

