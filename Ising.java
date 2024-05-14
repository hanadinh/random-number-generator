package final_project;
import java.awt.*;
import org.opensourcephysics.frames.*;

public abstract class Ising {
  public static final double criticalTemperature = 2.0/Math.log(1.0+Math.sqrt(2.0));
  public int L = 32;
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

  public void initialize(int L, LatticeFrame displayFrame, long seed) {
    lattice = displayFrame;
    this.L = L;
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

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
