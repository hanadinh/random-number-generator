package final_project;

public class IsingModelMetropolisLCG extends IsingModelMetropolis {
    public IsingModelMetropolisLCG(long seed, double T) {
        super(seed, T);
    }

    @Override
    protected RandomGenerator createRandomGenerator(long seed) {
        return new LCG(seed, 16807, 2147483647, 0);
    }
}
