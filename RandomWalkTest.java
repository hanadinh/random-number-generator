package final_project;

import java.util.*;

public class RandomWalkTest {
    private static final int NUM_QUADRANTS = 4;
    private static final double FAILURE_THRESHOLD = 7.815; // Chi-square threshold for 5% probability

    public static void main(String[] args) {
    	// ADJUST THE VARIABLES HERE
        testRandomNumberGenerators(4, 3, 10000); // 4 walkers, 3 runs, 100 steps
    }

    public static void testRandomNumberGenerators(int numWalkers, int numRuns, int numSteps) {
        // GFSR
    	Random rand = new Random();
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }
        
        // Initialize random generators
        RandomGenerator lcg = new LCG(12, 16807, 2147483647, 0);
        RandomGenerator gfsr = new GFSR(sequence, p, q);


        for (int i = 1; i <= numRuns; i++) {
            int[] quadrantCountsLCG = new int[NUM_QUADRANTS];
            int[] quadrantCountsGFSR = new int[NUM_QUADRANTS];

            simulateRandomWalks(numWalkers, lcg, quadrantCountsLCG, numSteps);
            simulateRandomWalks(numWalkers, gfsr, quadrantCountsGFSR, numSteps);

            double chiSquareLCG = calculateChiSquare(quadrantCountsLCG, numWalkers);
            double chiSquareGFSR = calculateChiSquare(quadrantCountsGFSR, numWalkers);
            
            System.out.println("Run " + i + " for LCG: " + chiSquareLCG);
            if (chiSquareLCG > FAILURE_THRESHOLD) {
            	System.out.println("Run " + i + " for LCG failed");
            } else {
            	System.out.println("Run " + i + " for LCG succeeded");
            }
            
            System.out.println("Run " + i + " for GFSR: " + chiSquareGFSR);
            if (chiSquareGFSR > FAILURE_THRESHOLD) {
            	System.out.println("Run " + i + " for GFSR failed");
            } else {
            	System.out.println("Run " + i + " for GFSR succeeded");
            }
            System.out.println();
        }


    }

    public static void simulateRandomWalks(int numWalkers, RandomGenerator generator, int[] quadrantCounts, int numSteps) {
        for (int j = 0; j < numWalkers; j++) {
            int x = 0;
            int y = 0;

            for (int step = 0; step < numSteps; step++) {
                int direction = (int) (generator.nextRandom() * 4); // 0: North, 1: East, 2: South, 3: West
                switch (direction) {
                    case 0 -> y++;
                    case 1 -> x++;
                    case 2 -> y--;
                    case 3 -> x--;
                }
            }

            if (x >= 0 && y >= 0) quadrantCounts[0]++; // First quadrant
            else if (x >= 0 && y < 0) quadrantCounts[1]++; // Second quadrant
            else if (x < 0 && y < 0) quadrantCounts[2]++; // Third quadrant
            else quadrantCounts[3]++; // Fourth quadrant
        }
    }

    public static double calculateChiSquare(int[] observed, int total) {
        double expected = (double) total / NUM_QUADRANTS;
        double chiSquare = 0;
        for (int count : observed) {
            chiSquare += Math.pow(count - expected, 2) / expected;
        }
        return chiSquare;
    }
}
