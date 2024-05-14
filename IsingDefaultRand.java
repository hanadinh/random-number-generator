package final_project;

import java.util.Random;

import org.opensourcephysics.frames.LatticeFrame;

public class IsingDefaultRand extends Ising {
    public IsingDefaultRand(LatticeFrame displayFrame, long seed) {
        super(displayFrame, seed);
    }

    @Override
    protected RandomGenerator createRandomGenerator(long seed) {
        return new JavaRandom(seed);
    }

    private static class JavaRandom extends RandomGenerator {
        private Random random;

        public JavaRandom(long seed) {
            random = new Random(seed);
        }

        @Override
        public double nextRandom() {
            return random.nextDouble();
        }
    }
}
