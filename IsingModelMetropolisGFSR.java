package final_project;

import java.util.Random;

public class IsingModelMetropolisGFSR {
    private int L = 16;
    private int[][] spins;
    private double T;
    private GFSR gfsr;
    Random rand;

    public IsingModelMetropolisGFSR(long seed, double T) {
    	this.T = T;
        spins = new int[L][L];
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        Random rand = new Random(seed);

        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }

        gfsr = new GFSR(sequence, p, q);
        initializeSpins();
    }

    private void initializeSpins() {
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                spins[i][j] = 1;
            }
        }
    }

    public void runSimulation(int steps) {
        for (int step = 0; step < steps; step++) {
            for (int i = 0; i < L; i++) {
                for (int j = 0; j < L; j++) {
                    int x = (int) (gfsr.nextRandom() * L);
                    int y = (int) (gfsr.nextRandom() * L);
                    flipSpin(x, y);
                }
            }
        }
    }

    private void flipSpin(int x, int y) {
        int deltaE = 2 * spins[x][y] * (spins[(x + 1) % L][y] + spins[(x - 1 + L) % L][y] +
                                        spins[x][(y + 1) % L] + spins[x][(y - 1 + L) % L]);
        if (deltaE <= 0 || gfsr.nextRandom() < Math.exp(-deltaE / T)) {
            spins[x][y] = -spins[x][y];
        }
    }

    public double computeEnergy() {
        double energy = 0;
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < L; j++) {
                energy -= spins[i][j] * (spins[(i + 1) % L][j] + spins[i][(j + 1) % L]);
            }
        }
        return energy / (L * L);
    }

    public double computeSpecificHeat() {
        double energy = computeEnergy();
        double energySquared = energy * energy;
        return (energySquared - energy * energy) / (T * T);
    }
}
