package final_project;

import org.opensourcephysics.frames.LatticeFrame;

public class IsingLCG extends Ising {
    public IsingLCG(LatticeFrame displayFrame, long seed) {
    	super(displayFrame, seed);
    }

    @Override
    protected RandomGenerator createRandomGenerator(long seed) {
        return new LCG(seed, 16807, 2147483647, 0);
    }
}
