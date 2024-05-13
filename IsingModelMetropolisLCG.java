package final_project;

public class IsingModelMetropolisLCG {
	private int L = 16;
    private int[][] spins;
    private double T;
    private LCG lcg;

    public IsingModelMetropolisLCG(long seed, double T) {
        this.T = T;
        spins = new int[L][L];
        lcg = new LCG(seed, 16807, 2147483647, 0);
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
                    int x = (int) (lcg.nextRandom() * L);
                    int y = (int) (lcg.nextRandom() * L);
                    flipSpin(x, y);
                }
            }
        }
    }

    private void flipSpin(int x, int y) {
        int deltaE = 2 * spins[x][y] * (spins[(x + 1) % L][y] + spins[(x - 1 + L) % L][y] +
                                       spins[x][(y + 1) % L] + spins[x][(y - 1 + L) % L]);
        if (deltaE <= 0 || lcg.nextRandom() < Math.exp(-deltaE / T)) {
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

