package final_project;

import java.util.Random;

public class IsingModelMetropolisDefaultRand extends IsingModelMetropolis {
    public IsingModelMetropolisDefaultRand(long seed, double T) {
        super(seed, T);
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
