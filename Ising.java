package final_project;
import java.awt.*;
import org.opensourcephysics.frames.*;

public abstract class Ising {
  public static final double criticalTemperature = 2.0/Math.log(1.0+Math.sqrt(2.0));
  public int L = 16;
  public int N = L*L;                 // number of spins
  public double temperature = criticalTemperature;
  public int mcs = 0;                 // number of MC moves per spin
  public int energy;
  public double energyAccumulator = 0;
  public double energySquaredAccumulator = 0;
  public int magnetization = 0;
  public double magnetizationAccumulator = 0;
  public double magnetizationSquaredAccumulator = 0;
  public int acceptedMoves = 0;
  public double[] w = new double[9]; // array to hold Boltzmann factors
  public LatticeFrame lattice;
  protected RandomGenerator randGen;
  
  
  public Ising(LatticeFrame displayFrame, long seed) {
    lattice = displayFrame;
    this.randGen = createRandomGenerator(seed);
    N = L*L;
    lattice.resizeLattice(L, L); // set lattice size
    lattice.setIndexedColor(1, Color.red);
    lattice.setIndexedColor(-1, Color.green);
    for(int i = 0;i<L;++i) {
      for(int j = 0;j<L;++j) {
        lattice.setValue(i, j, 1); // all spins up
      }
    }
    magnetization = N;
    energy = -2*N; // minimum energy
    resetData();
    w[8] = Math.exp(-8.0/temperature); // other array elements never occur for H = 0
    w[4] = Math.exp(-4.0/temperature);
  }
  
  protected abstract RandomGenerator createRandomGenerator(long seed);

  public double specificHeat() {
    double energySquaredAverage = energySquaredAccumulator/mcs;
    double energyAverage = energyAccumulator/mcs;
    double heatCapacity = energySquaredAverage-energyAverage*energyAverage;
    heatCapacity = heatCapacity/(temperature*temperature);
    return(heatCapacity/N);
  }

  public double susceptibility() {
    double magnetizationSquaredAverage = magnetizationSquaredAccumulator/mcs;
    double magnetizationAverage = magnetizationAccumulator/mcs;
    return(magnetizationSquaredAverage-Math.pow(magnetizationAverage, 2))/(temperature*N);
  }

  public void resetData() {
    mcs = 0;
    energyAccumulator = 0;
    energySquaredAccumulator = 0;
    magnetizationAccumulator = 0;
    magnetizationSquaredAccumulator = 0;
    acceptedMoves = 0;
  }

  public void doOneMCStep() {
    for(int k = 0;k<N;++k) {
      int i = (int) (randGen.nextRandom()*L);
      int j = (int) (randGen.nextRandom()*L);
      int dE = 2*lattice.getValue(i, j)*(lattice.getValue((i+1)%L, j)+lattice.getValue((i-1+L)%L, j)+lattice.getValue(i, (j+1)%L)+lattice.getValue(i, (j-1+L)%L));
      if((dE<=0)||(w[dE]>randGen.nextRandom())) {
        int newSpin = -lattice.getValue(i, j);
        lattice.setValue(i, j, newSpin);
        acceptedMoves++;
        energy += dE;
        magnetization += 2*newSpin;
      }
    }
    energyAccumulator += energy;
    energySquaredAccumulator += energy*energy;
    magnetizationAccumulator += magnetization;
    magnetizationSquaredAccumulator += magnetization*magnetization;
    mcs++;
  }
}
