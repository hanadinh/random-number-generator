package final_project;

import java.util.Random;

public class FillingSitesTest {
    public static double[] performFillingSitesTest(RandomGenerator generator, int latticeSize, int monteCarloSteps) {
        double[] unfilledFractions = new double[monteCarloSteps];

        for (int step = 0; step < monteCarloSteps; step++) {
            boolean[][] lattice = new boolean[latticeSize][latticeSize];
            int unfilledCount = 0;

            for (int t = 0; t < latticeSize * latticeSize; t++) {
                double xRandom = generator.nextRandom();
                double yRandom = generator.nextRandom();
                
                int x = (int) (xRandom * (latticeSize - 1)) + 1;
                int y = (int) (yRandom * (latticeSize - 1)) + 1;

                if (!lattice[x - 1][y - 1]) {
                    lattice[x - 1][y - 1] = true;
                } else {
                    unfilledCount++;
                }
            }

            unfilledFractions[step] = (double) unfilledCount / (latticeSize * latticeSize);
        }

        return unfilledFractions;
    }

    public static void main(String[] args) {
    	Random rand = new Random();
        // Parameters
        int latticeSizes[] = {10, 15, 20}; // L values
        int monteCarloSteps = 100; // Number of Monte Carlo steps per site
        
        // GFSR
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }

        // Initialize random generators
        RandomGenerator lcg = new LCG(12, 16807, 2147483647, 0);
        RandomGenerator gfsr = new GFSR(sequence, p, q);

        // Perform Filling Sites test for each lattice size
        for (int latticeSize : latticeSizes) {
            double[] lcgUnfilledFractions = performFillingSitesTest(lcg, latticeSize, monteCarloSteps);
            double[] gfsrUnfilledFractions = performFillingSitesTest(gfsr, latticeSize, monteCarloSteps);

            // Output results
            System.out.println("Results for Lattice Size " + latticeSize + ":");
            System.out.println("LCG Unfilled Fractions:");
            for (int i = 0; i < monteCarloSteps; i++) {
                System.out.println("Step " + i + ": " + lcgUnfilledFractions[i]);
            }

            System.out.println("\nGFSR Unfilled Fractions:");
            for (int i = 0; i < monteCarloSteps; i++) {
                System.out.println("Step " + i + ": " + gfsrUnfilledFractions[i]);
            }
        }
    }
}
