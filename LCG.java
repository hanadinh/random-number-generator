package final_project;

/*
 * Linear Congruential Generator
 * x_n = (16807 * x_(n-1)) mod (2^31 - 1)
 * x_n = (a * x_n−1 + c) mod m
 * x_n is the nth random number
 */

import java.util.*;
import java.math.BigInteger;

public class LCG extends RandomGenerator {
    private BigInteger seed;
    private BigInteger a;
    private BigInteger m;
    private BigInteger c;

    public LCG(long seed, long a, long m, long c) {
        this.seed = BigInteger.valueOf(seed);
        this.a = BigInteger.valueOf(a);
        this.m = BigInteger.valueOf(m);
        this.c = BigInteger.valueOf(c);
    }

    public double nextRandom() {
        seed = seed.multiply(a).add(c).mod(m);
        return seed.doubleValue() / m.doubleValue();
    }

    public static void main(String[] args) {
        Random rand = new Random();
        long seed = rand.nextLong();
        LCG lcg = new LCG(12, 16807, 2147483647, 0);
//        LCG lcg = new LCG(1, 3, 32, 4);
        HashSet<Double> set = new HashSet<>();
        while (true) {
        	double num = lcg.nextRandom();
            System.out.println(num);
            
            if (set.contains(num)) {
            	break;
            } else {
            	set.add(num);
            }
        }
    }
}