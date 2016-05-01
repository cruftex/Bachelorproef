package desmedt.frederik.cachebenchmarking.generator;

import java.util.Random;

/**
 * A generator generating random numbers between some lower and upper bound.
 */
public class RandomNumberGenerator {

    private Random random = new Random();

    private int lower;
    private int upper;

    public RandomNumberGenerator(int lower, int upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public int next() {
        return lower + random.nextInt(upper);
    }
}
