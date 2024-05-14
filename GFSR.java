package final_project;


/*
 * Generalized Feedback Shift Register algorithm for generating random numbers
 * x_n is the nth random number
 * Requires bit manipulation
 * x_n = x_n-103 ⊕ x_n-250
 */

import java.util.*;

public class GFSR extends RandomGenerator {
    private int[] sequence;
    private int p;
    private int q;
    private int currentIndex;

    public GFSR(int[] sequence, int p, int q) {
        this.sequence = sequence;
        this.p = p;
        this.q = q;
        this.currentIndex = 0;
    }

    public double nextRandom() {
        int j = (currentIndex < (p - q)) ? (currentIndex + q) : (currentIndex - p + q); 
        int randomNum = sequence[currentIndex] ^ sequence[j];
        currentIndex = (currentIndex + 1) % p;
        return (double) randomNum  / Integer.MAX_VALUE;
    }

    public static void main(String[] args) {
        // generate first p random integers by another random number generator
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        Random rand = new Random();

        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }

        GFSR gfsr = new GFSR(sequence, p, q);
        for (int i = 0; i < 10; i++) {
            System.out.println(gfsr.nextRandom());
        }
    }
}
