package final_project;

import java.util.Random;

import org.opensourcephysics.frames.LatticeFrame;

public class IsingGFSR extends Ising {
    public IsingGFSR(LatticeFrame displayFrame, long seed) {
    	super(displayFrame, seed);
    }

    @Override
    protected RandomGenerator createRandomGenerator(long seed) {
        int p = 250;
        int q = 103;
        int[] sequence = new int[p];
        Random rand = new Random(seed);

        for (int i = 0; i < p; i++) {
            sequence[i] = rand.nextInt(Integer.MAX_VALUE);
        }

        return new GFSR(sequence, p, q);
    }
}
